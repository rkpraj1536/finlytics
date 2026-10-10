package com.finlyticsltd.finlytics.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.finlyticsltd.finlytics.dto.SummaryResponse;
import com.finlyticsltd.finlytics.entity.Transaction;
import com.finlyticsltd.finlytics.entity.TransactionType;
import com.finlyticsltd.finlytics.entity.User;
import com.finlyticsltd.finlytics.exception.ResourceNotFoundException;
import com.finlyticsltd.finlytics.repository.TransactionRepository;
import com.finlyticsltd.finlytics.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void getTransactionById_whenExists_returnsTransaction() {
        Transaction transaction = new Transaction();
        transaction.setCategory("Food");

        when(transactionRepository.findByIdAndUserUsername(1L, "rahul"))
                .thenReturn(Optional.of(transaction));

        Transaction result = transactionService.getTransactionById(1L, "rahul");

        assertThat(result.getCategory()).isEqualTo("Food");
    }

    @Test
    void getTransactionById_whenMissingOrNotOwned_throwsNotFound() {
        when(transactionRepository.findByIdAndUserUsername(999L, "rahul"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.getTransactionById(999L, "rahul"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void getSummary_calculatesBalance() {
        when(transactionRepository.sumByTypeAndUser(TransactionType.INCOME, "rahul"))
                .thenReturn(new BigDecimal("50000.00"));
        when(transactionRepository.sumByTypeAndUser(TransactionType.EXPENSE, "rahul"))
                .thenReturn(new BigDecimal("2000.50"));

        SummaryResponse summary = transactionService.getSummary("rahul");

        assertThat(summary.balance()).isEqualByComparingTo("47999.50");
    }

    @Test
    void getSummary_whenNoData_returnsZeros() {
        when(transactionRepository.sumByTypeAndUser(TransactionType.INCOME, "rahul")).thenReturn(null);
        when(transactionRepository.sumByTypeAndUser(TransactionType.EXPENSE, "rahul")).thenReturn(null);

        SummaryResponse summary = transactionService.getSummary("rahul");

        assertThat(summary.totalIncome()).isEqualByComparingTo("0");
        assertThat(summary.totalExpense()).isEqualByComparingTo("0");
        assertThat(summary.balance()).isEqualByComparingTo("0");
    }

    @Test
    void getMonthlySummary_whenMonthInvalid_throwsException() {
        assertThatThrownBy(() -> transactionService.getMonthlySummary(2026, 13, "rahul"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Month");
    }

    @Test
    void addTransaction_setsOwnerAndSaves() {
        User user = new User();
        user.setUsername("rahul");
        Transaction transaction = new Transaction();

        when(userRepository.findByUsername("rahul")).thenReturn(Optional.of(user));
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Transaction result = transactionService.addTransaction(transaction, "rahul");

        assertThat(result.getUser()).isSameAs(user);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void addTransaction_ignoresIdSentByClient() {
        User user = new User();
        user.setUsername("priya");
        Transaction transaction = new Transaction();
        transaction.setId(5L);

        when(userRepository.findByUsername("priya")).thenReturn(Optional.of(user));
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        transactionService.addTransaction(transaction, "priya");

        assertThat(transaction.getId()).isNull();
    }

    @Test
    void addTransaction_whenUserMissing_throwsAndSavesNothing() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.addTransaction(new Transaction(), "ghost"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void deleteTransaction_whenMissingOrNotOwned_throwsNotFoundAndDeletesNothing() {
        when(transactionRepository.findByIdAndUserUsername(999L, "rahul"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.deleteTransaction(999L, "rahul"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(transactionRepository, never()).delete(any(Transaction.class));
    }
}