let Page = require('./Page');

class Checkout extends Page {

  constructor(baseUrl) {
    super(baseUrl);
  }

  getPath() {
    return '/checkout';
  }

  async populateField(name, value) {
    let el = await element(by.id(name));

    await el.sendKeys(value);
  }

  async next() {
    return await element(by.id('checkoutForm')).submit();
  };
}
module.exports = Checkout;