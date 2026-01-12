package com.company.onlinesalesapp.domain.model;

import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class ProfessionalClientTest {

    @Test
    void shouldCreateProfessionalClientWithHighRevenue() {
        ProfessionalClient client = new ProfessionalClient(
                "PRO001",
                "Tech Corp",
                Optional.of("VAT123"),
                "REG456",
                Money.of(15_000_000)
        );

        assertTrue(client.hasHighRevenue());
        assertEquals("PRO001", client.clientId());
        assertEquals("Tech Corp", client.companyName());
    }

    @Test
    void shouldCreateProfessionalClientWithLowRevenue() {
        ProfessionalClient client = new ProfessionalClient(
                "PRO002",
                "Small Business",
                Optional.empty(),
                "REG789",
                Money.of(5_000_000)
        );

        assertFalse(client.hasHighRevenue());
    }

    @Test
    void shouldReturnHighRevenuePricing() {
        ProfessionalClient client = new ProfessionalClient(
                "PRO001",
                "Tech Corp",
                Optional.empty(),
                "REG456",
                Money.of(15_000_000)
        );

        assertEquals(Money.of(1000),
                client.getPriceForProduct(ProductType.HIGH_END_PHONE));
        assertEquals(Money.of(550),
                client.getPriceForProduct(ProductType.MID_RANGE_PHONE));
        assertEquals(Money.of(900),
                client.getPriceForProduct(ProductType.LAPTOP));
    }

    @Test
    void shouldReturnLowRevenuePricing() {
        ProfessionalClient client = new ProfessionalClient(
                "PRO002",
                "Small Business",
                Optional.empty(),
                "REG789",
                Money.of(5_000_000)
        );

        assertEquals(Money.of(1150),
                client.getPriceForProduct(ProductType.HIGH_END_PHONE));
        assertEquals(Money.of(600),
                client.getPriceForProduct(ProductType.MID_RANGE_PHONE));
        assertEquals(Money.of(1000),
                client.getPriceForProduct(ProductType.LAPTOP));
    }
}
