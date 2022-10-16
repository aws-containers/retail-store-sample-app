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

package com.amazon.sample.carts.services;

import com.amazon.sample.carts.repositories.CartEntity;
import com.amazon.sample.carts.repositories.ItemEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractServiceTests {

    public abstract CartService getService();

    @Test
    public void testGetNewCart() {
        CartEntity cartEntity = this.getService().get("empty");

        assertEquals(0, cartEntity.getItems().size());
    }

    @Test
    public void testAddItem() {
        this.getService().add("123", "1", 1, 150);

        CartEntity cartEntity = this.getService().get("123");

        assertEquals("123", cartEntity.getCustomerId());
        assertEquals(1, cartEntity.getItems().size());

        ItemEntity itemEntity = cartEntity.getItems().get(0);

        assertEquals("1", itemEntity.getItemId());
        assertEquals(1, itemEntity.getQuantity());
        assertEquals(150, itemEntity.getUnitPrice());
    }

    @Test
    public void testRemoveItem() {
        this.getService().add("234", "1", 1, 150);

        this.getService().deleteItem("234", "1");

        CartEntity cartEntity = this.getService().get("234");

        assertEquals("234", cartEntity.getCustomerId());
        assertEquals(0, cartEntity.getItems().size());
    }

    @Test
    public void testDeleteCart() {
        this.getService().get("deleteme");

        this.getService().delete("deleteme");

        assertEquals(false, this.getService().exists("deleteme"));
    }
}
