package com.hernandolopera.auth_service.controller.statistics;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hernandolopera.auth_service.dto.response.statistics.UserStatisticsResponse;
import com.hernandolopera.auth_service.service.statistics.StatisticsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/users")
    public ResponseEntity<UserStatisticsResponse>
            getUserStatistics() {

        return ResponseEntity.ok(
                statisticsService.getUserStatistics());
    }
}