package com.hernandolopera.operation_servicio;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ControladorOperacionesIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void gestionaPaqueteConItinerarioCuposConsultaYEliminacionLogica() throws Exception {
        int idPaquete = crearPaquete();

        mockMvc.perform(get("/paquetes")
                .param("categoria", "Familiar")
                .param("ordenarPor", "precioBase")
                .param("direccion", "asc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(idPaquete));

        mockMvc.perform(put("/paquetes/{id}/cupos", idPaquete)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "cupoTotal": 25,
                      "motivo": "Aumento de disponibilidad"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cupoTotal").value(25))
            .andExpect(jsonPath("$.cupoDisponible").value(25));

        mockMvc.perform(get("/paquetes/{id}/cupos/historial", idPaquete))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].cupoAnterior").value(20))
            .andExpect(jsonPath("$[0].cupoNuevo").value(25));

        mockMvc.perform(delete("/paquetes/{id}", idPaquete))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/paquetes/{id}", idPaquete))
            .andExpect(status().isNotFound());
    }

    @Test
    void validaErroresDePaqueteYConsulta() throws Exception {
        mockMvc.perform(post("/paquetes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nombre": "",
                      "categoria": "Familiar",
                      "destino": "Quindio",
                      "descripcion": "Descripcion",
                      "precioBase": 0,
                      "cupoTotal": 0
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.campos.nombre").exists())
            .andExpect(jsonPath("$.campos.precioBase").exists())
            .andExpect(jsonPath("$.campos.cupoTotal").exists());

        mockMvc.perform(get("/paquetes")
                .param("ordenarPor", "campoInvalido"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.mensaje").value("Campo de ordenamiento no permitido: campoInvalido"));

        int idPaquete = crearPaquete();
        mockMvc.perform(put("/paquetes/{id}", idPaquete)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nombre": "Paquete con hora invalida",
                      "categoria": "Familiar",
                      "destino": "Quindio",
                      "descripcion": "Descripcion valida",
                      "precioBase": 1200000,
                      "cupoTotal": 10,
                      "itinerario": [{
                        "numeroDia": 1,
                        "titulo": "Actividad",
                        "descripcion": "Descripcion",
                        "horaInicio": "18:00:00",
                        "horaFin": "08:00:00"
                      }]
                    }
                    """))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.mensaje").value("La hora final del itinerario debe ser mayor a la hora inicial"));
    }

    @Test
    void gestionaPlanesProveedoresSegurosYConfiguracion() throws Exception {
        int idPaquete = crearPaquete();

        String respuestaPlan = mockMvc.perform(post("/planes-precio")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "idPaquete": %d,
                      "nombre": "Pago en cuotas",
                      "precio": 1300000,
                      "cuotas": 4,
                      "condiciones": "Cuotas mensuales"
                    }
                    """.formatted(idPaquete)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.idPaquete").value(idPaquete))
            .andReturn().getResponse().getContentAsString();
        int idPlan = leerId(respuestaPlan);

        mockMvc.perform(get("/planes-precio").param("idPaquete", String.valueOf(idPaquete)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(delete("/planes-precio/{id}", idPlan))
            .andExpect(status().isNoContent());

        String correo = "proveedor%s@vhltravel.com".formatted(System.nanoTime());
        String respuestaProveedor = mockMvc.perform(post("/proveedores")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nombre": "Transportes Andinos",
                      "tipoProveedor": "Transporte",
                      "nombreContacto": "Laura Perez",
                      "correo": "%s",
                      "telefono": "3001234567"
                    }
                    """.formatted(correo)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.correo").value(correo))
            .andReturn().getResponse().getContentAsString();
        int idProveedor = leerId(respuestaProveedor);

        mockMvc.perform(post("/proveedores")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nombre": "Proveedor duplicado",
                      "tipoProveedor": "Transporte",
                      "nombreContacto": "Otra Persona",
                      "correo": "%s",
                      "telefono": "3007654321"
                    }
                    """.formatted(correo)))
            .andExpect(status().isConflict());

        mockMvc.perform(delete("/proveedores/{id}", idProveedor))
            .andExpect(status().isNoContent());

        String respuestaSeguro = mockMvc.perform(post("/seguros")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "idPaquete": %d,
                      "nombre": "Seguro viajero basico",
                      "detalleCobertura": "Asistencia medica",
                      "montoCobertura": 5000000
                    }
                    """.formatted(idPaquete)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.idPaquete").value(idPaquete))
            .andReturn().getResponse().getContentAsString();
        int idSeguro = leerId(respuestaSeguro);

        mockMvc.perform(delete("/seguros/{id}", idSeguro))
            .andExpect(status().isNoContent());

        mockMvc.perform(put("/configuracion")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nombreOrganizacion": "VHL Travel",
                      "correo": "operaciones@vhltravel.com",
                      "telefono": "6011234567",
                      "direccion": "Bogota, Colombia",
                      "logoBase64": null
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombreOrganizacion").value("VHL Travel"));

        mockMvc.perform(get("/configuracion"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.correo").value("operaciones@vhltravel.com"));
    }

    private int crearPaquete() throws Exception {
        String respuesta = mockMvc.perform(post("/paquetes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nombre": "Eje Cafetero Familiar %s",
                      "categoria": "Familiar",
                      "destino": "Quindio",
                      "descripcion": "Paquete turistico de tres dias",
                      "precioBase": 1250000,
                      "cupoTotal": 20,
                      "itinerario": [{
                        "numeroDia": 1,
                        "titulo": "Llegada y recorrido",
                        "descripcion": "Recepcion y recorrido guiado",
                        "horaInicio": "09:00:00",
                        "horaFin": "17:00:00"
                      }]
                    }
                    """.formatted(System.nanoTime())))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.itinerario", hasSize(1)))
            .andReturn().getResponse().getContentAsString();
        return leerId(respuesta);
    }

    private int leerId(String respuesta) throws Exception {
        JsonNode json = objectMapper.readTree(respuesta);
        return json.get("id").asInt();
    }
}
