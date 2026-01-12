package com.company.onlinesalesapp.domain.model;

import java.util.Optional;

public record ProfessionalClient(String clientId, String companyName, Optional<String> intraCommunityVatNumber, String businessRegistrationNumber,
Money annualRevenue) implements Client {

    private static final Money REVENUE_THRESHOLD = Money.of(10_000_000);

    @Override
    public Money getPriceForProduct(ProductType productType) {
        boolean isHighRevenue = annualRevenue.amount()
                .compareTo(REVENUE_THRESHOLD.amount()) > 0;

        if (isHighRevenue) {
            return switch (productType) {
                case HIGH_END_PHONE -> Money.of(1000);
                case MID_RANGE_PHONE -> Money.of(550);
                case LAPTOP -> Money.of(900);
            };
        } else {
            return switch (productType) {
                case HIGH_END_PHONE -> Money.of(1150);
                case MID_RANGE_PHONE -> Money.of(600);
                case LAPTOP -> Money.of(1000);
            };
        }
    }

    public boolean hasHighRevenue() {
        return annualRevenue.amount().compareTo(REVENUE_THRESHOLD.amount()) > 0;
    }
}

