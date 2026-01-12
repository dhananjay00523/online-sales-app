package com.company.onlinesalesapp.domain.service;

import com.company.onlinesalesapp.domain.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OnlineSalesService {

    public Money calculateCartTotal(Client client, List<CartItem> items) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        if (items == null || items.isEmpty()) {
            return Money.zero();
        }

        ShoppingCart cart = new ShoppingCart(client, items);
        ShoppingCart pricedCart = cart.withClientPricing();
        return pricedCart.calculateTotal();
    }
}
