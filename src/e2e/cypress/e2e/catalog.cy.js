import Catalog from "../pages/Catalog";
import Cart from "../pages/Cart";

const catalog = new Catalog();

describe("testing catalog", () => {
  it("should visit catalog", () => {
    catalog.visit();
  });

  it("should display cards", () => {
    catalog.visit();

    catalog
      .cards()
      .first()
      .find(".product-name")
      .should("contain.text", "Aqua Ace GT");
    catalog.cards().its("length").should("eq", 6);
  });

  it("should add to cart", () => {
    catalog.visit();

    catalog.cards().first().find(".add-to-cart").click();

    new Cart().items().its("length").should("eq", 1);

    cy.url().should("contain", "/cart");
  });
});
