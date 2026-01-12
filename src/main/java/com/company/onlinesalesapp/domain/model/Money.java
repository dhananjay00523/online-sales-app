package com.company.onlinesalesapp.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(BigDecimal amount) {

    public Money {
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money multiply(int quantity) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)));
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    public static Money of(double amount) {
        return new Money(BigDecimal.valueOf(amount));
    }
}