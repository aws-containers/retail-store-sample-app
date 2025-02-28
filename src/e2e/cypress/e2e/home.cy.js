import Home from "../pages/Home";

const home = new Home();

describe("testing home page", () => {
  it("should visit home page", () => {
    home.visit();
    home.showNow().should("be.visible");
  });

  it("should navigate to home", () => {
    home.visit();
    home.home().click();

    home.showNow().should("be.visible");
  });

  it("should navigate to catalog", () => {
    home.visit();
    home.catalog().click();

    cy.url().should("include", "/catalog");
  });
});
