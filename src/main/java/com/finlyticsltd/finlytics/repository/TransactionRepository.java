package com.finlyticsltd.finlytics.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finlyticsltd.finlytics.entity.Transaction;
import com.finlyticsltd.finlytics.entity.TransactionType;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByType(TransactionType type);

    List<Transaction> findByCategory(String category);

    List<Transaction> findByTransactionDateBetween(LocalDate start, LocalDate end);
}