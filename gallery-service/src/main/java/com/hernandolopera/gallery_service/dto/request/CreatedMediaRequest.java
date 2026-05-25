package com.hernandolopera.gallery_service.dto.request;

import com.hernandolopera.gallery_service.model.MediaType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatedMediaRequest {

    @NotNull(message = "Media type is required")
    private MediaType type;

    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Year must be greater than or equal to 2000")
    @Max(value = 2100, message = "Year must be less than or equal to 2100")
    private Integer year;

    @NotBlank(message = "Excursion is required")
    private String excursion;

    @NotBlank(message = "Location is required")
    private String location;
}