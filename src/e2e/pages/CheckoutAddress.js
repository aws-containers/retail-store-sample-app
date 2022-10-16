let Checkout = require('./Checkout');

class CheckoutAddress extends Checkout {

  constructor(baseUrl) {
    super(baseUrl);
  }

  async populate(firstName, lastName, email, address, city, state, zip) {
    this.populateField('firstName', firstName);
    this.populateField('lastName', lastName);
    this.populateField('email', email);
    this.populateField('address1', address);
    this.populateField('city', city);
    this.populateField('zip', zip);

    return element(by.cssContainingText('option', state)).click();
  }
}
module.exports = CheckoutAddress;