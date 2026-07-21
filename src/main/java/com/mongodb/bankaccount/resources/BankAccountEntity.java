package com.mongodb.bankaccount.resources;

import com.mongodb.bankaccount.domain.BankAccount;

public record BankAccountEntity(
        String accountHolderName,
        String accountNumber,
        int cardVerificationCode,
        Double accountBalance) {
    public BankAccount toDomain() {
        return new BankAccount(accountHolderName, accountNumber, cardVerificationCode, accountBalance);
    }
}