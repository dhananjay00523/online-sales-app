package com.company.onlinesalesapp.domain.model;

public record IndividualClient(String clientId, String firstName, String lastName) implements Client {
    @Override
    public Money getPriceForProduct(ProductType productType) {
        return switch (productType) {
            case HIGH_END_PHONE -> Money.of(1500);
            case MID_RANGE_PHONE -> Money.of(800);
            case LAPTOP -> Money.of(1200);
        };
    }
}
