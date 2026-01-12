package com.company.onlinesalesapp.domain.model;

import java.util.List;

public record ShoppingCart(Client client, List<CartItem> items) {

    public Money calculateTotal() {
        return items.stream()
                .map(CartItem::calculateTotal)
                .reduce(Money.zero(), Money::add);
    }

    public ShoppingCart withClientPricing() {
        List<CartItem> pricedItems = items.stream()
                .map(item -> new CartItem(
                        new Product(
                                item.product().type(),
                                client.getPriceForProduct(item.product().type())
                        ),
                        item.quantity()
                ))
                .toList();

        return new ShoppingCart(client, pricedItems);
    }
}