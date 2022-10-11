package com.amazon.sample.ui.web;

import com.amazon.sample.ui.services.Metadata;
import com.amazon.sample.ui.services.carts.CartsService;
import com.amazon.sample.ui.services.catalog.CatalogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

import java.util.Collections;

@Controller
@RequestMapping("/catalog")
@Slf4j
public class CatalogController extends BaseController {

    private CatalogService catalogService;

    public CatalogController(@Autowired CatalogService catalogService, @Autowired CartsService cartsService, @Autowired Metadata metadata) {
        super(cartsService, metadata);

        this.catalogService = catalogService;
    }

    @GetMapping("")
    public String catalog(@RequestParam(required = false, defaultValue = "") String tag,
                          @RequestParam(required = false, defaultValue = "1") int page,
                          @RequestParam(required = false, defaultValue = "3") int size,
                          ServerHttpRequest request, Model model) {
        model.addAttribute("tags", this.catalogService.getTags());
        model.addAttribute("selectedTag", tag);

        model.addAttribute("catalog", catalogService.getProducts(tag, "", page, size));

        populateCommon(request, model);

        return "catalog";
    }

    @GetMapping("/{id}")
    public String item(@PathVariable String id,
                       ServerHttpRequest request, Model model) {
        model.addAttribute("item", catalogService.getProduct(id));
        model.addAttribute("recommendations", catalogService.getProducts("", "", 1, 3).map(p -> {
            Collections.shuffle(p.getProducts());
            return p.getProducts();
        }).flatMapMany(Flux::fromIterable));

        populateCommon(request, model);

        return "detail";
    }
}
