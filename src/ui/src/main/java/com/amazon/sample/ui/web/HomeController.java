package com.amazon.sample.ui.web;

import com.amazon.sample.ui.services.Metadata;
import com.amazon.sample.ui.services.carts.CartsService;
import com.amazon.sample.ui.services.catalog.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController extends BaseController {

    private CatalogService catalogService;

    public HomeController(@Autowired CatalogService catalogService, @Autowired CartsService cartsService, @Autowired Metadata metadata) {
        super(cartsService, metadata);

        this.catalogService = catalogService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(final Model model, final ServerHttpRequest request) {
        model.addAttribute("catalog", this.catalogService.getProducts("", "" ,1, 4));

        populateCommon(request, model);

        return "home";
    }
}
