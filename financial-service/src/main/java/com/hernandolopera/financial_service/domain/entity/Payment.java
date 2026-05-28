package com.hernandolopera.financial_service.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hernandolopera.financial_service.domain.enums.PaymentMethod;
import com.hernandolopera.financial_service.domain.enums.PaymentStatus;

import jakarta.persistence.Column;
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
@Table(name = "payment")
@Getter @Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPayment;

    private Integer fkIdAccount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private Integer installmentNumber;

    private BigDecimal amount;

    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(unique = true)
    private String referenceNumber;

    private LocalDateTime createdAt;
}
