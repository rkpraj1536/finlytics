package com.finlyticsltd.finlytics.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.finlyticsltd.finlytics.dto.CategoryTotal;
import com.finlyticsltd.finlytics.dto.SummaryResponse;
import com.finlyticsltd.finlytics.entity.Transaction;
import com.finlyticsltd.finlytics.entity.TransactionType;
import com.finlyticsltd.finlytics.exception.ResourceNotFoundException;
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

	public Transaction getTransactionById(Long id) {
		return transactionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id " + id));
	}

	public Transaction updateTransaction(Long id, Transaction updated) {
		Transaction existing = getTransactionById(id);

		existing.setAmount(updated.getAmount());
		existing.setType(updated.getType());
		existing.setCategory(updated.getCategory());
		existing.setDescription(updated.getDescription());
		existing.setTransactionDate(updated.getTransactionDate());

		return transactionRepository.save(existing);
	}

	public void deleteTransaction(Long id) {
		Transaction existing = getTransactionById(id);
		transactionRepository.delete(existing);
	}

	public SummaryResponse getSummary() {
		BigDecimal income = transactionRepository.sumByType(TransactionType.INCOME);
		BigDecimal expense = transactionRepository.sumByType(TransactionType.EXPENSE);

		if (income == null) {
			income = BigDecimal.ZERO;
		}
		if (expense == null) {
			expense = BigDecimal.ZERO;
		}

		return new SummaryResponse(income, expense, income.subtract(expense));
		
	}

	public List<CategoryTotal> getTotalsByCategory(TransactionType type) {
		return transactionRepository.totalsByCategory(type);
	}
}