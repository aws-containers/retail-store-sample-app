let Page = require('./Page');

class Homepage extends Page {

  constructor(baseUrl) {
    super(baseUrl);
  }

  getPath() {
    return '/home';
  }
}
module.exports = Homepage;