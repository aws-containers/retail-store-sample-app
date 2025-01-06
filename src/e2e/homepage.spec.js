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

let Homepage = require('./pages/Homepage');
let Catalog = require('./pages/Catalog');
let Cart = require('./pages/Cart');

var homepage = new Homepage(baseUrl);
var catalog = new Catalog(baseUrl);
var cart = new Cart(baseUrl);

describe('when on homepage', function() {
  beforeAll(function() { 
    homepage.go();
  });

  it('should have a title', function() {
    expect(homepage.getTitle()).toEqual('Retail Store Sample App');
  });

  describe('and open Home', function() {
    beforeAll(function() { 
      homepage.home();
    });

    it('should open the home page', function() {
      expect(browser.getCurrentUrl())
        .toBe(homepage.getUrl());
    });
  });

  describe('and open Catalog', function() {
    beforeAll(function() { 
      homepage.catalog();
    });

    it('should open the catalog page', function() {
      expect(browser.getCurrentUrl())
        .toBe(catalog.getUrl());
    });
  });

  describe('and open Cart', function() {
    beforeAll(function() { 
      homepage.cart();
    });

    it('should open the cart page', function() {
      expect(browser.getCurrentUrl())
        .toBe(cart.getUrl());
    });
  });
});