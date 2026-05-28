package com.hernandolopera.financial_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hernandolopera.financial_service.domain.entity.AccountsReceivable;

public interface AccountRepository extends JpaRepository<AccountsReceivable, Integer> {

}
