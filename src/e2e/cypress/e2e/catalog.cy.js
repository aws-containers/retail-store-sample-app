import Catalog from "../pages/Catalog";

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
      .should("contain.text", "The Quiet Quill");
    catalog.cards().its("length").should("eq", 6);
  });
});
