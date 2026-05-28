package com.hernandolopera.financial_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.hernandolopera.financial_service.domain.entity.AccountsReceivable;
import com.hernandolopera.financial_service.domain.enums.AccountStatus;
import com.hernandolopera.financial_service.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountsReceivable registerAccount(Integer reservationId, BigDecimal totalDebt, Integer installments) {

        AccountsReceivable account = new AccountsReceivable();
        account.setIdReservation(reservationId);
        account.setTotalDebt(totalDebt);
        account.setPaidAmount(BigDecimal.ZERO);
        account.setPendingAmount(totalDebt);
        account.setInstallments(installments);
        account.setStatus(AccountStatus.PENDIENTE);
        account.setCreatedAt(LocalDateTime.now());

        return accountRepository.save(account);
    }

    public AccountsReceivable applyPayment(Integer accountId, BigDecimal amount) {

        AccountsReceivable account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        BigDecimal newPaid = account.getPaidAmount().add(amount);
        BigDecimal newPending = account.getTotalDebt().subtract(newPaid);

        account.setPaidAmount(newPaid);
        account.setPendingAmount(newPending);

        if (newPending.compareTo(BigDecimal.ZERO) == 0) {
            account.setStatus(AccountStatus.PAGADO);
        } else {
            account.setStatus(AccountStatus.PARCIAL);
        }

        return accountRepository.save(account);
    }
}
