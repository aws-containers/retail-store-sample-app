browser.waitForAngularEnabled(false);
var baseUrl = browser.params.baseUrl

let Catalog = require('./pages/Catalog');
let Product = require('./pages/Product');

var catalog = new Catalog(baseUrl);
var product = new Product(baseUrl, '510a0d7e-8e83-4193-b483-e27e09ddc34d');

describe('when on catalog', function() {
  beforeAll(function() { 
    catalog.go();
  });

  it('should have title', function() {
    expect(catalog.getTitle()).toEqual('Retail Store Sample App');
  });

  it('should have breadcrumb', function() {
    expect(catalog.getBreadcrumb().getText())
      .toEqual('Catalog');
  });

  it('should show products', function() {
    expect(catalog.getProducts().count())
      .toEqual(3);
  });

  describe('and select page', function() {
    beforeEach(function() { 
      catalog.go();
      catalog.page('2');
    });

    it('should show page', function() {
      expect(catalog.getProducts().count())
        .toEqual(3);
    });
  });

  describe('and select page size', function() {
    beforeEach(function() { 
      catalog.go();
      catalog.size('9');
    });

    it('should show products', function() {
      expect(catalog.getProducts().count())
        .toEqual(6);
    });

    describe('and select tag', function() {
      beforeEach(function() { 
        catalog.tag('Dress');
      });
  
      it('should filter products', function() {
        expect(catalog.getProducts().count())
          .toEqual(5);
      });
    });
  });

  describe('and select tag', function() {
    beforeEach(function() { 
      catalog.go();
      catalog.tag('Luxury');
    });

    it('should filter products', function() {
      expect(catalog.getProducts().count())
        .toEqual(1);
    });
  });

  describe('and select item', function() {
    beforeEach(async function() { 
      catalog.go();
      catalog.product(0);
    });

    it('should show product page', function() {
      expect(browser.getCurrentUrl())
        .toBe(product.getUrl());
    });
  });
});