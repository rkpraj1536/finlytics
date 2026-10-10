package com.finlyticsltd.finlytics.controller;

import java.security.Principal;
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
    public Transaction addTransaction(@Valid @RequestBody Transaction transaction, Principal principal) {
        return transactionService.addTransaction(transaction, principal.getName());
    }

    @GetMapping
    public List<Transaction> getAllTransactions(Principal principal) {
        return transactionService.getAllTransactions(principal.getName());
    }

    @GetMapping("/{id}")
    public Transaction getTransactionById(@PathVariable Long id, Principal principal) {
        return transactionService.getTransactionById(id, principal.getName());
    }

    @PutMapping("/{id}")
    public Transaction updateTransaction(@PathVariable Long id,
            @Valid @RequestBody Transaction transaction, Principal principal) {
        return transactionService.updateTransaction(id, transaction, principal.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable Long id, Principal principal) {
        transactionService.deleteTransaction(id, principal.getName());
    }

    // ---- Summaries (not yet scoped to a user, next round) ----

    @GetMapping("/summary")
    public SummaryResponse getSummary(Principal principal) {
        return transactionService.getSummary(principal.getName());
    }

    @GetMapping("/summary/by-category")
    public List<CategoryTotal> getTotalsByCategory(
            @RequestParam(defaultValue = "EXPENSE") TransactionType type, Principal principal) {
        return transactionService.getTotalsByCategory(type, principal.getName());
    }

    @GetMapping("/summary/monthly")
    public SummaryResponse getMonthlySummary(@RequestParam int year, @RequestParam int month,
            Principal principal) {
        return transactionService.getMonthlySummary(year, month, principal.getName());
    }
}