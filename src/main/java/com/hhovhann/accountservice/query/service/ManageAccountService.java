package com.hhovhann.accountservice.query.service;

import com.hhovhann.accountservice.event.AccountActivatedEvent;
import com.hhovhann.accountservice.event.AccountCreatedEvent;
import com.hhovhann.accountservice.event.AccountCreditedEvent;
import com.hhovhann.accountservice.event.AccountDebitedEvent;
import com.hhovhann.accountservice.query.entity.Account;
import com.hhovhann.accountservice.query.query.FindAccountByIdQuery;
import com.hhovhann.accountservice.query.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ManageAccountService {

    private final AccountRepository accountRepository;

    public ManageAccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @EventHandler
    public void on(AccountCreatedEvent accountCreatedEvent) {
        log.trace("AccountCreatedEvent, {}", accountCreatedEvent);

        Account account = new Account();
        account.setAccountId(accountCreatedEvent.getId());
        account.setBalance(accountCreatedEvent.getBalance());
        account.setStatus("CREATED");

        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountActivatedEvent accountActivatedEvent) {
        log.trace("AccountActivatedEvent, {}", accountActivatedEvent);
        Account account = accountRepository.findById(accountActivatedEvent.getId()).orElse(null);

        if (account != null) {
            account.setStatus(accountActivatedEvent.getStatus());
            accountRepository.save(account);
        }
    }
    @EventHandler
    public void on(AccountCreditedEvent accountCreditedEvent) {
        log.trace("AccountCreditedEvent, {}", accountCreditedEvent);
        Account account = accountRepository
                .findById(accountCreditedEvent.getId()).orElse(null);

        if (account != null) {
            account.setBalance(account.getBalance()
                    .add(accountCreditedEvent.getAmount()));
        }
    }
    @EventHandler
    public void on(AccountDebitedEvent accountDebitedEvent) {
        log.trace("AccountDebitedEvent, {}", accountDebitedEvent);
        Account account = accountRepository
                .findById(accountDebitedEvent.getId()).orElse(null);

        if (account != null) {
            account.setBalance(account.getBalance()
                    .subtract(accountDebitedEvent.getAmount()));
        }
    }

    @QueryHandler
    public Account handle(FindAccountByIdQuery findAccountByIdQuery) {
        log.trace("FindAccountByIdQuery, {}", findAccountByIdQuery);

        return accountRepository
                .findById(findAccountByIdQuery.getAccountId()).orElse(null);
    }
}
