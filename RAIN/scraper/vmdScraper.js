const puppeteer = require('puppeteer');
const url = 'https://www.promet.si/portal/sl/stevci-prometa.aspx';
const $ = require('cheerio');
var array = [];

puppeteer
  .launch()
  .then(function(browser) {
    return browser.newPage();
  })
  .then(function(page) {
    return page.goto(url).then(function() {
      return page.content();
    });
  })
  .then(function(html) {
    $('td', html).each(function() {
      array.push($(this).text());
      console.log($(this).text());
    });
  })
  .catch(function(err) {
    //handle error
  });
