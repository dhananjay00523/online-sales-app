package com.company.onlinesalesapp.application;

import com.company.onlinesalesapp.application.dto.request.CartCalculationRequest;
import com.company.onlinesalesapp.application.dto.response.CartCalculationResponse;
import com.company.onlinesalesapp.application.dto.response.ItemDetail;
import com.company.onlinesalesapp.domain.model.*;
import com.company.onlinesalesapp.domain.port.ClientRepository;
import com.company.onlinesalesapp.domain.service.OnlineSalesService;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CartCalculation {

    private final ClientRepository clientRepository;
    private final OnlineSalesService onlineSalesService;

    public CartCalculation(ClientRepository clientRepository, OnlineSalesService onlineSalesService) {
        this.clientRepository = clientRepository;
        this.onlineSalesService = onlineSalesService;
    }

    public CartCalculationResponse execute(CartCalculationRequest request) {
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Client not found: " + request.clientId()));

        List<CartItem> cartItems = request.products().stream()
                .map(pq -> new CartItem(
                        new Product(
                                ProductType.valueOf(pq.productType()),
                                Money.zero()
                        ),
                        pq.quantity()
                ))
                .toList();

        Money total = onlineSalesService.calculateCartTotal(client, cartItems);

        List<ItemDetail> itemDetails =
                createItemDetails(client, cartItems);

        String clientType = client instanceof IndividualClient
                ? "INDIVIDUAL" : "PROFESSIONAL";

        return new CartCalculationResponse(
                client.clientId(),
                clientType,
                itemDetails,
                total.amount()
        );
    }

    private List<ItemDetail> createItemDetails(
            Client client,
            List<CartItem> items
    ) {
        return items.stream()
                .map(item -> {
                    Money unitPrice = client.getPriceForProduct(item.product().type());
                    Money lineTotal = unitPrice.multiply(item.quantity());
                    return new ItemDetail(
                            item.product().type().name(),
                            item.quantity(),
                            unitPrice.amount(),
                            lineTotal.amount()
                    );
                })
                .toList();
    }
}