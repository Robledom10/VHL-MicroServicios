package com.documents.entity;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contract_acceptances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractAcceptance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acceptance")
    private Integer idAcceptance;

    @Column(name = "id_reservation")
    private Integer idReservation;

    @Column(name = "id_user")
    private Integer idUser;

    @Column(name = "contract_version")
    private String contractVersion;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;
}