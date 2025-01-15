import Page from "./Page";

class Checkout extends Page {
  constructor() {
    super();
    this.url = "/checkout";
  }

  populateField(name, value) {
    return cy.get(`[name="${name}"]`).type(value);
  }

  items() {
    return cy.get(".checkout-item");
  }

  subtotal() {
    return cy.get("#checkout-subtotal");
  }

  submit() {
    return cy.get("#checkout-submit").click();
  }
}

module.exports = Checkout;
