package com.hernandolopera.financial_service.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hernandolopera.financial_service.domain.entity.Invoice;
import com.hernandolopera.financial_service.domain.entity.Payment;
import com.hernandolopera.financial_service.domain.enums.InvoiceType;
import com.hernandolopera.financial_service.repository.InvoiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public Invoice generateInvoice(Payment payment) {

        Invoice invoice = new Invoice();
        invoice.setFkIdPayment(payment.getIdPayment());
        invoice.setInvoiceNumber(UUID.randomUUID().toString());
        invoice.setTotalAmount(payment.getAmount());
        invoice.setInvoiceType(InvoiceType.PARCIAL);
        invoice.setIssueDate(LocalDateTime.now());
        invoice.setFileUrl("generated");

        return invoiceRepository.save(invoice);
    }
}
