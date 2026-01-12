package com.company.onlinesalesapp.application.dto.response;

import java.math.BigDecimal;

public record ItemDetail(String productType, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {}
