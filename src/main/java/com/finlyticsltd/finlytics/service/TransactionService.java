package com.finlyticsltd.finlytics.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;

import com.finlyticsltd.finlytics.dto.CategoryTotal;
import com.finlyticsltd.finlytics.dto.SummaryResponse;
import com.finlyticsltd.finlytics.entity.Transaction;
import com.finlyticsltd.finlytics.entity.TransactionType;
import com.finlyticsltd.finlytics.entity.User;
import com.finlyticsltd.finlytics.exception.ResourceNotFoundException;
import com.finlyticsltd.finlytics.repository.TransactionRepository;
import com.finlyticsltd.finlytics.repository.UserRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public Transaction addTransaction(Transaction transaction, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        transaction.setId(null);
        transaction.setUser(user);
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions(String username) {
        return transactionRepository.findByUserUsername(username);
    }

    public Transaction getTransactionById(Long id, String username) {
        return transactionRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id " + id));
    }

    public Transaction updateTransaction(Long id, Transaction updated, String username) {
        Transaction existing = getTransactionById(id, username);

        existing.setAmount(updated.getAmount());
        existing.setType(updated.getType());
        existing.setCategory(updated.getCategory());
        existing.setDescription(updated.getDescription());
        existing.setTransactionDate(updated.getTransactionDate());

        return transactionRepository.save(existing);
    }

    public void deleteTransaction(Long id, String username) {
        Transaction existing = getTransactionById(id, username);
        transactionRepository.delete(existing);
    }

 // ---- Summaries (scoped to the logged-in user) ----

    public SummaryResponse getSummary(String username) {
        BigDecimal income = orZero(transactionRepository.sumByTypeAndUser(TransactionType.INCOME, username));
        BigDecimal expense = orZero(transactionRepository.sumByTypeAndUser(TransactionType.EXPENSE, username));

        return new SummaryResponse(income, expense, income.subtract(expense));
    }

    public List<CategoryTotal> getTotalsByCategory(TransactionType type, String username) {
        return transactionRepository.totalsByCategoryAndUser(type, username);
    }

    public SummaryResponse getMonthlySummary(int year, int month, String username) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        BigDecimal income = orZero(transactionRepository
                .sumByTypeAndUserAndDateBetween(TransactionType.INCOME, username, start, end));
        BigDecimal expense = orZero(transactionRepository
                .sumByTypeAndUserAndDateBetween(TransactionType.EXPENSE, username, start, end));

        return new SummaryResponse(income, expense, income.subtract(expense));
    }

    private BigDecimal orZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}