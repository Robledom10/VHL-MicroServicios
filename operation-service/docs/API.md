# Operation Service API

Base local: `http://localhost:8083`

## HU 2.1, 2.2, 2.3, 2.4, 2.5 y 2.6 - Paquetes turisticos

- `POST /paquetes`: registra un paquete con itinerario.
- `GET /paquetes`: consulta catalogo con filtros `categoria`, `destino`, `search`, `minPrice`, `maxPrice`, `status`, `page`, `size`, `sortBy`, `direction`.
- `GET /paquetes/{id}`: consulta un paquete por id.
- `PUT /paquetes/{id}`: edita datos e itinerario.
- `DELETE /paquetes/{id}`: eliminacion logica. Falla si hay reservas activas.

Ejemplo `POST /paquetes`:

```json
{
  "nombre": "Eje Cafetero Familiar",
  "categoria": "Familiar",
  "destino": "Quindio",
  "descripcion": "Paquete turistico de tres dias",
  "precioBase": 1250000,
  "cupoTotal": 20,
  "itinerario": [
    {
      "numeroDia": 1,
      "titulo": "Llegada y city tour",
      "descripcion": "Recepcion y recorrido guiado",
      "horaInicio": "09:00:00",
      "horaFin": "17:00:00"
    }
  ]
}
```

## HU 2.7 - Cupos

- `PUT /paquetes/{id}/cupos`: actualiza cupos y registra historial.
- `GET /paquetes/{id}/cupos/historial`: consulta historial de cambios.

```json
{
  "cupoTotal": 25,
  "motivo": "Aumento de disponibilidad del operador"
}
```

## HU 2.8 - Planes de precio

- `POST /planes-precio`
- `GET /planes-precio?idPaquete=1`
- `PUT /planes-precio/{id}`
- `DELETE /planes-precio/{id}`

## HU 2.9 - Proveedores turisticos

- `POST /proveedores`
- `GET /proveedores`
- `PUT /proveedores/{id}`
- `DELETE /proveedores/{id}`

## HU 2.10 - Seguros y coberturas

- `POST /seguros`
- `GET /seguros?idPaquete=1`
- `PUT /seguros/{id}`
- `DELETE /seguros/{id}`

## HU 2.11 - Perfil de organizacion

- `GET /configuracion`
- `PUT /configuracion`: crea o actualiza el unico perfil permitido.

## Errores

Las respuestas de error usan:

```json
{
  "timestamp": "2026-05-08T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "La solicitud tiene campos invalidos",
  "path": "/paquetes",
  "fields": {
    "nombre": "must not be blank"
  }
}
```
