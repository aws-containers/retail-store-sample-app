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

let CheckoutAddress = require('./pages/CheckoutAddress');
let CheckoutDelivery = require('./pages/CheckoutDelivery');
let CheckoutPayment = require('./pages/CheckoutPayment');
let CheckoutReview = require('./pages/CheckoutReview');
let CheckoutSuccess = require('./pages/CheckoutSuccess');
let Product = require('./pages/Product');

var checkoutAddress = new CheckoutAddress(baseUrl);
var checkoutDelivery = new CheckoutDelivery(baseUrl);
var checkoutPayment = new CheckoutPayment(baseUrl);
var checkoutReview = new CheckoutReview(baseUrl);
var checkoutSuccess = new CheckoutSuccess(baseUrl);
var product = new Product(baseUrl, '510a0d7e-8e83-4193-b483-e27e09ddc34d');

describe('when on checkout', function() {

  describe('with product in cart', function() {
    beforeAll(function() { 
      browser.manage().deleteAllCookies();

      product.go();
      product.addToCart();

      checkoutAddress.go();
    });

    it('should show checkout', function() {
      expect(browser.getCurrentUrl())
        .toBe(checkoutAddress.getUrl());
    });

    it('should process checkout address', async() => {
      await checkoutAddress.populate('John', 'Doe', 'jdoe@example.com', '123 Main Street', 'Santa Barbara', 'California', '95133');

      await checkoutAddress.next();

      await checkoutDelivery.populate('token1');

      await checkoutDelivery.next();

      await checkoutPayment.populate('John Doe', '1234567890', '12/20', '123');

      await checkoutPayment.next();

      await checkoutReview.next();
    });
  });
});