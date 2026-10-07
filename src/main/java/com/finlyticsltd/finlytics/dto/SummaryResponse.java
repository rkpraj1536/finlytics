package com.finlyticsltd.finlytics.dto;

import java.math.BigDecimal;

public record SummaryResponse(BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal balance) {
}