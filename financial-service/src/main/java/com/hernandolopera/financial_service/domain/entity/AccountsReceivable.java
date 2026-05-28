package com.hernandolopera.financial_service.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hernandolopera.financial_service.domain.enums.AccountStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "accounts_receivable")
@Getter @Setter
public class AccountsReceivable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAccount;

    private Integer idReservation;

    private BigDecimal totalDebt;
    private BigDecimal paidAmount;
    private BigDecimal pendingAmount;
    private Integer installments;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    private LocalDateTime createdAt;
}
