/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.amazon.sample.ui.web;

import com.amazon.sample.ui.services.Metadata;
import com.amazon.sample.ui.services.carts.CartsService;
import com.amazon.sample.ui.services.checkout.CheckoutService;
import com.amazon.sample.ui.services.checkout.model.Checkout;
import com.amazon.sample.ui.services.checkout.model.ShippingAddress;
import com.amazon.sample.ui.web.payload.CheckoutDeliveryMethodRequest;
import com.amazon.sample.ui.web.payload.ShippingAddressRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/checkout")
@Slf4j
public class CheckoutController extends BaseController {

    private CheckoutService checkoutService;

    public CheckoutController(@Autowired CartsService cartsService,
                              @Autowired CheckoutService checkoutService,
                              @Autowired Metadata metadata) {
        super(cartsService, metadata);

        this.cartsService = cartsService;
        this.checkoutService = checkoutService;
    }

    @GetMapping
    public Mono<String> checkout(ServerHttpRequest request, Model model) {
        return showShipping(new ShippingAddressRequest(), request, model);
    }

    private Mono<String> showShipping(ShippingAddressRequest shippingAddressRequest, ServerHttpRequest request, Model model) {
        String sessionId = getSessionID(request);

        this.populateCommon(request, model);

        model.addAttribute("shippingAddressRequest", shippingAddressRequest);

        return this.checkoutService.create(sessionId)
                .doOnNext(o -> {
                    model.addAttribute("checkout", o);
                })
                .thenReturn("checkout-shipping");
    }

    @PostMapping
    public Mono<String> handleShipping(@Valid @ModelAttribute("shippingAddressRequest") ShippingAddressRequest shippingAddressRequest,
                                       BindingResult result,
                                       ServerHttpRequest request,
                                       Model model) {
        if (result.hasErrors()) {
            return showShipping(shippingAddressRequest, request, model);
        }

        ShippingAddress address = new ShippingAddress();
        address.setFirstName(shippingAddressRequest.getFirstName());
        address.setLastName(shippingAddressRequest.getLastName());
        address.setAddress1(shippingAddressRequest.getAddress1());
        address.setAddress2(shippingAddressRequest.getAddress2());
        address.setCity(shippingAddressRequest.getCity());
        address.setState(shippingAddressRequest.getState());
        address.setZip(shippingAddressRequest.getZip());

        String sessionId = getSessionID(request);

        return this.checkoutService.shipping(sessionId, shippingAddressRequest.getEmail(), address)
            .map(c -> this.showDelivery(c, new CheckoutDeliveryMethodRequest(), request, model));
    }

    public String showDelivery(Checkout checkout, CheckoutDeliveryMethodRequest checkoutDeliveryMethodRequest,
                                     ServerHttpRequest request,
                                     Model model) {
        this.populateCommon(request, model);

        model.addAttribute("checkoutDeliveryMethodRequest", checkoutDeliveryMethodRequest);
        model.addAttribute("checkout", checkout);

        return "checkout-delivery";
    }

    @PostMapping("/delivery")
    public Mono<String> handleDelivery(@Valid @ModelAttribute("checkoutDeliveryMethodRequest") CheckoutDeliveryMethodRequest checkoutDeliveryMethodRequest,
                                       BindingResult result,
                                       ServerHttpRequest request,
                                       Model model) {
        String sessionId = getSessionID(request);

        if (result.hasErrors()) {
            return this.checkoutService.get(sessionId).map(c -> showDelivery(c, checkoutDeliveryMethodRequest, request, model));
        }

        this.populateCommon(request, model);

        return this.checkoutService.delivery(sessionId, checkoutDeliveryMethodRequest.getToken())
            .doOnNext(o -> {
                model.addAttribute("checkout", o);
            })
            .thenReturn("checkout-payment");
    }

    @PostMapping("/payment")
    public String handlePayment(ServerHttpRequest request, Model model) {
        String sessionId = getSessionID(request);

        this.populateCommon(request, model);

        model.addAttribute("checkout", this.checkoutService.get(sessionId));

        return "checkout-confirm";
    }

    @PostMapping("/confirm")
    public Mono<String> confirm(ServerHttpRequest request, Model model) {
        String sessionId = getSessionID(request);

        populateMetadata(model);

        return this.checkoutService.submit(sessionId)
            .doOnNext(o -> {
                model.addAttribute("order", o.getSubmitted());
                model.addAttribute("cart", o.getCart());
            })
            .thenReturn("order");
    }
}
