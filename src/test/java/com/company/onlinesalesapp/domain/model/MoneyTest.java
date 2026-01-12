package com.company.onlinesalesapp.domain.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void shouldCreateMoneyWithValidAmount() {
        Money money = Money.of(100.50);
        assertEquals(new BigDecimal("100.50"), money.amount());
    }


    @Test
    void shouldThrowExceptionForNullAmount() {
        assertThrows(NullPointerException.class,
                () -> new Money(null));
    }

    @Test
    void shouldAddMoneyCorrectly() {
        Money m1 = Money.of(100);
        Money m2 = Money.of(50);
        Money result = m1.add(m2);
        assertEquals(new BigDecimal("150.00"), result.amount());
    }

    @Test
    void shouldMultiplyByQuantity() {
        Money money = Money.of(100);
        Money result = money.multiply(3);
        assertEquals(new BigDecimal("300.00"), result.amount());
    }

    @Test
    void shouldCreateZeroMoney() {
        Money zero = Money.zero();
        assertEquals(BigDecimal.ZERO.setScale(2), zero.amount());
    }

    @Test
    void shouldRoundToTwoDecimalPlaces() {
        Money money = new Money(new BigDecimal("100.567"));
        assertEquals(new BigDecimal("100.57"), money.amount());
    }
}
