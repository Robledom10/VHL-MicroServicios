package com.hernandolopera.financial_service.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hernandolopera.financial_service.domain.enums.InvoiceType;

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
@Table(name = "invoice")
@Getter @Setter
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idInvoice;

    private Integer fkIdPayment;

    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    private InvoiceType invoiceType;

    private BigDecimal totalAmount;

    private String fileUrl;

    private LocalDateTime issueDate;
}
