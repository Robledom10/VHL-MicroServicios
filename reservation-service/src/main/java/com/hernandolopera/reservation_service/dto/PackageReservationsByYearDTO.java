package com.hernandolopera.reservation_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageReservationsByYearDTO {

    private Integer year;
    private Long packageId;
    private String packageName;
    private Long totalReservations;
}
