import Cart from "../pages/Cart";
import Product from "../pages/Product";

const cart = new Cart();
const product = new Product("d27cf49f-b689-4a75-a249-d373e0330bb5");

describe("testing cart", () => {
  beforeEach(() => {
    product.visit();
    product.addToCart().click();
  });

  it("should visit cart", () => {
    cart.visit();
  });

  it("should display items", () => {
    cart.items().its("length").should("eq", 1);

    cart
      .items()
      .first()
      .find(".item-name")
      .should("contain.text", "The Quiet Quill");

    cart.subtotal().should("contain.text", "$150");
  });

  it("should open checkout", () => {
    cart.checkout().click();

    cy.url().should("contain", "/checkout");
  });
});
