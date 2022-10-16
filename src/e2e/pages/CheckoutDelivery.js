let Checkout = require('./Checkout');

class CheckoutDelivery extends Checkout {

  constructor(baseUrl) {
    super(baseUrl);
  }

  async populate(shippingToken) {
    return element(by.css('.border-oncheck')).click();
  }
}
module.exports = CheckoutDelivery;