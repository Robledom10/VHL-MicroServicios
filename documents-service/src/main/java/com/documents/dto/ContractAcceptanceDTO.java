package com.documents.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractAcceptanceDTO {

    private Integer reservationId;

    private Integer userId;

    private String contractVersion;

    private String electronicSignature;
}