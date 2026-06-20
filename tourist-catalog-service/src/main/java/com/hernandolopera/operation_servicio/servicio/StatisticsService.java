package com.hernandolopera.operation_servicio.servicio;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.hernandolopera.operation_servicio.transferencia.CatalogStatisticsResponse;
import com.hernandolopera.operation_servicio.repositorio.RepositorioPaqueteTuristico;
import com.hernandolopera.operation_servicio.repositorio.RepositorioProveedorTuristico;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final RepositorioPaqueteTuristico paqueteRepository;
    private final RepositorioProveedorTuristico proveedorRepository;

    public CatalogStatisticsResponse getStatistics() {

        return new CatalogStatisticsResponse(

                paqueteRepository.count(),
                paqueteRepository.countByActivo(true),
                paqueteRepository.countByActivo(false),

                proveedorRepository.count(),
                proveedorRepository.countByActivo(true),
                proveedorRepository.countByActivo(false),

                paqueteRepository.averagePrice() != null
                        ? paqueteRepository.averagePrice()
                        : BigDecimal.ZERO,

                paqueteRepository.maxPrice() != null
                        ? paqueteRepository.maxPrice()
                        : BigDecimal.ZERO,

                paqueteRepository.minPrice() != null
                        ? paqueteRepository.minPrice()
                        : BigDecimal.ZERO
        );
    }
}