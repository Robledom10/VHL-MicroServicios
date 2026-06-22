package com.hernandolopera.operation_servicio.controlador;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hernandolopera.operation_servicio.transferencia.CatalogStatisticsResponse;
import com.hernandolopera.operation_servicio.servicio.StatisticsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/catalog")
    public CatalogStatisticsResponse getStatistics() {
        return statisticsService.getStatistics();
    }
}