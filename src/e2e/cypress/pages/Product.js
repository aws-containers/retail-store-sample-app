import Page from "./Page";

class Product extends Page {
  constructor(id) {
    super();
    this.id = id;
    this.url = `/catalog/${id}`;
  }

  addToCart() {
    return cy.get("#add-to-cart");
  }
}

module.exports = Product;
