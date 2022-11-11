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

package com.amazon.sample.ui.services.carts.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Cart {
    private List<CartItem> items = new ArrayList<>();

    public void addItem(CartItem item) {
        boolean existing = false;

        for(CartItem i : items) {
            if(i.getId().equals(item.getId())) {
                i.addQuantity(item.getQuantity());
                existing = true;
            }
        }

        if(!existing) {
            this.items.add(item);
        }
    }

    public void removeItem(String id) {
        for(CartItem i : items) {
            if(i.getId().equals(id)) {
                this.items.remove(i);
            }
        }
    }

    public int getSubtotal() {
        int subtotal = 0;

        for(CartItem i : items) {
            subtotal += i.getTotalPrice();
        }

        return subtotal;
    }

    public int getTotalPrice() {
        return this.getSubtotal() + this.getShipping();
    }

    public int getShipping() {
        return this.items.size() > 0 ? 10 : 0;
    }

    public int getNumItems() {
        int total = 0;

        for(CartItem i : items) {
            total += i.getQuantity();
        }

        return total;
    }
}
