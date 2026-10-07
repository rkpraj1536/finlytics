package com.finlyticsltd.finlytics.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import jakarta.persistence.*;

@Entity
@Table(name = "transactions")
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Amount is required")
	@Positive(message = "Amount must be greater than zero")
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal amount;

	@NotNull(message = "Type is required (INCOME or EXPENSE)")
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TransactionType type;

	@NotBlank(message = "Category is required")
	@Column(nullable = false)
	private String category;

	@Size(max = 255, message = "Description can be at most 255 characters")
	private String description;

	@NotNull(message = "Transaction date is required")
	@Column(nullable = false)
	private LocalDate transactionDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public TransactionType getType() {
		return type;
	}

	public void setType(TransactionType type) {
		this.type = type;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(LocalDate transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Transaction() {
		super();
	}

}