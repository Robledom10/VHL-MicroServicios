package com.hernandolopera.gallery_service.dto.request;

import com.hernandolopera.gallery_service.model.MediaType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatedMediaRequest {
    @NotBlank(message = "URL es requerida")
    private String url;

    @NotNull(message = "Tipo es requerido")
    private MediaType type;

    @NotNull(message = "Año es requerido")
    @Min(value = 2000, message = "Year must be greater than or equal to 2000")
    @Max(value = 2100, message = "Year must be less than or equal to 2100")
    private Integer year;
}
