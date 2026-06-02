package com.documents.controller;

import com.documents.dto.ContractAcceptanceDTO;
import com.documents.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService service;

    @GetMapping
    public ResponseEntity<String>
    getContract() {

        return ResponseEntity.ok(
                service.getContract()
        );
    }

    @PostMapping("/accept")
    public ResponseEntity<String>
    acceptContract(
            @RequestBody ContractAcceptanceDTO dto
    ) {

        service.acceptContract(dto);

        return ResponseEntity.ok(
                "Contrato aceptado"
        );
    }

    @GetMapping("/accepted")
    public ResponseEntity<Boolean>
    hasAccepted(
            @RequestParam Integer reservationId,
            @RequestParam Integer userId
    ) {

        return ResponseEntity.ok(
                service.hasAcceptedContract(
                        reservationId,
                        userId
                )
        );
    }
}