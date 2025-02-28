import Checkout from "./Checkout";

class CheckoutAddress extends Checkout {
  constructor() {
    super();
  }

  populate(firstName, lastName, email, address, city, state, zip) {
    this.populateField("firstName", firstName);
    this.populateField("lastName", lastName);
    this.populateField("email", email);
    this.populateField("streetAddress", address);
    this.populateField("city", city);
    this.populateField("zip", zip);

    cy.get('select[name="state"]').select(state);
  }
}

module.exports = CheckoutAddress;
