package com.documents.controller;

import com.documents.dto.TravelerFileDTO;
import com.documents.service.TravelerFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class TravelerFileController {

    private final TravelerFileService service;

    @GetMapping("/{userId}")
    public TravelerFileDTO getFile(
            @PathVariable Integer userId
    ) {

        return service.getTravelerFile(
                userId
        );
    }
}
