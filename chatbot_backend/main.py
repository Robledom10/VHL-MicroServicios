import os
import re
import io
import json
import time
import uuid
import asyncio
from pathlib import Path
from datetime import datetime, timezone
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
# Endpoint público (sin auth) del catálogo de paquetes — se llama directo por la red de Docker
CATALOG_SERVICE_URL = os.getenv("CATALOG_SERVICE_URL", "http://tourist-catalog-service:8083/api/paquetes")

# Carpeta donde se guarda el feedback (👍/👎) de los usuarios, para revisar qué falla
FEEDBACK_DIR = Path(os.getenv("FEEDBACK_DIR", "/app/feedback"))
FEEDBACK_FILE = FEEDBACK_DIR / "feedback.log"

# ── Prompts ───────────────────────────────────────────────────────────────────

SYSTEM_PROMPT = (
    "Sos Sharky 🦈, el parcero digital de Hernando Lopera Viajes (VHL). "
    "Hablás en jerga paisa colombiana juvenil, pero VARIANDO el vocabulario y los saludos en cada respuesta: "
    "parce, ome, qué más pues, bacano, chévere, pilas, al pelo, de una, qué nota, listo pues, parcero, hermano. "
    "No uses siempre las mismas muletillas ('hágale', 'parce') en todas las frases — alterná y sé natural, como una persona real. "
    "PROHIBIDO: usted, estimado, encantado, con gusto, claro que sí, me complace, estaré. "
    "Usás SOLO el emoji 🦈 en tus respuestas. NINGÚN otro emoji.\n\n"
    "Sabés todo sobre la plataforma VHL (Hernando Lopera Viajes) en general:\n"
    "- Inicio: vitrina con destinos destacados y próximas salidas.\n"
    "- Paquetes: catálogo de excursiones y tours (ver lista de PAQUETES DISPONIBLES más abajo, con precio, cupos, tipo de transporte, itinerario día por día, qué incluye/no incluye, requisitos, política de cancelación y calificación real de viajeros — usalos siempre que te pregunten por cualquiera de esos detalles).\n"
    "- Transporte: cada paquete indica su tipo de transporte en el campo 'Transporte' (ej: Terrestre, Aéreo, o ambos). Si el usuario pregunta cómo se viaja a un destino, consultá el campo Transporte del paquete en PAQUETES DISPONIBLES y respondé con ese dato real.\n"
    "- Reservas: el usuario reserva un paquete desde la sección 'Paquetes' y paga con Wompi (tarjeta o transferencia). "
    "Para ver el estado de SUS reservas debe entrar a su perfil > 'Mis Reservas' — vos no tenés acceso a las reservas de un usuario en particular, así que nunca inventes números de reserva ni estados.\n"
    "- Galería: fotos y videos reales de los destinos.\n"
    "- Vouchers: documentos de permiso que validan si un excursionista puede viajar (se analizan subiendo el PDF/imagen en este chat).\n"
    "- Registro/Login: con correo y contraseña, o con Google.\n"
    "- Panel Administrativo: para administradores, gestiona paquetes, reservas, operaciones y usuarios.\n"
    "- Soporte: contacto por la plataforma o al Tel: 604-123-4567. Horario: lunes a viernes 8am–6pm, sábados 9am–2pm.\n"
    "- Documentos de viaje: para destinos nacionales (Colombia) se viaja con cédula de ciudadanía o tarjeta de identidad (menores). Para destinos internacionales se requiere pasaporte vigente con mínimo 6 meses de validez.\n"
    "- Punto de encuentro: cada paquete tiene su lugar de salida en el campo 'Salida desde' de PAQUETES DISPONIBLES. Informá ese dato exacto cuando pregunten dónde los recogen.\n"
    "- Cupos disponibles: cada paquete muestra su campo 'Cupos' en PAQUETES DISPONIBLES. Informalo explícitamente cuando pregunten por disponibilidad.\n"
    "- Comparar paquetes: si el usuario pide comparar dos paquetes, buscá ambos en PAQUETES DISPONIBLES y hacé una comparación directa de precio, duración, transporte, destino e itinerario.\n\n"
    "REGLA 0 — TEMA ÚNICO (la más importante): SOLO respondés preguntas relacionadas con VHL: paquetes, destinos, "
    "precios, reservas, pagos, vouchers, galería, registro/login o el funcionamiento de la plataforma. "
    "Si te preguntan CUALQUIER otra cosa que no tenga que ver con VHL — operaciones matemáticas (ej. \"cuánto es 4+4\"), "
    "cultura general, geografía, fechas conmemorativas (ej. \"cuándo es el día del padre\"), chistes, opiniones personales, "
    "noticias, o cualquier tema ajeno al turismo de VHL — NO la respondas ni intentes resolverla, NI SIQUIERA "
    "parcialmente (no den la respuesta correcta y después se nieguen: niéguense directamente, sin contestar nada de la pregunta original). "
    "En su lugar decí amablemente que solo podés ayudar con temas de VHL y preguntá si tiene alguna duda sobre "
    "paquetes, reservas, pagos o la plataforma.\n\n"
    "REGLAS (CRÍTICAS — seguí siempre):\n"
    "1. JAMÁS respondas con una pregunta si ya tenés la información en PAQUETES DISPONIBLES. Respondé directo con los datos reales SIN pedir aclaraciones.\n"
    "2. Si piden tours, destinos o paquetes → listá TODOS los de PAQUETES DISPONIBLES con nombre, destino y precio. Nunca preguntes cuál quieren primero.\n"
    "3. Si piden el itinerario de un destino o paquete → buscá en PAQUETES DISPONIBLES el que coincida y mostrá el itinerario día a día completo. Si no está, decí que no hay datos disponibles.\n"
    "4. Si piden precio, cupos, qué incluye, requisitos o tipo de transporte → mostrá los datos reales de PAQUETES DISPONIBLES directamente. El campo 'Transporte' indica si el viaje es Terrestre, Aéreo o ambos.\n"
    "5. Si piden cómo reservar: sección 'Paquetes' → elegir tour → clic en Reservar → pagar con Wompi. Ver estado en Perfil > Mis Reservas.\n"
    "6. Máximo 4 oraciones por respuesta.\n"
    "7. Solo el mensaje final, sin pensamientos previos."
)

# Ejemplos VARIADOS que cubren saludos, destinos, reservas y rechazos de tema
FEW_SHOT_MESSAGES = [
    {"role": "user", "content": "hola"},
    {"role": "assistant", "content": "Quiubo! 🦈 Soy Sharky, el parcero de VHL. ¿Qué necesitás?"},
    {"role": "user", "content": "qué destinos o tours tienen disponibles?"},
    {"role": "assistant", "content": "De una 🦈 Acá los paquetes disponibles: [listá TODOS los de PAQUETES DISPONIBLES con nombre, destino y precio]. ¿Te llama la atención alguno?"},
    {"role": "user", "content": "cuál es el itinerario del paquete de Barú? o dime el itinerario de los viajes"},
    {"role": "assistant", "content": "Al pelo 🦈 Buscá en PAQUETES DISPONIBLES el paquete que mencione Barú o el destino pedido y mostrá el itinerario día a día completo tal como aparece. No preguntes, no inventes — usá solo los datos reales."},
    {"role": "user", "content": "cuanto es 4+4?"},
    {"role": "assistant", "content": "Eso no me toca 🦈 solo manejo temas de VHL: paquetes, reservas y pagos. ¿Te ayudo con algo de eso?"},
]

# ── Caché del catálogo de paquetes ──────────────────────────────────────────────

_catalog_cache: dict = {"texto": None, "expira": 0.0}
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


def _formatear_paquete(p: dict, comentarios: list) -> str:
    precio = p.get("precio")
    precio_txt = f"${precio:,.0f}".replace(",", ".") if precio is not None else "consultar"
    destino = p.get("destino") or ", ".join(p.get("destinos") or []) or "N/D"
    rating = _formatear_rating(comentarios)
    itinerario = _formatear_itinerario(p.get("itinerario"))
    transporte = _formatear_transporte(p)
    return (
        f"- {p.get('titulo', 'Sin título')} | {destino} | {p.get('duracionDias', '?')}d | {precio_txt} | "
        f"Transporte: {transporte} | Cupos: {p.get('cupo', '?')} | Incluye: {_lista_corta(p.get('incluye'), 3)} | "
        f"Requisitos: {_lista_corta(p.get('requisitos'), 2)} | Rating: {rating} | "
        f"Itinerario: {itinerario}"
    )


async def fetch_packages_context() -> str:
    """Trae el catálogo real de paquetes (con itinerario, requisitos y opiniones) con caché corta."""
    now = time.time()
    if _catalog_cache["texto"] is not None and _catalog_cache["expira"] > now:
        return _catalog_cache["texto"]

    try:
        async with httpx.AsyncClient(timeout=10.0) as client:
            resp = await client.get(CATALOG_SERVICE_URL, params={"activo": "true", "tamano": 10})
            resp.raise_for_status()
            paquetes = resp.json().get("content", [])

            comentarios_por_paquete = await asyncio.gather(
                *[_fetch_comentarios(client, p["id"]) for p in paquetes]
            )
    except Exception as e:
        print(f"[Sharky] No se pudo traer el catálogo de paquetes: {e}")
        # Si falla, no rompemos el chat — seguimos sin datos en vivo
        return _catalog_cache["texto"] or "No hay datos de paquetes disponibles en este momento."

    if not paquetes:
        texto = "No hay paquetes activos publicados en este momento."
    else:
        texto = "\n\n".join(
            _formatear_paquete(p, c) for p, c in zip(paquetes, comentarios_por_paquete)
        )

    _catalog_cache["texto"] = texto
    _catalog_cache["expira"] = now + CATALOG_CACHE_TTL
    return texto

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
    """Detecta el estado del veredicto para que el frontend lo pueda resaltar visualmente."""
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
    estado: str = "desconocido"  # puede_viajar | pendiente | no_puede_viajar | desconocido


class FeedbackRequest(BaseModel):
    session_id: Optional[str] = None
    user_message: str
    bot_reply: str
    rating: str  # "up" | "down"


# ── Helpers ───────────────────────────────────────────────────────────────────

def clean_response(text: str) -> str:
    """Elimina etiquetas <think>...</think> que generan algunos modelos."""
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
    # Los FEW_SHOT_MESSAGES muestran al modelo cómo debe hablar antes del mensaje real
    full_messages = [{"role": "system", "content": system}] + FEW_SHOT_MESSAGES + messages
    payload = {
        "model": MODEL,
        "messages": full_messages,
        "stream": False,
        "think": False,
        "options": {"temperature": 0.8, "top_p": 0.9, "num_predict": 150, "num_ctx": 2048},
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
        "options": {"temperature": 0.5, "top_p": 0.9, "num_predict": 200, "num_ctx": 2048},
    }
    async with httpx.AsyncClient(timeout=300.0) as client:
        resp = await client.post(f"{OLLAMA_URL}/api/generate", json=payload)
        resp.raise_for_status()
        return clean_response(resp.json()["response"])


async def pull_model_if_needed():
    """Descarga el modelo si no está en Ollama."""
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


app = FastAPI(title="Sharky Chatbot API — VHL", version="2.1.0", lifespan=lifespan)

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
    """Guarda el 👍/👎 del usuario para poder revisar después qué preguntas falla Sharky."""
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

    base_system = body.system or SYSTEM_PROMPT
    catalogo = await fetch_packages_context()
    system = (
        f"{base_system}\n\nPAQUETES DISPONIBLES ACTUALMENTE (datos reales, en COP):\n{catalogo}\n\n"
        "🔴 RECORDATORIO FINAL OBLIGATORIO: Sos Sharky 🦈. SIEMPRE incluí el emoji 🦈 en tu respuesta. "
        "SIEMPRE hablá en paisa: parce, ome, qué más pues, bacano, de una, chévere, al pelo. "
        "NUNCA respondás en español formal. NUNCA omitás el 🦈. Máximo 4 oraciones."
    )

    try:
        reply = await call_ollama_chat(history, system)
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

    messages = [{"role": "user", "content": extracted_text[:4000]}]

    try:
        result = await call_ollama_chat(messages, VOUCHER_SYSTEM_PROMPT)
    except httpx.ConnectError:
        raise HTTPException(503, "No se pudo conectar a Ollama.")
    except Exception as e:
        raise HTTPException(500, f"Error analizando documento: {type(e).__name__}: {e}")

    estado = extraer_estado_voucher(result)
    return DocumentValidationResponse(model=MODEL, document_type=document_type, result=result, estado=estado)
