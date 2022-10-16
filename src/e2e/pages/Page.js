"use strict";

class Page {
  constructor(baseUrl) {
    this.baseUrl = baseUrl;
  }

  async home() {
    await element(by.id('menu-home')).click();
  };

  async catalog() {
    await element(by.id('menu-catalog')).click();
  };

  async cart() {
    await element(by.id('go-cart')).click();
  };

  async get(path) {
    return await browser.get(path);
  };

  async go() {
    return await this.get(this.getUrl());
  };

  getTitle() {
    return browser.getTitle();
  };

  getBreadcrumb() {
    return element(by.css('.breadcrumb-item.active'))
  }

  getPath() {
    return "";
  }

  getUrl() {
    return this.baseUrl+this.getPath();
  }
}
module.exports = Page;