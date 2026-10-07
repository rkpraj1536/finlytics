package com.finlyticsltd.finlytics.repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.finlyticsltd.finlytics.dto.CategoryTotal;
import com.finlyticsltd.finlytics.entity.Transaction;
import com.finlyticsltd.finlytics.entity.TransactionType;



public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByType(TransactionType type);

    List<Transaction> findByCategory(String category);

    List<Transaction> findByTransactionDateBetween(LocalDate start, LocalDate end);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.type = :type")
    BigDecimal sumByType(@Param("type") TransactionType type);


    @Query("SELECT new com.finlyticsltd.finlytics.dto.CategoryTotal(t.category, SUM(t.amount)) "
    	     + "FROM Transaction t WHERE t.type = :type "
    	     + "GROUP BY t.category ORDER BY SUM(t.amount) DESC")
    	List<CategoryTotal> totalsByCategory(@Param("type") TransactionType type);



}




