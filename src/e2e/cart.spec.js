/**
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

browser.waitForAngularEnabled(false);
var baseUrl = browser.params.baseUrl

let Cart = require('./pages/Cart');
let CartItem = require('./pages/CartItem')
let Product = require('./pages/Product');

var cart = new Cart(baseUrl);
var product = new Product(baseUrl, '510a0d7e-8e83-4193-b483-e27e09ddc34d');

describe('when on cart', function() {
  beforeAll(function() { 
    browser.manage().deleteAllCookies();
    cart.go();
  });

  it('should have title', function() {
    expect(cart.getTitle()).toEqual('Retail Store Sample App');
  });

  it('should have breadcrumb', function() {
    expect(cart.getBreadcrumb().getText())
      .toEqual('Cart');
  });

  it('should be empty', function() {
    expect(cart.getItems().count()).toEqual(0);
  });

  describe('and add item', function() {
    beforeAll(function() { 
      browser.manage().deleteAllCookies();

      product.go();
      product.addToCart();
    });

    it('should show cart', function() {
      expect(browser.getCurrentUrl())
        .toBe(cart.getUrl());
    });

    it('should show the item', function() {
      expect(cart.getItems().count()).toEqual(1);

      let item = new CartItem(cart.getItems().get(0));

      expect(item.getName()).toEqual('Gentleman');
      expect(item.getPrice()).toEqual('$ 795');
    });
  });

  describe('and remove item', function() {
    beforeAll(function() { 
      browser.manage().deleteAllCookies();
      
      product.go();
      product.addToCart();

      let item = new CartItem(cart.getItems().get(0));

      item.remove();
    });

    it('should show cart', function() {
      expect(browser.getCurrentUrl())
        .toBe(cart.getUrl());
    });

    it('should be empty', function() {
      expect(cart.getItems().count()).toEqual(0);
    });
  });
});