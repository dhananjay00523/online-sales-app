package com.company.onlinesalesapp.domain.model;

public record CartItem(Product product, int quantity) {

    public Money calculateTotal() {
        return product.price().multiply(quantity);
    }
}