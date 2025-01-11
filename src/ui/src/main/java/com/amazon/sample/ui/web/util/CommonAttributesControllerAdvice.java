package com.amazon.sample.ui.web.util;

import com.amazon.sample.ui.services.carts.CartsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@ControllerAdvice(annotations = RequiresCommonAttributes.class)
public class CommonAttributesControllerAdvice {

  private final CartsService cartsService;

  public CommonAttributesControllerAdvice(CartsService cartsService) {
    this.cartsService = cartsService;
  }

  @ModelAttribute("common")
  public void populateCommon(ServerHttpRequest request, Model model) {
    populateCart(request, model);
  }

  private void populateCart(ServerHttpRequest request, Model model) {
    String sessionId = SessionIDUtil.getSessionId(request);

    cartsService
      .getCart(sessionId)
      .doOnNext(cart -> model.addAttribute("cart", cart))
      .subscribe();
  }
}
