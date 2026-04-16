import Catalog from "../pages/Catalog";

const catalog = new Catalog();

describe("testing catalog search", () => {
  it("should not display search bar when search is disabled", () => {
    if (Cypress.env("SEARCH_ENABLED")) {
      cy.log("Skipping: search is enabled in this environment");
      return;
    }

    catalog.visit();

    cy.get('input[name="keyword"]').should("not.exist");
  });

  describe("with search enabled", () => {
    beforeEach(function () {
      if (!Cypress.env("SEARCH_ENABLED")) {
        this.skip();
      }
    });

    it("should display search bar on catalog page", () => {
      catalog.visit();

      cy.get('input[name="keyword"]').should("be.visible");
      cy.get('input[name="keyword"]').should(
        "have.attr",
        "placeholder",
        "Search gadgets...",
      );
    });

    it("should return results for a valid keyword", () => {
      catalog.visit();

      cy.get('input[name="keyword"]').type("shoe");
      cy.get('button[type="submit"]').contains("Search").click();

      cy.url().should("include", "/catalog/search");
      cy.url().should("include", "keyword=shoe");

      cy.get(".product-card").should("have.length.greaterThan", 0);
    });

    it("should show result count after searching", () => {
      catalog.visit();

      cy.get('input[name="keyword"]').type("shoe");
      cy.get('button[type="submit"]').contains("Search").click();

      cy.contains("Showing results for").should("be.visible");
      cy.contains("products found").should("be.visible");
    });

    it("should show no results message for nonsense keyword", () => {
      catalog.visit();

      cy.get('input[name="keyword"]').type("xyznonexistent123");
      cy.get('button[type="submit"]').contains("Search").click();

      cy.contains("No gadgets found").should("be.visible");
    });

    it("should clear search and return to catalog", () => {
      catalog.visit();

      cy.get('input[name="keyword"]').type("shoe");
      cy.get('button[type="submit"]').contains("Search").click();

      cy.contains("Clear").click();

      cy.url().should("include", "/catalog");
      cy.url().should("not.include", "/search");
    });

    it("should hide sidebar when showing search results", () => {
      catalog.visit();

      cy.get('input[name="keyword"]').type("shoe");
      cy.get('button[type="submit"]').contains("Search").click();

      cy.contains("Categories").should("not.exist");
    });
  });
});
