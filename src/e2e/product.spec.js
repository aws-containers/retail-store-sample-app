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

let EC = protractor.ExpectedConditions;

let Product = require('./pages/Product');
let Cart = require('./pages/Cart');

var product = new Product(baseUrl, '510a0d7e-8e83-4193-b483-e27e09ddc34d');
var cart = new Cart(baseUrl);

describe('when on product', function() {
  beforeAll(function() { 
    product.go();
  });

  it('should have title', function() {
    expect(product.getTitle()).toEqual('Retail Store Sample App');
  });

  it('should have breadcrumb', function() {
    expect(product.getBreadcrumb().getText())
      .toEqual('Gentleman');
  });

  it('should have name', function() {
    expect(product.getName().getText())
      .toEqual('Gentleman');
  });

  it('should have price', function() {
    expect(product.getPrice().getText())
      .toEqual('$795');
  });

  describe('and add to cart', function() {
    beforeAll(function() { 
      product.go();
      product.addToCart();
    });

    it('should show cart', function() {
      expect(browser.getCurrentUrl())
        .toBe(cart.getUrl());
    });
  });
});