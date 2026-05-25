package com.hernandolopera.reservation_service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hernandolopera.reservation_service.entidades.Reserva;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MYSQL",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        }
)
@AutoConfigureMockMvc
@Transactional
class ControladorReservaIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void hu31ReservarPaqueteTuristicoCreaReservaPendiente() throws Exception {
        JsonNode reservaCreada = crearReserva(42L, 2, "1234.56");

        mockMvc.perform(get("/api/v1/reservas/{id}", reservaCreada.get("id").asLong()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservaCreada.get("id").asLong()))
                .andExpect(jsonPath("$.numeroReserva").value(reservaCreada.get("numeroReserva").asText()))
                .andExpect(jsonPath("$.idPaquete").value(1))
                .andExpect(jsonPath("$.idUsuario").value(42))
                .andExpect(jsonPath("$.cantidadPasajeros").value(2))
                .andExpect(jsonPath("$.precioTotal").value(1234.56))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.pagoVerificado").value(false));
    }

    @Test
    void hu32GestionarReservasRegistradasPermiteConsultarListarYActualizar() throws Exception {
        JsonNode reservaCreada = crearReserva(42L, 2, "1234.56");
        Long reservaId = reservaCreada.get("id").asLong();
        String numeroReserva = reservaCreada.get("numeroReserva").asText();

        mockMvc.perform(get("/api/v1/reservas/numero/{numeroReserva}", numeroReserva))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservaId))
                .andExpect(jsonPath("$.numeroReserva").value(numeroReserva));

        mockMvc.perform(get("/api/v1/reservas/usuario/{idUsuario}", 42L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(reservaId));

        mockMvc.perform(get("/api/v1/reservas/estado/{estado}", "PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));

        mockMvc.perform(put("/api/v1/reservas/{id}", reservaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "precioTotal": 1400.00,
                                  "cantidadPasajeros": 3,
                                  "fechaInicioViaje": "2026-06-15T08:00:00",
                                  "fechaFinViaje": "2026-06-22T18:00:00",
                                  "estado": "PENDIENTE",
                                  "pagoVerificado": true,
                                  "notas": "Cambio solicitado por el usuario"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precioTotal").value(1400.00))
                .andExpect(jsonPath("$.cantidadPasajeros").value(3))
                .andExpect(jsonPath("$.fechaInicioViaje").value("2026-06-15T08:00:00"))
                .andExpect(jsonPath("$.fechaFinViaje").value("2026-06-22T18:00:00"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.pagoVerificado").value(true))
                .andExpect(jsonPath("$.notas").value("Cambio solicitado por el usuario"));
    }

    @Test
    void hu33ConfirmarReservaValidaPagoViajerosYActualizaEstado() throws Exception {
        JsonNode reservaCreada = crearReserva(10L, 2, "1500.00");
        Long reservaId = reservaCreada.get("id").asLong();

        registrarViajero(reservaId, "Juan", "Perez", "12345678", "juan.perez@email.com")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
        registrarViajero(reservaId, "Maria", "Gomez", "87654321", "maria.gomez@email.com")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));

        mockMvc.perform(post("/api/v1/confirmaciones/confirmar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idReserva": %d,
                                  "numeroReserva": "%s",
                                  "cantidadPasajeros": 2,
                                  "montoPago": 1500.00,
                                  "pagoVerificado": true,
                                  "referenciaTransaccion": "TXN-TEST-001",
                                  "fechaPago": "2026-05-01T15:00:00",
                                  "metodoPago": "TARJETA_CREDITO"
                                }
                                """.formatted(reservaId, reservaCreada.get("numeroReserva").asText())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Reserva confirmada exitosamente"))
                .andExpect(jsonPath("$.estadoReserva").value("CONFIRMADA"));

        mockMvc.perform(get("/api/v1/reservas/{id}", reservaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"))
                .andExpect(jsonPath("$.pagoVerificado").value(true))
                .andExpect(jsonPath("$.fechaConfirmacion").exists());

        mockMvc.perform(get("/api/v1/reservas/usuario/{idUsuario}/confirmadas", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(reservaId))
                .andExpect(jsonPath("$[0].estado").value("CONFIRMADA"));
    }

    @Test
    void reservasConfirmadasConFechaFinVencidaQuedanComoPasadas() throws Exception {
        JsonNode reservaCreada = crearReserva(77L, 2, "1500.00");
        Long reservaId = reservaCreada.get("id").asLong();

        mockMvc.perform(put("/api/v1/reservas/{id}", reservaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fechaInicioViaje": "2020-01-10T08:00:00",
                                  "fechaFinViaje": "2020-01-15T18:00:00",
                                  "estado": "CONFIRMADA",
                                  "pagoVerificado": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));

        mockMvc.perform(get("/api/v1/reservas/usuario/{idUsuario}/pasadas", 77L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(reservaId))
                .andExpect(jsonPath("$[0].estado").value("PASADA"));

        mockMvc.perform(get("/api/v1/reservas/estado/{estado}", "PASADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(reservaId))
                .andExpect(jsonPath("$[0].estado").value("PASADA"));
    }

    @Test
    void hu33ConfirmarReservaRechazaPagoNoVerificadoMontoIncorrectoYViajerosIncompletos() throws Exception {
        JsonNode reservaCreada = crearReserva(10L, 2, "1500.00");
        Long reservaId = reservaCreada.get("id").asLong();
        String numeroReserva = reservaCreada.get("numeroReserva").asText();

        confirmarReserva(reservaId, numeroReserva, 2, "1500.00", false)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("El pago no ha sido verificado"));

        confirmarReserva(reservaId, numeroReserva, 2, "1200.00", true)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("El monto del pago no coincide"));

        confirmarReserva(reservaId, numeroReserva, 2, "1500.00", true)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("No hay suficientes viajeros registrados"));
    }

    @Test
    void hu34RegistrarViajerosPermiteConsultarActualizarYRespetaCantidadMaxima() throws Exception {
        JsonNode reservaCreada = crearReserva(10L, 2, "1500.00");
        Long reservaId = reservaCreada.get("id").asLong();

        String respuestaViajero = registrarViajero(reservaId, "Juan", "Perez", "12345678", "juan.perez@email.com")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.idReserva").value(reservaId))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long viajeroId = objectMapper.readTree(respuestaViajero).get("idViajero").asLong();

        mockMvc.perform(get("/api/v1/viajeros/{id}", viajeroId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.datosCompletos").value(true))
                .andExpect(jsonPath("$.documentosVerificados").value(false));

        mockMvc.perform(put("/api/v1/viajeros/{id}", viajeroId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Juan Carlos",
                                  "apellido": "Perez Lopez",
                                  "documento": "12345678",
                                  "tipoDocumento": "CEDULA",
                                  "fechaNacimiento": "1990-05-15",
                                  "email": "juancarlos.perez@email.com",
                                  "telefono": "3005555555",
                                  "genero": "MASCULINO",
                                  "nacionalidad": "COLOMBIA"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Carlos"))
                .andExpect(jsonPath("$.email").value("juancarlos.perez@email.com"));

        registrarViajero(reservaId, "Maria", "Gomez", "87654321", "maria.gomez@email.com")
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/viajeros/reserva/{idReserva}", reservaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        registrarViajero(reservaId, "Carlos", "Ruiz", "11223344", "carlos.ruiz@email.com")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.detalleError").exists());
    }

    @Test
    void hu35CancelarReservaCambiaEstadoACancelada() throws Exception {
        JsonNode reservaCreada = crearReserva(42L, 2, "1234.56");
        Long reservaId = reservaCreada.get("id").asLong();

        mockMvc.perform(delete("/api/v1/reservas/{id}", reservaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservaId))
                .andExpect(jsonPath("$.estado").value("CANCELADA"));

        mockMvc.perform(get("/api/v1/reservas/{id}", reservaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
    }

    @Test
    void endpointsRetornan404CuandoNoExisteElRecurso() throws Exception {
        mockMvc.perform(get("/api/v1/reservas/{id}", 99999L))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        mockMvc.perform(get("/api/v1/viajeros/{id}", 99999L))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    private JsonNode crearReserva(Long idUsuario, Integer cantidadPasajeros, String precioTotal) throws Exception {
        Reserva reserva = Reserva.builder()
                .idPaquete(1L)
                .idUsuario(idUsuario)
                .cantidadPasajeros(cantidadPasajeros)
                .precioTotal(new BigDecimal(precioTotal))
                .fechaInicioViaje(LocalDateTime.now().plusDays(7))
                .fechaFinViaje(LocalDateTime.now().plusDays(14))
                .notas("Reserva para prueba de integracion")
                .build();

        String respuestaCreacion = mockMvc.perform(post("/api/v1/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reserva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.numeroReserva").isString())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(respuestaCreacion);
    }

    private ResultActions registrarViajero(
            Long reservaId, String nombre, String apellido, String documento, String email) throws Exception {
        return mockMvc.perform(post("/api/v1/viajeros/reserva/{idReserva}", reservaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nombre": "%s",
                          "apellido": "%s",
                          "documento": "%s",
                          "tipoDocumento": "CEDULA",
                          "fechaNacimiento": "1990-05-15",
                          "email": "%s",
                          "telefono": "3001234567",
                          "genero": "MASCULINO",
                          "nacionalidad": "COLOMBIA"
                        }
                        """.formatted(nombre, apellido, documento, email)));
    }

    private ResultActions confirmarReserva(
            Long reservaId, String numeroReserva, Integer cantidadPasajeros, String montoPago, Boolean pagoVerificado)
            throws Exception {
        return mockMvc.perform(post("/api/v1/confirmaciones/confirmar")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "idReserva": %d,
                          "numeroReserva": "%s",
                          "cantidadPasajeros": %d,
                          "montoPago": %s,
                          "pagoVerificado": %s,
                          "referenciaTransaccion": "TXN-TEST-001",
                          "fechaPago": "2026-05-01T15:00:00",
                          "metodoPago": "TARJETA_CREDITO"
                        }
                        """.formatted(reservaId, numeroReserva, cantidadPasajeros, montoPago, pagoVerificado)));
    }
}
