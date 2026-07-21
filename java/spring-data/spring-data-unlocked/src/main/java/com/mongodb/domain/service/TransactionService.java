package com.mongodb.domain.service;

import com.mongodb.domain.model.Transaction;
import com.mongodb.resources.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Page<Transaction> findAll(
            Pageable pageable
    ) {
        return transactionRepository.findAll(pageable);
    }

    public List<Transaction> searchTransactions(
            String type,
            String currency,
            String status) {

        if (type != null && currency != null) {
            return transactionRepository.findByTransactionTypeAndCurrency(type, currency);
        }
        if (type != null) {
            return transactionRepository.findByTransactionType(type);
        }
        if (status != null) {
            return transactionRepository.findByStatus(status);
        }
        return List.of();
    }

    public void exportErrorTransactions() {
        transactionRepository.exportErrorTransactions();
    }

    public Transaction update(Transaction updatedTransaction) {
        return transactionRepository.save(updatedTransaction);
    }

    public void delete(String id) {
        if (!transactionRepository.existsById(id)) {
            throw new NoSuchElementException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }
}