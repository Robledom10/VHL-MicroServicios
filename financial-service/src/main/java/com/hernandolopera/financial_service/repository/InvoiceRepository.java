package com.hernandolopera.financial_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hernandolopera.financial_service.domain.entity.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer>{
    
}
