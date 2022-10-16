let Page = require('./Page');

class Product extends Page {

  constructor(baseUrl, id) {
    super(baseUrl);

    this.id = id;
  }

  getPath() {
    return '/catalog/'+this.id;
  }

  getRecommendations() {
    return element(by.css('.recommendations')).all(by.css('.product'));
  }

  getName() {
    return element(by.id('productMain')).element(by.css('h1'));
  }

  getPrice() {
    return element(by.id('productMain')).element(by.css('.price'));
  }

  async addToCart() {
    await element(by.id('add-to-cart')).click();
  };
}
module.exports = Product;