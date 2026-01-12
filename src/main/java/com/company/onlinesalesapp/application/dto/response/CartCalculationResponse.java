package com.company.onlinesalesapp.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CartCalculationResponse(String clientId, String clientType, List<ItemDetail> items,
                                      BigDecimal totalAmount) {}
