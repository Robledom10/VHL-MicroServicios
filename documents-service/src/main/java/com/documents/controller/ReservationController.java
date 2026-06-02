package com.documents.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.documents.service.ReservationFlowService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationFlowService service;

    @GetMapping
    public ResponseEntity<Boolean>
    canContinueReservation(

            @RequestParam Integer userId,

            @RequestParam Integer reservationId

    ) {

        return ResponseEntity.ok(
                service.canContinueReservation(
                        userId,
                        reservationId
                )
        );
    }
}