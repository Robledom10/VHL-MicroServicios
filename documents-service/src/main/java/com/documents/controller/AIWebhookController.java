package com.documents.controller;

import com.documents.dto.AIValidationResultDTO;
import com.documents.service.AIResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ia")
@RequiredArgsConstructor
public class AIWebhookController {

    private final AIResultService service;

    @PostMapping("/result")
    public ResponseEntity<String> receiveResult(
            @RequestBody AIValidationResultDTO dto
    ) {

        service.processResult(dto);

        return ResponseEntity.ok(
                "Resultado recibido"
        );
    }
}
