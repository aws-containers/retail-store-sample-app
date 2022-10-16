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