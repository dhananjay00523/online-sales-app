package com.company.onlinesalesapp.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IndividualClientTest {

    @Test
    void shouldCreateIndividualClient() {
        IndividualClient client = new IndividualClient(
                "IND001", "John", "Doe"
        );

        assertEquals("IND001", client.clientId());
        assertEquals("John", client.firstName());
        assertEquals("Doe", client.lastName());
    }

    @Test
    void shouldReturnCorrectPriceForHighEndPhone() {
        IndividualClient client = new IndividualClient(
                "IND001", "John", "Doe"
        );
        Money price = client.getPriceForProduct(ProductType.HIGH_END_PHONE);
        assertEquals(Money.of(1500), price);
    }

    @Test
    void shouldReturnCorrectPriceForMidRangePhone() {
        IndividualClient client = new IndividualClient(
                "IND001", "John", "Doe"
        );
        Money price = client.getPriceForProduct(ProductType.MID_RANGE_PHONE);
        assertEquals(Money.of(800), price);
    }

    @Test
    void shouldReturnCorrectPriceForLaptop() {
        IndividualClient client = new IndividualClient(
                "IND001", "John", "Doe"
        );
        Money price = client.getPriceForProduct(ProductType.LAPTOP);
        assertEquals(Money.of(1200), price);
    }
}
