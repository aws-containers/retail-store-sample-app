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