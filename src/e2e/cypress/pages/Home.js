import Page from "./Page";

class Home extends Page {
  constructor() {
    super();
    this.url = "/";
  }

  showNow() {
    return cy.get("#shop-now");
  }
}

module.exports = Home;
