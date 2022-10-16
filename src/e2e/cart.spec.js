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