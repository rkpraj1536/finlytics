package com.finlyticsltd.finlytics.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.finlyticsltd.finlytics.dto.CategoryTotal;
import com.finlyticsltd.finlytics.entity.Transaction;
import com.finlyticsltd.finlytics.entity.TransactionType;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserUsername(String username);

    Optional<Transaction> findByIdAndUserUsername(Long id, String username);

    @Query("SELECT SUM(t.amount) FROM Transaction t "
         + "WHERE t.type = :type AND t.user.username = :username")
    BigDecimal sumByTypeAndUser(@Param("type") TransactionType type,
                                @Param("username") String username);

    @Query("SELECT SUM(t.amount) FROM Transaction t "
         + "WHERE t.type = :type AND t.user.username = :username "
         + "AND t.transactionDate BETWEEN :start AND :end")
    BigDecimal sumByTypeAndUserAndDateBetween(@Param("type") TransactionType type,
                                              @Param("username") String username,
                                              @Param("start") LocalDate start,
                                              @Param("end") LocalDate end);

    @Query("SELECT new com.finlyticsltd.finlytics.dto.CategoryTotal(t.category, SUM(t.amount)) "
         + "FROM Transaction t WHERE t.type = :type AND t.user.username = :username "
         + "GROUP BY t.category ORDER BY SUM(t.amount) DESC")
    List<CategoryTotal> totalsByCategoryAndUser(@Param("type") TransactionType type,
                                                @Param("username") String username);
}