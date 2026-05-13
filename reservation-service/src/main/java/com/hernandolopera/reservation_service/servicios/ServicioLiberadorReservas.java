package com.hernandolopera.reservation_service.servicios;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hernandolopera.reservation_service.entidades.EstadoReserva;
import com.hernandolopera.reservation_service.entidades.Reserva;
import com.hernandolopera.reservation_service.repositorios.RepositorioReserva;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ServicioLiberadorReservas {

        private final RepositorioReserva repositorioReserva;

        /**
         * Se ejecuta cada minuto.
         * Busca reservas BLOQUEADAS cuyo tiempo de pago expiró y las cancela,
         * dejando el paquete disponible para otros usuarios.
         */
        @Scheduled(fixedDelay = 60_000)
        public void liberarReservasExpiradas() {
                List<Reserva> expiradas = repositorioReserva.findByEstadoAndExpiresAtBefore(
                        EstadoReserva.BLOQUEADA, LocalDateTime.now());

                if (expiradas.isEmpty()) return;

                log.info("Liberando {} reserva(s) expirada(s)", expiradas.size());

                expiradas.forEach(reserva -> {
                        reserva.setEstado(EstadoReserva.CANCELADA);
                        reserva.setExpiresAt(null);
                        log.info("Reserva {} liberada por vencimiento de tiempo", reserva.getNumeroReserva());
                });

                repositorioReserva.saveAll(expiradas);
        }
}