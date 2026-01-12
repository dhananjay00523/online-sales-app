package com.company.onlinesalesapp.application.dto.request;

import java.util.List;

public record CartCalculationRequest(String clientId, List<ProductQuantity> products) {}
