package com.mongodb.bankaccount.domain;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class BankAccountService {

    BankAccountPort bankAccountPort;

    BankAccountService(BankAccountPort bankAccountPort) {
        this.bankAccountPort = bankAccountPort;
    }

    public String insert(BankAccount bankAccount) {
        Objects.requireNonNull(bankAccount, "Bank account must not be null");

        return bankAccountPort.insert(bankAccount);
     }

    public List<BankAccount> find() {
        return bankAccountPort.find();
    }

    public List<BankAccount> findByBalanceGreaterThan(double value) {
        return bankAccountPort.findByBalanceGreaterThan(value);
    }

    public BankAccount findByAccountNumber(String accountNumber) {
        return bankAccountPort.findByAccountNumber(accountNumber);
    }

}



