import CheckoutAddress from "../pages/CheckoutAddress";
import CheckoutOrder from "../pages/CheckoutOrder";
import Cart from "../pages/Cart";
import Product from "../pages/Product";

const cart = new Cart();
const checkout = new CheckoutAddress();
const checkoutOrder = new CheckoutOrder();
const product1 = new Product("d27cf49f-b689-4a75-a249-d373e0330bb5");
const product2 = new Product("4f18544b-70a5-4352-8e19-0d070f46745d");

describe("testing checkout", () => {
  describe("single product", () => {
    beforeEach(() => {
      product1.visit();
      product1.addToCart().click();
      cart.checkout().click();
    });

    it("should visit checkout", () => {
      checkout.visit();
    });

    it("should process order", () => {
      checkout.visit();

      new CheckoutAddress().submit();

      new CheckoutAddress().submit();

      new CheckoutAddress().submit();

      checkoutOrder.shipping().should("contain.text", "$10");
      checkoutOrder.tax().should("contain.text", "$5");
      checkoutOrder.subtotal().should("contain.text", "$150");
      checkoutOrder.total().should("contain.text", "$165");
    });
  });

  describe("multiple products", () => {
    beforeEach(() => {
      product1.visit();
      product1.addToCart().click();
      product2.visit();
      product2.addToCart().click();
      cart.checkout().click();
    });

    it("should visit checkout", () => {
      checkout.visit();
    });

    it("should process order", () => {
      checkout.visit();

      new CheckoutAddress().submit();

      new CheckoutAddress().submit();

      new CheckoutAddress().submit();

      checkoutOrder.shipping().should("contain.text", "$10");
      checkoutOrder.tax().should("contain.text", "$5");
      checkoutOrder.subtotal().should("contain.text", "$360");
      checkoutOrder.total().should("contain.text", "$375");
    });
  });
});
