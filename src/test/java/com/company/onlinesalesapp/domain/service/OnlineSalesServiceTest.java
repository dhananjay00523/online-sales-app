package com.company.onlinesalesapp.domain.service;

import com.company.onlinesalesapp.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class OnlineSalesServiceTest {

    private OnlineSalesService service;

    @BeforeEach
    void setUp() {
        service = new OnlineSalesService();
    }

    @Test
    void shouldCalculateCartTotalForIndividualClient() {
        IndividualClient client = new IndividualClient(
                "IND001", "John", "Doe"
        );

        List<CartItem> items = List.of(
                new CartItem(
                        new Product(ProductType.HIGH_END_PHONE, Money.zero()),
                        1
                ),
                new CartItem(
                        new Product(ProductType.LAPTOP, Money.zero()),
                        1
                )
        );

        Money total = service.calculateCartTotal(client, items);
        assertEquals(Money.of(2700), total);
    }

    @Test
    void shouldCalculateCartTotalForLowRevenueProfessional() {
        ProfessionalClient client = new ProfessionalClient(
                "PRO001",
                "Small Corp",
                Optional.empty(),
                "REG123",
                Money.of(5_000_000)
        );

        List<CartItem> items = List.of(
                new CartItem(
                        new Product(ProductType.HIGH_END_PHONE, Money.zero()),
                        2
                ),
                new CartItem(
                        new Product(ProductType.MID_RANGE_PHONE, Money.zero()),
                        1
                ),
                new CartItem(
                        new Product(ProductType.LAPTOP, Money.zero()),
                        1
                )
        );

        Money total = service.calculateCartTotal(client, items);

        // 2 * 1150 + 1 * 600 + 1 * 1000 = 3900
        assertEquals(Money.of(3900), total);
    }

    @Test
    void shouldReturnZeroForEmptyItems() {
        IndividualClient client = new IndividualClient(
                "IND001", "John", "Doe"
        );

        Money total = service.calculateCartTotal(client, List.of());
        assertEquals(Money.zero(), total);
    }

    @Test
    void shouldThrowExceptionForNullClient() {
        assertThrows(IllegalArgumentException.class,
                () -> service.calculateCartTotal(null, List.of()));
    }
}
