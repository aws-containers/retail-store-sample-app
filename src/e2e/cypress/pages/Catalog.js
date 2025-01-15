import Page from "./Page";

class Catalog extends Page {
  constructor() {
    super();
    this.url = "/catalog";
  }

  cards() {
    return cy.get(".product-card");
  }
}

module.exports = Catalog;
