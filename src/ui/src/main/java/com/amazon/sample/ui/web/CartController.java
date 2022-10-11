package com.amazon.sample.ui.web;

import com.amazon.sample.ui.services.Metadata;
import com.amazon.sample.ui.services.carts.CartsService;
import com.amazon.sample.ui.web.payload.CartChangeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/cart")
@Slf4j
public class CartController extends BaseController {

    private CartsService cartsService;


    public CartController(@Autowired CartsService cartsService, @Autowired Metadata metadata) {
        super(cartsService, metadata);

        this.cartsService = cartsService;
    }

    @GetMapping
    public String cart(ServerHttpRequest request, Model model) {
        String sessionId = getSessionID(request);

        model.addAttribute("fullCart", cartsService.getCart(sessionId));

        this.populateCommon(request, model);

        return "cart";
    }

    @PostMapping
    public Mono<String> add(@ModelAttribute CartChangeRequest addRequest, ServerHttpRequest request) {
        return this.cartsService.addItem(getSessionID(request), addRequest.getProductId())
            .thenReturn("redirect:/cart");
    }

    @PostMapping("/remove")
    public Mono<String> remove(@ModelAttribute CartChangeRequest addRequest, ServerHttpRequest request) {
        return this.cartsService.removeItem(getSessionID(request), addRequest.getProductId())
            .thenReturn("redirect:/cart");
    }
}
