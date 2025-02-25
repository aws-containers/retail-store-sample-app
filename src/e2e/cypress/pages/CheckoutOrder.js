import Checkout from "./Checkout";

class CheckoutOrder extends Checkout {
  constructor() {
    super();
  }

  email() {
    return cy.get("#order-email");
  }

  tax() {
    return cy.get("#order-tax");
  }

  shipping() {
    return cy.get("#order-shipping");
  }

  subtotal() {
    return cy.get("#order-subtotal");
  }

  total() {
    return cy.get("#order-total");
  }
}

module.exports = CheckoutOrder;
