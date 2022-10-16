let Page = require('./Page');

class Catalog extends Page {

  constructor(baseUrl) {
    super(baseUrl);
  }

  getPath() {
    return '/catalog';
  }

  tag(name) {
    element(by.css('.category-menu')).element(by.linkText(name)).click();
  };

  size(number) {
    element(by.css('.products-number')).element(by.linkText(number)).click();
  };

  page(number) {
    element(by.css('.pagination')).element(by.linkText(number)).click();
  }

  getProducts() {
    return element(by.css('.products')).all(by.css('.product'));
  }

  async product(index) {
    return await this.getProducts().get(index).element(by.css('.product-link')).click()
  }
}
module.exports = Catalog;