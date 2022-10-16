let Page = require('./Page');

class Cart extends Page {

  constructor(baseUrl) {
    super(baseUrl);
  }

  getPath() {
    return '/cart';
  }

  getItems() {
    return element(by.id('basket')).all(by.css('.cart-item'));
  }
}
module.exports = Cart;