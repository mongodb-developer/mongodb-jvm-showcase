package com.mongodb.bankaccount.application.web;

import com.mongodb.bankaccount.domain.BankAccount;
import com.mongodb.bankaccount.domain.BankAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bank")
public class BankController {

    BankAccountService bankAccountService;

    BankController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping
    ResponseEntity<String> create(@RequestBody BankRequest bankRequest) {
        return ResponseEntity.ok(bankAccountService.insert(bankRequest.toDomain()));
    }

    @GetMapping
    ResponseEntity<List<BankResponse>> getAllAccounts() {
        return ResponseEntity.ok(bankAccountService.find().stream().map(BankAccount::toResponse).toList());
    }

    @GetMapping("/balance/greaterThan/{value}")
    ResponseEntity<List<BankResponse>> findByBalanceGreaterThan(@PathVariable Double value) {
        return ResponseEntity.ok(bankAccountService.findByBalanceGreaterThan(value).stream().map(BankAccount::toResponse).toList());
    }

    @GetMapping("/accountNumber/{accountNumber}")
    ResponseEntity<BankResponse> getAccountByNumber(@PathVariable String accountNumber) {
        BankAccount bankAccount = bankAccountService.findByAccountNumber(accountNumber);
        if (bankAccount != null) {
            return ResponseEntity.ok(bankAccount.toResponse());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
