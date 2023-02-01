package com.hhovhann.accountservice.command.aggregate;

import com.hhovhann.accountservice.command.command.CreateAccountCommand;
import com.hhovhann.accountservice.command.command.DepositMoneyCommand;
import com.hhovhann.accountservice.command.command.WithdrawMoneyCommand;
import com.hhovhann.accountservice.event.AccountActivatedEvent;
import com.hhovhann.accountservice.event.AccountCreatedEvent;
import com.hhovhann.accountservice.event.AccountCreditedEvent;
import com.hhovhann.accountservice.event.AccountDebitedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;

@Aggregate
@Slf4j
public class AccountAggregate {

    @AggregateIdentifier
    private String accountId;
    private BigDecimal balance;
    private String status;

    public AccountAggregate() {
    }

    @CommandHandler
    public AccountAggregate(CreateAccountCommand createAccountCommand) {
        log.trace("CreateAccountCommand, {}", createAccountCommand);

        AggregateLifecycle.apply(new AccountCreatedEvent(
                createAccountCommand.getId(),
                createAccountCommand.getBalance()));
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent accountCreatedEvent) {
        log.trace("AccountCreatedEvent, {}", accountCreatedEvent);

        this.accountId = accountCreatedEvent.getId();
        this.balance = accountCreatedEvent.getBalance();
        this.status = "CREATED";

        AggregateLifecycle.apply(new AccountActivatedEvent(this.accountId, "ACTIVATED"));
    }

    @EventSourcingHandler
    public void on(AccountActivatedEvent accountActivatedEvent) {
        log.trace("AccountActivatedEvent, {}", accountActivatedEvent);

        this.status = accountActivatedEvent.getStatus();
    }

    @CommandHandler
    public void on(DepositMoneyCommand depositMoneyCommand) {
        log.trace("DepositMoneyCommand, {}", depositMoneyCommand);

        AggregateLifecycle.apply(new AccountCreditedEvent(
                depositMoneyCommand.getId(),
                depositMoneyCommand.getAmount()));
    }

    @EventSourcingHandler
    public void on(AccountCreditedEvent accountCreditedEvent) {
        log.trace("AccountCreditedEvent, {}", accountCreditedEvent);

        this.balance = this.balance.add(accountCreditedEvent.getAmount());
    }

    @CommandHandler
    public void on(WithdrawMoneyCommand withdrawMoneyCommand) {
        log.trace("WithdrawMoneyCommand, {}", withdrawMoneyCommand);

        AggregateLifecycle.apply(new AccountDebitedEvent(
                withdrawMoneyCommand.getId(),
                withdrawMoneyCommand.getAmount()));
    }

    @EventSourcingHandler
    public void on(AccountDebitedEvent accountDebitedEvent) {
        log.trace("AccountDebitedEvent, {}", accountDebitedEvent);

        this.balance = this.balance.subtract(accountDebitedEvent.getAmount());
    }
}
