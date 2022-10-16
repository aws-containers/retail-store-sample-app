let Checkout = require('./Checkout');

class CheckoutPayment extends Checkout {

  constructor(baseUrl) {
    super(baseUrl);
  }

  async populate(name, number, expiration, cvv) {
    this.populateField('cc-name', name);
    this.populateField('cc-number', number);
    this.populateField('cc-expiration', expiration);
    return this.populateField('cc-cvv', cvv);
  }
}
module.exports = CheckoutPayment;