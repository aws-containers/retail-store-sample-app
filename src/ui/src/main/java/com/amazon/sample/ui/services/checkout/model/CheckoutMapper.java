package com.amazon.sample.ui.services.checkout.model;

import com.amazon.sample.ui.services.carts.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CheckoutMapper {
    @Mapping(source = "request.items", target = "items")
    @Mapping(source = "request.subtotal", target = "subtotal")
    @Mapping(source = "shippingRates.rates", target = "shippingOptions")
    Checkout checkout(com.amazon.sample.ui.clients.checkout.model.Checkout checkout);

    CheckoutSubmitted submitted(com.amazon.sample.ui.clients.checkout.model.CheckoutSubmitted submitted);

    com.amazon.sample.ui.clients.checkout.model.ShippingAddress clientShippingAddress(ShippingAddress address);

    CheckoutItem item(com.amazon.sample.ui.clients.checkout.model.Item clientItem);

    @Mapping(source = "price", target = "unitCost")
    @Mapping(source = "totalPrice", target = "totalCost")
    com.amazon.sample.ui.clients.checkout.model.Item fromCartItem(CartItem cartItem);
}
