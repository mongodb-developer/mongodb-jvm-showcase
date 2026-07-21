package com.mongodb.bankaccount.application.web;

import com.mongodb.bankaccount.domain.BankAccount;

record BankRequest(
        String accountHolderName,
        String accountNumber,
        int cardVerificationCode,
        Double accountBalance) {
    public BankAccount toDomain() {
        return new BankAccount(accountHolderName, accountNumber, cardVerificationCode, accountBalance);
    }
}