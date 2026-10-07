package com.finlyticsltd.finlytics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.finlyticsltd.finlytics.entity.Transaction;
import com.finlyticsltd.finlytics.repository.TransactionRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction addTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}