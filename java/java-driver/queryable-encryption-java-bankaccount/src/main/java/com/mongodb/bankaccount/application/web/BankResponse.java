package com.mongodb.bankaccount.application.web;

public record BankResponse(String accountHolderName,
                           String accountNumber,
                           int cardVerificationCode,
                           Double accountBalance) {
}
