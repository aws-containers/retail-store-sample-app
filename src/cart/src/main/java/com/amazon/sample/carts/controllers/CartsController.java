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

package com.amazon.sample.carts.controllers;

import com.amazon.sample.carts.controllers.api.Cart;
import com.amazon.sample.carts.controllers.api.Item;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.amazon.sample.carts.services.CartService;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@Tag(name = "carts")
@RequestMapping(path = "/carts")
@Slf4j
public class CartsController {
    @Autowired
    private CartService service;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{customerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieve a cart", operationId = "getCart")
    public Cart get(@PathVariable String customerId) {
        return Cart.from(this.service.get(customerId));
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping(value = "/{customerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete a cart", operationId = "deleteCart")
    public Cart delete(@PathVariable String customerId) {
        this.service.delete(customerId);

        return new Cart();
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping(value = "/{customerId}/merge", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Merge two carts contents", operationId = "mergeCarts")
    public void mergeCarts(@PathVariable String customerId, @RequestParam(value = "sessionId") String sessionId) {
        this.service.merge(sessionId, customerId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{customerId}/items/{itemId:.*}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieve an item from a cart", operationId = "getItem")
    public Item get(@PathVariable String customerId, @PathVariable String itemId) {
        return this.service.item(customerId, itemId).map(Item::from).get();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{customerId}/items", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieve items from a cart", operationId = "getItems")
    public List<Item> getItems(@PathVariable String customerId) {
        return this.service.items(customerId).stream()
                .map(Item::from).collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{customerId}/items", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Add an item to a cart", operationId = "addItem")
    public Item addToCart(@PathVariable String customerId, @RequestBody Item item) {
        return Item.from(this.service.add(customerId, item.getItemId(), item.getQuantity(), item.getUnitPrice()));
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping(value = "/{customerId}/items/{itemId:.*}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete an item from a cart", operationId = "deleteItem")
    public void removeItem(@PathVariable String customerId, @PathVariable String itemId) {
        this.service.deleteItem(customerId, itemId);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PatchMapping(value = "/{customerId}/items", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update an item in a cart", operationId = "updateItem")
    public void updateItem(@PathVariable String customerId, @RequestBody Item item) {
        this.service.update(customerId, item.getItemId(), item.getQuantity(), item.getUnitPrice());
    }
}
