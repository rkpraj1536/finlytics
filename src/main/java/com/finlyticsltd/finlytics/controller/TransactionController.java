package com.finlyticsltd.finlytics.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.finlyticsltd.finlytics.dto.CategoryTotal;
import com.finlyticsltd.finlytics.dto.SummaryResponse;
import com.finlyticsltd.finlytics.entity.Transaction;
import com.finlyticsltd.finlytics.entity.TransactionType;
import com.finlyticsltd.finlytics.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

	private final TransactionService transactionService;

	public TransactionController(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Transaction addTransaction(@Valid @RequestBody Transaction transaction) {
		return transactionService.addTransaction(transaction);
	}

	@GetMapping
	public List<Transaction> getAllTransactions() {
		return transactionService.getAllTransactions();
	}

	@GetMapping("/{id}")
	public Transaction getTransactionById(@PathVariable Long id) {
		return transactionService.getTransactionById(id);
	}

	@PutMapping("/{id}")
	public Transaction updateTransaction(@PathVariable Long id, @Valid @RequestBody Transaction transaction) {
		return transactionService.updateTransaction(id, transaction);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteTransaction(@PathVariable Long id) {
		transactionService.deleteTransaction(id);
	}

	@GetMapping("/summary")
	public SummaryResponse getSummary() {
		return transactionService.getSummary();
	}
	
	@GetMapping("/summary/by-category")
	public List<CategoryTotal> getTotalsByCategory(
	        @RequestParam(defaultValue = "EXPENSE") TransactionType type) {
	    return transactionService.getTotalsByCategory(type);
	}
}