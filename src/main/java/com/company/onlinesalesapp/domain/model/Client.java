package com.company.onlinesalesapp.domain.model;

public sealed interface Client permits IndividualClient, ProfessionalClient {
    String clientId();
    Money getPriceForProduct(ProductType productType);
}
