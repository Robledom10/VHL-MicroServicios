import os
import re
import io
import json
import time
import uuid
import asyncio
from pathlib import Path
from datetime import date, datetime, timezone
from typing import Optional
from contextlib import asynccontextmanager




import httpx
import fitz  # PyMuPDF
from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel

# ── Configuración via entorno ─────────────────────────────────────────────────

OLLAMA_URL = os.getenv("OLLAMA_URL", "http://localhost:11434")
MODEL = os.getenv("OLLAMA_MODEL", "qwen3:1.7b")
CATALOG_SERVICE_URL = os.getenv("CATALOG_SERVICE_URL", "http://tourist-catalog-service:8083/api/paquetes")
OPERATION_SERVICE_URL = os.getenv("OPERATION_SERVICE_URL", "http://operation-service:8086/api/v1/operaciones/viajes")
RESERVATION_SERVICE_URL = os.getenv("RESERVATION_SERVICE_URL", "http://reservation-service:8082/api/v1/reservas")

FEEDBACK_DIR = Path(os.getenv("FEEDBACK_DIR", "/app/feedback"))
FEEDBACK_FILE = FEEDBACK_DIR / "feedback.log"

# ── Prompts ───────────────────────────────────────────────────────────────────

SYSTEM_PROMPT = (
    "Sos Sharky 🦈, parcero digital de VHL (Hernando Lopera Viajes). "
    "Hablás en paisa colombiano juvenil variando: parce, ome, bacano, chévere, al pelo, de una, qué nota. "
    "PROHIBIDO: usted, estimado, con gusto, claro que sí. Solo emoji 🦈, ningún otro.\n\n"
    "VHL es una agencia de viajes. Los datos REALES de cada paquete están en PAQUETES DISPONIBLES (más abajo). "
    "Reservas: para reservar el usuario va a 'Paquetes' → Reservar → paga con Wompi. "
    "Para ver sus reservas personales entra a 'Perfil > Mis Reservas' — vos no tenés acceso a ellas, nunca inventes números ni estados.\n\n"
    "REGLAS (obligatorias):\n"
    "R0: Solo respondés temas de VHL. Cualquier otra cosa (mates, cultura, chistes) — rechazala amablemente.\n"
    "R1: Nunca hagas preguntas si ya tenés el dato en PAQUETES DISPONIBLES. Respondé directo.\n"
    "R2: Cuántos días dura → usá EXACTAMENTE el valor de DURACION_EXACTA del paquete. NUNCA inventes ni calcules.\n"
    "R3: Cuándo sale → usá el valor de FECHAS_VIAJE del paquete. Si dice N/D, no hay fechas programadas.\n"
    "R4: Cuántos cupos → usá el valor de CUPOS del paquete.\n"
    "R5: Dónde se sale → usá el valor de SALE_DESDE del paquete.\n"
    "R6: Si piden lista de paquetes → listá TODOS de PAQUETES DISPONIBLES con nombre, destino y precio.\n"
    "R7: Máximo 4 oraciones. Solo el mensaje final, sin etiquetas <think>."
)

FEW_SHOT_MESSAGES = [
    {"role": "user", "content": "hola"},
    {"role": "assistant", "content": "Quiubo! 🦈 Soy Sharky, el parcero de VHL. ¿Qué necesitás?"},
    {"role": "user", "content": "cuántos días dura el paquete de San Andres?"},
    {"role": "assistant", "content": "Al pelo 🦈 Buscá DURACION_EXACTA del paquete San Andres en PAQUETES DISPONIBLES y respondé ese número exacto de días."},
    {"role": "user", "content": "cuándo sale el próximo viaje a Coveñas?"},
    {"role": "assistant", "content": "Ome 🦈 Buscá FECHAS_VIAJE del paquete Coveñas en PAQUETES DISPONIBLES y respondé con esas fechas reales."},
    {"role": "user", "content": "cuánto es 4+4?"},
    {"role": "assistant", "content": "Eso no me toca 🦈 Solo manejo temas de VHL. ¿Te ayudo con paquetes, reservas o pagos?"},
]

# ── Caché del catálogo ────────────────────────────────────────────────────────

_catalog_cache: dict = {"texto": None, "paquetes": [], "viajes": [], "expira": 0.0}
CATALOG_CACHE_TTL = 300  # segundos


def _lista_corta(valores: Optional[list], maximo: int = 4) -> str:
    valores = valores or []
    if not valores:
        return "N/D"
    texto = ", ".join(valores[:maximo])
    if len(valores) > maximo:
        texto += f" (+{len(valores) - maximo} más)"
    return texto


def _formatear_itinerario(itinerario: Optional[list]) -> str:
    if not itinerario:
        return "N/D"
    dias = sorted(itinerario, key=lambda it: it.get("numeroDia", 0))
    return "; ".join(f"Día {it.get('numeroDia')}: {it.get('titulo')}" for it in dias)


def _formatear_rating(comentarios: list) -> str:
    if not comentarios:
        return "sin opiniones todavía"
    promedio = sum(c.get("puntaje", 0) for c in comentarios) / len(comentarios)
    texto = f"{promedio:.1f}/5 ({len(comentarios)} opiniones)"
    mejor = max(comentarios, key=lambda c: c.get("puntaje", 0))
    if mejor.get("comentario"):
        texto += f' — ej: "{mejor["comentario"][:100]}"'
    return texto


async def _fetch_comentarios(client: httpx.AsyncClient, id_paquete) -> list:
    try:
        resp = await client.get(f"{CATALOG_SERVICE_URL}/{id_paquete}/comentarios")
        resp.raise_for_status()
        return resp.json()
    except Exception:
        return []


def _formatear_transporte(p: dict) -> str:
    tipos = p.get("tiposTransporte") or []
    singular = p.get("tipoTransporte") or ""
    todos = list(dict.fromkeys(t for t in ([singular] + tipos) if t))
    return ", ".join(todos) if todos else "N/D"


def _formatear_fechas_viaje(id_paquete, viajes: list) -> str:
    hoy = str(date.today())
    proximas = [
        v for v in viajes
        if str(v.get("idPaquete")) == str(id_paquete)
        and v.get("fechaRegreso", "") >= hoy
    ]
    if not proximas:
        return "N/D"
    return " | ".join(
        f"Salida: {v['fechaSalida']} → Regreso: {v['fechaRegreso']} ({v.get('estado', 'programado')})"
        for v in proximas[:3]
    )


def _formatear_paquete(p: dict, comentarios: list, viajes: list) -> str:
    precio = p.get("precio")
    precio_txt = f"${precio:,.0f}".replace(",", ".") if precio is not None else "consultar"
    destino = p.get("destino") or ", ".join(p.get("destinos") or []) or "N/D"
    rating = _formatear_rating(comentarios)
    itinerario = _formatear_itinerario(p.get("itinerario"))
    transporte = _formatear_transporte(p)
    lugar_salida = p.get("lugarSalida") or "N/D"
    fechas = _formatear_fechas_viaje(p.get("id"), viajes)
    duracion = p.get("duracionDias", "?")

    return (
        f"PAQUETE:{p.get('titulo','?')} | DURACION_EXACTA:{duracion}dias | PRECIO:{precio_txt} | "
        f"DESTINO:{destino} | TRANSPORTE:{transporte} | SALE_DESDE:{lugar_salida} | "
        f"CUPOS:{p.get('cupo','?')} | FECHAS_VIAJE:{fechas} | "
        f"INCLUYE:{_lista_corta(p.get('incluye'),3)} | REQUISITOS:{_lista_corta(p.get('requisitos'),2)} | "
        f"RATING:{rating} | ITINERARIO:{itinerario}"
    )


async def _fetch_viajes() -> list:
    try:
        async with httpx.AsyncClient(timeout=8.0) as client:
            resp = await client.get(OPERATION_SERVICE_URL, params={"pagina": 0, "tamano": 1000})
            resp.raise_for_status()
            data = resp.json()
            return data.get("content", data) if isinstance(data, dict) else data
    except Exception as e:
        print(f"[Sharky] No se pudo traer viajes de operaciones: {e}")
        return []


async def fetch_reservas() -> list:
    try:
        async with httpx.AsyncClient(timeout=8.0) as client:
            resp = await client.get(RESERVATION_SERVICE_URL, params={"pagina": 0, "tamano": 1000})
            resp.raise_for_status()
            data = resp.json()
            return data.get("content", data) if isinstance(data, dict) else data
    except Exception as e:
        print(f"[Sharky] No se pudo traer reservas: {e}")
        return []


async def _refrescar_catalogo():
    """Fetch y cachea texto formateado + datos crudos de paquetes y viajes."""
    now = time.time()
    if _catalog_cache["texto"] is not None and _catalog_cache["expira"] > now:
        return

    try:
        async with httpx.AsyncClient(timeout=10.0) as client:
            resp = await client.get(CATALOG_SERVICE_URL, params={"activo": "true", "tamano": 50})
            resp.raise_for_status()
            paquetes = resp.json().get("content", [])

            comentarios_por_paquete, viajes = await asyncio.gather(
                asyncio.gather(*[_fetch_comentarios(client, p["id"]) for p in paquetes]),
                _fetch_viajes(),
            )
    except Exception as e:
        print(f"[Sharky] No se pudo traer el catálogo de paquetes: {e}")
        return

    texto = "\n\n".join(
        _formatear_paquete(p, c, viajes)
        for p, c in zip(paquetes, comentarios_por_paquete)
    ) if paquetes else "No hay paquetes activos publicados en este momento."

    _catalog_cache["texto"] = texto
    _catalog_cache["paquetes"] = paquetes
    _catalog_cache["viajes"] = viajes
    _catalog_cache["expira"] = now + CATALOG_CACHE_TTL


async def fetch_packages_context() -> str:
    """Texto formateado del catálogo para el system prompt de Ollama."""
    await _refrescar_catalogo()
    return _catalog_cache["texto"] or "No hay datos de paquetes disponibles en este momento."


async def fetch_catalog_raw() -> tuple[list, list]:
    """Datos crudos: (paquetes, viajes) para respuestas directas."""
    await _refrescar_catalogo()
    return _catalog_cache["paquetes"], _catalog_cache["viajes"]


# ── Respuestas directas (sin Ollama) ─────────────────────────────────────────

import unicodedata

def _norm(texto: str) -> str:
    """Normaliza a minúsculas sin tildes para comparaciones."""
    return unicodedata.normalize("NFD", texto.lower()).encode("ascii", "ignore").decode()


def _fmt_fecha_raw(val) -> str:
    """Convierte fecha de la API (array [2025,6,20,...] o ISO string '2025-06-20T...') a 'DD/MM/YYYY'."""
    if not val:
        return ""
    if isinstance(val, list):
        try:
            return f"{int(val[2]):02d}/{int(val[1]):02d}/{int(val[0])}"
        except (IndexError, TypeError, ValueError):
            return str(val)
    if isinstance(val, str):
        try:
            d = datetime.fromisoformat(val[:10])
            return f"{d.day:02d}/{d.month:02d}/{d.year}"
        except Exception:
            return val[:10]
    return str(val)


def _detectar_intencion(mensaje: str) -> Optional[str]:
    msg = _norm(mensaje)
    patrones = {
        "lista":      ["que paquetes", "cuales paquetes", "que destinos", "que viajes", "que tienen disponible",
                       "que opciones", "que ofrecen", "ver paquetes", "mostrar paquetes", "todos los paquetes",
                       "lista de paquetes", "cuantos paquetes"],
        "duracion":   ["cuanto dura", "cuantos dias", "dias dura", "duracion", "cuantas noches", "dias tiene",
                       "cuanto tiempo dura", "cuanto tiempo es", "cuanto tiempo tiene", "dias son",
                       "cuantos dias son", "cuanto tiempo", "dias tiene el paquete", "dias del paquete",
                       "cuantos dias tiene", "cuantos dias dura"],
        "itinerario": ["itinerario", "actividades", "que vamos a hacer", "que hacemos", "plan del viaje",
                       "dia a dia", "programa del viaje", "que se hace", "dias del viaje"],
        "no_incluye": ["no incluye", "no incluido", "que no incluye", "que no trae", "que no viene",
                       "que no cubre", "que no tiene incluido", "exclusiones", "que no esta incluido",
                       "no incluyen", "que no incluyen"],
        "incluye":    ["que incluye", "incluye", "incluido", "que trae incluido", "que viene incluido",
                       "que esta incluido", "que cubre"],
        "fechas":     ["cuando sale", "fecha de salida", "fechas", "cuando viajan", "proxima salida",
                       "cuando hay salida", "cuando es el viaje", "cuando parte"],
        "precio":     ["cuanto cuesta", "precio", "valor", "cuanto vale", "costo", "cuanto es", "que precio tiene"],
        "salida":     ["donde sale", "punto de salida", "desde donde", "sale desde", "de donde salen",
                       "lugar de salida"],
        "cupos":      ["cupos", "cuantos cupos", "disponibilidad", "hay cupos", "quedan cupos",
                       "cupos disponibles"],
        "transporte":   ["transporte", "como viajan", "en que viajan", "en bus", "en avion", "medio de transporte",
                         "como van", "van en bus", "van en avion", "van en barco", "tipo de transporte",
                         "que transporte", "como se viaja", "como se va"],
        "requisitos":   ["requisitos", "que necesito", "documentos", "que debo llevar", "que necesitan",
                         "que piden"],
        "mis_reservas": ["mis reservas", "ver mis reservas", "estado de mi reserva", "mis viajes reservados",
                         "reservas que tengo", "consultar reserva", "donde veo mis reservas",
                         "tengo una reserva", "mi reserva"],
        "como_reservar":["como reservo", "quiero reservar", "hacer una reserva", "como hago la reserva",
                         "proceso de reserva", "pasos para reservar", "como se reserva", "reservar un paquete",
                         "me interesa reservar", "quiero apartar"],
        "que_reservas": ["que reservas hay", "que viajes hay", "que salidas hay", "hay reservas",
                         "ver reservas", "reservas disponibles", "viajes disponibles", "viajes programados",
                         "proximos viajes", "proximas salidas", "que viajes tienen", "que salidas tienen",
                         "que reservaciones hay", "cuales reservas", "que reservas tienen",
                         "hay proximas", "proximas fechas", "fechas disponibles", "salidas disponibles",
                         "viajes hay programados", "hay viajes programados", "ver las reservas",
                         "reservas", "info reservas", "informacion de reservas", "sobre reservas",
                         "saber de reservas", "quiero saber de reservas", "info de reservas",
                         "saber de la reserva", "ver la reserva", "la reserva de", "reserva de"],
    }
    for intencion, palabras in patrones.items():
        if any(p in msg for p in palabras):
            return intencion
    return None


_STOP_WORDS = {
    "para", "como", "cuanto", "cuantos", "cuales", "cuando", "donde", "tiene",
    "tienen", "dura", "dias", "viaje", "viajes", "paquete", "paquetes", "plan",
    "cual", "este", "quiero", "saber", "dime", "dices", "puedo", "puedes",
}

def _detectar_paquete(mensaje: str, paquetes: list) -> Optional[dict]:
    msg = _norm(mensaje)
    # Palabras significativas del mensaje — se limpian signos de puntuación (ej: "andres?" → "andres")
    palabras_msg = {re.sub(r"\W", "", w) for w in msg.split()}
    palabras_msg = {w for w in palabras_msg if len(w) > 3 and w not in _STOP_WORDS}

    candidatos = []
    for p in paquetes:
        titulo = _norm(p.get("titulo") or "")
        destino = _norm(p.get("destino") or "")
        destinos = [_norm(d) for d in (p.get("destinos") or [])]

        # Match exacto del título completo
        if titulo and titulo in msg:
            return p

        # Match exacto de destino
        if destino and len(destino) > 3 and destino in msg:
            return p
        for d in destinos:
            if d and len(d) > 3 and d in msg:
                return p

        # Cuántas palabras del usuario aparecen en el título del paquete (sin puntuación)
        palabras_titulo = {re.sub(r"\W", "", w) for w in titulo.split()}
        if palabras_msg:
            hits = palabras_msg & palabras_titulo
            if hits:
                # Score = proporción de las palabras del usuario que coinciden
                score = len(hits) / len(palabras_msg)
                candidatos.append((score, len(hits), p))

    if candidatos:
        candidatos.sort(key=lambda x: (-x[0], -x[1]))
        mejor_score, mejor_hits, mejor_p = candidatos[0]
        # Aceptar si al menos 1 palabra clave coincide con score >= 0.3, o 2+ palabras
        if mejor_hits >= 2 or mejor_score >= 0.3:
            return mejor_p
    return None


def _responder_directo(mensaje: str, paquetes: list, viajes: list, reservas: Optional[list] = None, contexto: str = "", intent_override: Optional[str] = None) -> Optional[str]:
    """Retorna respuesta en texto plano si la pregunta es factual, o None para pasar a Ollama."""
    if not paquetes:
        return None

    intencion = _detectar_intencion(mensaje)
    if intencion is None:
        intencion = intent_override  # usar intent del turno anterior si el usuario solo respondió el paquete
    if intencion is None:
        return None

    # Lista general — no necesita identificar paquete específico
    if intencion == "lista":
        lineas = []
        for p in paquetes:
            titulo = p.get("titulo", "?")
            destino = p.get("destino") or ", ".join(p.get("destinos") or []) or "?"
            precio = p.get("precio")
            precio_txt = f"${precio:,.0f}".replace(",", ".") if precio else "consultar"
            duracion = p.get("duracionDias", "?")
            lineas.append(f"• {titulo} → {destino} | {duracion} días | {precio_txt}")
        return (
            "Qué más pues 🦈 estos son todos los paquetes de VHL:\n"
            + "\n".join(lineas)
            + "\n\n¿Cuál te llama la atención, parce?"
        )

    # Reservas — no necesitan un paquete específico para responder
    if intencion == "que_reservas":
        lista_reservas = reservas or []
        if not lista_reservas:
            return (
                "Ome 🦈 no hay reservas registradas en VHL por el momento. "
                "¿Querés ser el primero en reservar, parce?"
            )
        # Si mencionaron un paquete específico, filtrar por ese paquete
        paquete_filtro = _detectar_paquete(mensaje, paquetes)
        contexto_txt = " en VHL"
        if paquete_filtro:
            tit_filtro = paquete_filtro.get("titulo", "")
            id_filtro = str(paquete_filtro.get("id", ""))
            norm_filtro = _norm(tit_filtro)
            filtradas = [
                r for r in lista_reservas
                if (r.get("idPaquete") and str(r.get("idPaquete")) == id_filtro)
                or (r.get("paqueteNombre") and _norm(r.get("paqueteNombre")) == norm_filtro)
            ]
            if not filtradas:
                return (
                    f"Ome 🦈 no hay reservas registradas para **{tit_filtro}** todavía. "
                    f"¿Querés ser el primero en reservar, parce?"
                )
            lista_reservas = filtradas
            contexto_txt = f" para **{tit_filtro}**"
        lineas = []
        for r in lista_reservas[:10]:
            nombre_r = r.get("paqueteNombre") or "Paquete"
            destino_r = r.get("destino") or "?"
            estado_r = r.get("estadoDescripcion") or r.get("estado") or "?"
            fi = r.get("fechaViaje") or ""
            ff = _fmt_fecha_raw(r.get("fechaFinViaje"))
            fecha_txt = f"📅 {fi}" + (f" → {ff}" if ff else "") if fi else "📅 fecha por confirmar"
            pasajeros = r.get("cantidadPasajeros") or r.get("personas") or "?"
            pago = "✅ pago verificado" if r.get("pagoVerificado") else "⏳ pago pendiente"
            codigo = r.get("numeroReserva") or ""
            lineas.append(
                f"• **{nombre_r}** ({destino_r})\n"
                f"  {fecha_txt} | Estado: {estado_r} | {pasajeros} pax | {pago}"
                + (f" | {codigo}" if codigo else "")
            )
        total = len(lista_reservas)
        encabezado = f"Bacano 🦈 hay **{total} reserva{'s' if total != 1 else ''}**{contexto_txt}"
        if total > 10:
            encabezado += " (mostrando las primeras 10)"
        return (
            encabezado + ":\n\n"
            + "\n\n".join(lineas)
            + "\n\n¿Querés hacer una nueva reserva? Andá a **Paquetes** → **Reservar**, parce!"
        )

    if intencion == "mis_reservas":
        return (
            "Bacano 🦈 para ver tus reservas personales andá a **Perfil → Mis Reservas** en la app. "
            "Ahí encontrás el estado de todos tus viajes: pendiente, confirmado o cancelado. "
            "¡Yo no tengo acceso a reservas privadas, eso es tuyo, parce!"
        )

    if intencion == "como_reservar":
        paquete_reserva = _detectar_paquete(mensaje, paquetes)
        if paquete_reserva:
            tit = paquete_reserva.get("titulo", "?")
            precio = paquete_reserva.get("precio")
            precio_txt = f"${precio:,.0f}".replace(",", ".") if precio else "consultá el precio"
            cupo = paquete_reserva.get("cupo", "?")
            return (
                f"De una 🦈 para reservar **{tit}** ({precio_txt}, {cupo} cupos) seguí estos pasos:\n"
                f"1. Andá a **Paquetes** en la app\n"
                f"2. Buscá **{tit}** y tocá **Reservar**\n"
                f"3. Completá tus datos y pagá con **Wompi**\n\n"
                f"¡Así de fácil, parce! ¿Necesitás algo más?"
            )
        # Sin paquete específico → mostrar reservas actuales + instrucciones
        lista_reservas = reservas or []
        partes = []

        if lista_reservas:
            total = len(lista_reservas)
            lineas_r = []
            for r in lista_reservas[:8]:
                nombre_r = r.get("paqueteNombre") or "Paquete"
                estado_r = r.get("estadoDescripcion") or r.get("estado") or "?"
                fi = r.get("fechaViaje") or ""
                ff = _fmt_fecha_raw(r.get("fechaFinViaje"))
                fecha_txt = f"📅 {fi}" + (f" → {ff}" if ff else "") if fi else "📅 ?"
                pasajeros = r.get("cantidadPasajeros") or r.get("personas") or "?"
                codigo = r.get("numeroReserva") or ""
                lineas_r.append(
                    f"• **{nombre_r}** | {fecha_txt} | {estado_r} | {pasajeros} pax"
                    + (f" | {codigo}" if codigo else "")
                )
            encabezado_r = f"📋 **Reservas actuales en VHL ({total} en total):**"
            if total > 8:
                encabezado_r += " _(mostrando 8)_"
            partes.append(encabezado_r + "\n" + "\n".join(lineas_r))

        partes.append(
            "✅ **¿Querés hacer una nueva reserva?**\n"
            "1. Andá a **Paquetes** en la app\n"
            "2. Elegí el paquete y tocá **Reservar**\n"
            "3. Completá tus datos y pagá con **Wompi**\n\n"
            "¿De cuál paquete te interesa, parce?"
        )
        return "De una 🦈\n\n" + "\n\n".join(partes)

    # Para el resto necesitamos saber de qué paquete habla el usuario
    paquete = _detectar_paquete(mensaje, paquetes)

    # Si no se detectó en el mensaje actual, buscar en el contexto histórico (mensajes anteriores)
    if paquete is None and contexto:
        paquete = _detectar_paquete(contexto, paquetes)

    if paquete is None:
        nombres = [p.get("titulo", "?") for p in paquetes[:6]]
        lista = ", ".join(nombres)
        if len(paquetes) > 6:
            lista += f" y {len(paquetes) - 6} más"
        return f"Ome 🦈 ¿de cuál paquete me estás preguntando? Los que tenemos son: {lista}. ¡Dime el nombre, parce!"

    titulo = paquete.get("titulo", "?")

    if intencion == "duracion":
        duracion = paquete.get("duracionDias", "?")
        return f"Al pelo 🦈 el paquete **{titulo}** dura exactamente **{duracion} días**. ¿Te interesa reservar, parce?"

    if intencion == "itinerario":
        itinerario = paquete.get("itinerario") or []
        duracion = paquete.get("duracionDias", "?")
        if not itinerario:
            return (
                f"Ome 🦈 el paquete **{titulo}** dura **{duracion} días** pero aún no tiene "
                f"el itinerario detallado cargado. ¡Contactá a VHL para más info!"
            )
        dias = sorted(itinerario, key=lambda it: it.get("numeroDia", 0))
        lineas = [f"📍 Día {it.get('numeroDia')}: {it.get('titulo', '')}" for it in dias]
        nota = ""
        if isinstance(duracion, int) and len(dias) < duracion:
            nota = f"\n_(itinerario parcial: {len(dias)} de {duracion} días cargados)_"
        return (
            f"Chévere 🦈 el paquete **{titulo}** tiene **{duracion} días** en total. Aquí el itinerario:\n"
            + "\n".join(lineas)
            + nota
            + "\n\n¿Te animás, parce?"
        )

    if intencion == "incluye":
        incluye = paquete.get("incluye") or []
        if not incluye:
            return f"Ome 🦈 no tengo el detalle de qué incluye **{titulo}** por acá. ¡Confirmá directamente con VHL!"
        return f"De una 🦈 el paquete **{titulo}** incluye: {', '.join(incluye)}. ¿Querés reservar, parce?"

    if intencion == "no_incluye":
        no_incluye = paquete.get("noIncluye") or []
        if not no_incluye:
            return f"Ome 🦈 no tengo registrado qué no incluye **{titulo}**. ¡Confirmá directamente con VHL!"
        return f"Bacano 🦈 el paquete **{titulo}** NO incluye: {', '.join(no_incluye)}. ¿Querés saber algo más, parce?"

    if intencion == "fechas":
        fechas_txt = _formatear_fechas_viaje(paquete.get("id"), viajes)
        if fechas_txt != "N/D":
            lineas = fechas_txt.split(" | ")
            return (
                f"Bacano 🦈 las próximas salidas del paquete **{titulo}** son:\n"
                + "\n".join(lineas)
                + "\n\n¿Te apuntás, parce?"
            )
        # Sin viajes en operation-service → buscar en reservas por ID o nombre
        id_paq = str(paquete.get("id", ""))
        nombre_paq_norm = _norm(titulo)
        reservas_paq = [
            r for r in (reservas or [])
            if (r.get("idPaquete") and str(r.get("idPaquete")) == id_paq)
            or (r.get("paqueteNombre") and _norm(r.get("paqueteNombre")) == nombre_paq_norm)
        ]
        if reservas_paq:
            lineas_f = []
            for r in reservas_paq[:4]:
                fi = r.get("fechaViaje") or ""
                ff_raw = r.get("fechaFinViaje")
                ff = _fmt_fecha_raw(ff_raw)
                estado_r = r.get("estadoDescripcion") or r.get("estado") or "?"
                if fi and ff:
                    lineas_f.append(f"• Salida: {fi} → Regreso: {ff} ({estado_r})")
                elif fi:
                    lineas_f.append(f"• Salida: {fi} ({estado_r})")
            if lineas_f:
                return (
                    f"De una 🦈 las fechas registradas para **{titulo}** son:\n"
                    + "\n".join(lineas_f)
                    + "\n\n¿Te apuntás, parce?"
                )
        return (
            f"Ome 🦈 el paquete **{titulo}** no tiene fechas de viaje programadas por el momento. "
            f"¡Estate pendiente o escríbenos directamente!"
        )

    if intencion == "precio":
        precio = paquete.get("precio")
        precio_txt = f"${precio:,.0f}".replace(",", ".") if precio else "consultar con VHL"
        return (
            f"Al pelo 🦈 el paquete **{titulo}** tiene un precio de **{precio_txt}**. "
            f"Para reservar vas a 'Paquetes' → Reservar → pagás con Wompi. ¿Te late, parce?"
        )

    if intencion == "salida":
        lugar = paquete.get("lugarSalida") or "N/D"
        if lugar == "N/D":
            return f"Ome 🦈 no tengo registrado el punto de salida de **{titulo}**. ¡Confirmá con VHL directamente!"
        return f"De una 🦈 el paquete **{titulo}** sale desde **{lugar}**. ¿Algo más que quieras saber, parce?"

    if intencion == "cupos":
        cupo = paquete.get("cupo", "?")
        return f"Chévere 🦈 el paquete **{titulo}** tiene **{cupo} cupos** disponibles. ¡No dejés pasar el tuyo, parce!"

    if intencion == "transporte":
        transporte = _formatear_transporte(paquete)
        if transporte == "N/D":
            return f"Ome 🦈 el paquete **{titulo}** no tiene transporte asignado todavía. ¡Confirmá directamente con VHL!"
        return f"Al pelo 🦈 para el paquete **{titulo}** el transporte es: **{transporte}**. ¿Algo más, parce?"

    if intencion == "requisitos":
        requisitos = paquete.get("requisitos") or []
        if not requisitos:
            return f"Ome 🦈 no tengo requisitos específicos para **{titulo}** por acá. ¡Confirmá directamente con VHL!"
        return f"Bacano 🦈 para el paquete **{titulo}** necesitás: {', '.join(requisitos)}. ¿Te preparás y reservamos, parce?"

    return None


VOUCHER_SYSTEM_PROMPT = (
    "Sos Sharky, parcero de VHL. Te van a mandar el texto extraído de un voucher, permiso de viaje "
    "o comprobante relacionado. Tu tarea es EVALUAR DE VERDAD las condiciones reales que aparecen en el "
    "texto (fechas, estado de pago, firmas, restricciones, permisos de menores, vigencia, datos faltantes) "
    "y dar un veredicto honesto. NO fuerces un PUEDE o NO PUEDE viajar si la información es incompleta, "
    "ambigua o no se puede confirmar — en ese caso el estado es PENDIENTE.\n\n"
    "ESTADOS POSIBLES (elegí el que de verdad corresponda según lo que leíste):\n\n"
    "✅ PUEDE VIAJAR — toda la info necesaria está presente, vigente, pagada y sin restricciones.\n"
    "Formato: '✅ PUEDE VIAJAR — [nombre del titular o \"titular no identificado\"]' seguido de la razón concreta en paisa.\n\n"
    "⏳ PENDIENTE — falta algo para poder confirmar (ej: no se ve la fecha, no es claro si está pagado, "
    "falta firma o permiso de un menor, el documento está parcialmente ilegible, falta un dato puntual).\n"
    "Formato: '⏳ PENDIENTE — [nombre del titular o \"titular no identificado\"]' seguido de QUÉ falta o qué hay que confirmar, en paisa.\n\n"
    "❌ NO PUEDE VIAJAR — hay una condición real y concreta que lo impide (vencido, no pagado, restricción "
    "explícita, menor sin permiso firmado, cancelado).\n"
    "Formato: '❌ NO PUEDE VIAJAR — [nombre del titular o \"titular no identificado\"]' seguido de la razón concreta en paisa.\n\n"
    "Si el documento NO es un voucher/permiso/comprobante de viaje → respondé exactamente: "
    "'Ome esto no parece un voucher de viaje, subí el documento correcto parce! 🤔'\n\n"
    "SOLO el veredicto con la explicación de 1-2 frases. Sin pensamientos. Sin repetir el texto completo del documento."
)


def extraer_estado_voucher(texto: str) -> str:
    primera_linea = texto.strip().split("\n", 1)[0]
    if "PENDIENTE" in primera_linea or "⏳" in primera_linea:
        return "pendiente"
    if "NO PUEDE VIAJAR" in primera_linea or "❌" in primera_linea:
        return "no_puede_viajar"
    if "PUEDE VIAJAR" in primera_linea or "✅" in primera_linea:
        return "puede_viajar"
    return "desconocido"

# ── Estado en memoria (sesiones) ──────────────────────────────────────────────

sessions: dict[str, list[dict]] = {}
session_state: dict[str, dict] = {}  # guarda el último intent pendiente por sesión

# ── Modelos Pydantic ──────────────────────────────────────────────────────────

class ChatRequest(BaseModel):
    message: str
    session_id: Optional[str] = None
    system: Optional[str] = None


class ChatResponse(BaseModel):
    session_id: str
    model: str
    reply: str


class DocumentValidationResponse(BaseModel):
    model: str
    document_type: str
    result: str
    estado: str = "desconocido"


class FeedbackRequest(BaseModel):
    session_id: Optional[str] = None
    user_message: str
    bot_reply: str
    rating: str  # "up" | "down"


# ── Helpers ───────────────────────────────────────────────────────────────────

def clean_response(text: str) -> str:
    text = re.sub(r"<think>.*?</think>", "", text, flags=re.DOTALL)
    return text.strip()


def extract_text_from_pdf(file_bytes: bytes) -> str:
    doc = fitz.open(stream=file_bytes, filetype="pdf")
    pages = [page.get_text() for page in doc]
    doc.close()
    return "\n".join(pages).strip()


def extract_text_from_image(file_bytes: bytes) -> str:
    try:
        import pytesseract
        from PIL import Image
        image = Image.open(io.BytesIO(file_bytes))
        return pytesseract.image_to_string(image, lang="spa").strip()
    except Exception:
        return ""


async def call_ollama_chat(messages: list[dict], system: str) -> str:
    full_messages = [{"role": "system", "content": system}] + FEW_SHOT_MESSAGES + messages
    payload = {
        "model": MODEL,
        "messages": full_messages,
        "stream": False,
        "think": False,
        "options": {"temperature": 0.7, "top_p": 0.9, "num_predict": 150, "num_ctx": 3072},
    }
    async with httpx.AsyncClient(timeout=600.0) as client:
        resp = await client.post(f"{OLLAMA_URL}/api/chat", json=payload)
        resp.raise_for_status()
        return clean_response(resp.json()["message"]["content"])


async def call_ollama_voucher(text: str, system: str) -> str:
    payload = {
        "model": MODEL,
        "messages": [
            {"role": "system", "content": system},
            {"role": "user", "content": text[:2500]},
        ],
        "stream": False,
        "think": False,
        "options": {"temperature": 0.3, "top_p": 0.9, "num_predict": 120, "num_ctx": 1024},
    }
    async with httpx.AsyncClient(timeout=300.0) as client:
        resp = await client.post(f"{OLLAMA_URL}/api/chat", json=payload)
        resp.raise_for_status()
        return clean_response(resp.json()["message"]["content"])


async def call_ollama_generate(prompt: str) -> str:
    payload = {
        "model": MODEL,
        "prompt": prompt,
        "stream": False,
        "think": False,
        "options": {"temperature": 0.5, "top_p": 0.9, "num_predict": 300, "num_ctx": 4096},
    }
    async with httpx.AsyncClient(timeout=300.0) as client:
        resp = await client.post(f"{OLLAMA_URL}/api/generate", json=payload)
        resp.raise_for_status()
        return clean_response(resp.json()["response"])


async def pull_model_if_needed():
    async with httpx.AsyncClient(timeout=30.0) as client:
        try:
            r = await client.get(f"{OLLAMA_URL}/api/tags")
            models = [m["name"] for m in r.json().get("models", [])]
            already_exists = MODEL in models
            if not already_exists:
                print(f"[Sharky] Descargando modelo {MODEL}...")
                async with httpx.AsyncClient(timeout=600.0) as dl:
                    await dl.post(f"{OLLAMA_URL}/api/pull", json={"name": MODEL, "stream": False})
                print(f"[Sharky] Modelo {MODEL} listo.")
            else:
                print(f"[Sharky] Modelo {MODEL} ya disponible.")
        except Exception as e:
            print(f"[Sharky] No se pudo verificar/descargar modelo: {e}")


# ── App ───────────────────────────────────────────────────────────────────────

@asynccontextmanager
async def lifespan(_app: FastAPI):
    print(f"[Sharky] Backend arrancando — Ollama: {OLLAMA_URL} — Modelo: {MODEL}")
    await pull_model_if_needed()
    print("[Sharky] Listo para chatear, parcero!")
    yield
    print("[Sharky] Backend apagandose...")


app = FastAPI(title="Sharky Chatbot API — VHL", version="2.2.0", lifespan=lifespan)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ── Endpoints ─────────────────────────────────────────────────────────────────

@app.get("/health")
async def health():
    return {"status": "ok", "model": MODEL, "service": "chatbot-service"}


@app.post("/feedback")
async def feedback(body: FeedbackRequest):
    entry = {
        "timestamp": datetime.now(timezone.utc).isoformat(),
        "session_id": body.session_id,
        "user_message": body.user_message,
        "bot_reply": body.bot_reply,
        "rating": body.rating,
    }
    try:
        FEEDBACK_DIR.mkdir(parents=True, exist_ok=True)
        with open(FEEDBACK_FILE, "a", encoding="utf-8") as f:
            f.write(json.dumps(entry, ensure_ascii=False) + "\n")
    except Exception as e:
        print(f"[Sharky] No se pudo guardar el feedback: {e}")
        raise HTTPException(500, "No se pudo guardar el feedback.")
    return {"status": "ok"}


@app.post("/chat", response_model=ChatResponse)
async def chat(body: ChatRequest):
    sid = body.session_id or str(uuid.uuid4())
    history = sessions.setdefault(sid, [])
    history.append({"role": "user", "content": body.message})

    # Intentar respuesta directa desde la API (sin llamar a Ollama)
    paquetes, viajes = await fetch_catalog_raw()
    intencion_detectada = _detectar_intencion(body.message)

    # Contexto histórico: últimos mensajes del usuario para detectar el paquete mencionado antes
    mensajes_usuario = [m["content"] for m in history[-8:] if m["role"] == "user"]
    contexto_historico = " ".join(mensajes_usuario[:-1])  # todo menos el actual

    # Intent pendiente: si el bot preguntó "¿de cuál paquete?" en el turno anterior,
    # y el usuario ahora solo responde con el nombre del paquete, reusar el intent anterior
    state = session_state.setdefault(sid, {"last_intent": None})
    intent_override = None
    if intencion_detectada:
        state["last_intent"] = intencion_detectada  # guardar intent para posible follow-up
    elif state["last_intent"]:
        # Verificar que el turno anterior del bot fue la pregunta "¿de cuál paquete?"
        mensajes_bot = [m["content"] for m in history[-4:] if m["role"] == "assistant"]
        ultimo_bot = mensajes_bot[-1] if mensajes_bot else ""
        if "de cual paquete" in _norm(ultimo_bot):
            intent_override = state["last_intent"]

    reservas = await fetch_reservas() if (intencion_detectada or intent_override) in ("que_reservas", "como_reservar", "fechas") else None

    reply = _responder_directo(body.message, paquetes, viajes, reservas, contexto=contexto_historico, intent_override=intent_override)

    # Limpiar intent pendiente si se resolvió exitosamente
    if reply is not None and intent_override:
        state["last_intent"] = None

    if reply is None:
        # Pregunta conversacional — caída a Ollama
        base_system = body.system or SYSTEM_PROMPT
        catalogo = await fetch_packages_context()
        system = (
            f"{base_system}\n\n"
            f"PAQUETES DISPONIBLES ACTUALMENTE (datos 100% reales — usá SOLO estos datos, nunca inventes):\n"
            f"{catalogo}\n\n"
            "🔴 RECORDATORIO FINAL: Sos Sharky 🦈. SIEMPRE incluí el emoji 🦈. "
            "SIEMPRE hablá en paisa: parce, ome, qué más pues, bacano, de una, chévere, al pelo. "
            "NUNCA inventes datos — si no está en PAQUETES DISPONIBLES, decí que no tenés esa info. "
            "Máximo 5 oraciones."
        )
        try:
            reply = await call_ollama_chat(history[-6:], system)
        except httpx.ConnectError:
            raise HTTPException(503, "No se pudo conectar a Ollama.")
        except httpx.HTTPStatusError as e:
            raise HTTPException(502, f"Ollama respondio con error: {e.response.status_code}")
        except Exception as e:
            raise HTTPException(500, f"Error interno: {type(e).__name__}: {e}")

    history.append({"role": "assistant", "content": reply})

    if len(history) > 20:
        sessions[sid] = history[-20:]

    return ChatResponse(session_id=sid, model=MODEL, reply=reply)


@app.post("/validate-document", response_model=DocumentValidationResponse)
async def validate_document(file: UploadFile = File(...)):
    file_bytes = await file.read()
    filename = (file.filename or "").lower()
    content_type = file.content_type or ""

    extracted_text = ""
    document_type = "desconocido"

    if filename.endswith(".pdf") or "pdf" in content_type:
        document_type = "PDF"
        try:
            extracted_text = extract_text_from_pdf(file_bytes)
        except Exception as e:
            raise HTTPException(422, f"No se pudo leer el PDF: {e}")

    elif any(filename.endswith(ext) for ext in [".jpg", ".jpeg", ".png", ".webp"]):
        document_type = "Imagen"
        extracted_text = extract_text_from_image(file_bytes)
        if not extracted_text:
            return DocumentValidationResponse(
                model=MODEL,
                document_type=document_type,
                estado="pendiente",
                result=(
                    "Parcero, no pude leer el texto de esa imagen. "
                    "Subí el documento en formato PDF o asegurate de que la imagen "
                    "sea clara y con buena resolucion. Cualquier duda me avisas!"
                ),
            )
    else:
        raise HTTPException(415, "Formato no soportado. Subi un PDF o imagen (JPG, PNG, WEBP).")

    if not extracted_text:
        return DocumentValidationResponse(
            model=MODEL,
            document_type=document_type,
            estado="pendiente",
            result=(
                "Mano, el documento parece estar vacio o no tiene texto legible. "
                "Revisa que el PDF no este escaneado como imagen sin OCR, "
                "o intenta con una foto clara del documento."
            ),
        )

    try:
        result = await call_ollama_voucher(extracted_text, VOUCHER_SYSTEM_PROMPT)
    except httpx.ConnectError:
        raise HTTPException(503, "No se pudo conectar a Ollama.")
    except Exception as e:
        raise HTTPException(500, f"Error analizando documento: {type(e).__name__}: {e}")

    estado = extraer_estado_voucher(result)
    return DocumentValidationResponse(model=MODEL, document_type=document_type, result=result, estado=estado)
