const puppeteer = require('puppeteer');
const url = 'https://www.promet.si/portal/sl/stevci-prometa.aspx';
const $ = require('cheerio');
var array = [];
var MongoClient = require('mongodb').MongoClient;
var url2 = "mongodb://localhost:27017/";

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
    console.log("_________________________________________________________________");
    MongoClient.connect(url2, function(err, db) {
      if (err) throw err;
      var dbo = db.db("projekt_database");
      dbo.collection('infrastrukturas',function(err, collection){
        collection.deleteMany({},function(err, removed){
        });
      });
    });
    MongoClient.connect(url2, function(err, db) {
      if (err) throw err;
      var dbo = db.db("projekt_database");
      var myobj = [];
      for(var i=0; i<300; i++)
      {
        myobj.push({ lokacija: array[i*10+45], stanje_vozisca: array[i*10+52], hitrost: array[i*10+50], razmik: array[i*10+51]});
      }
      dbo.collection("infrastrukturas").insertMany(myobj, function(err, res) {
        if (err) throw err;
        console.log("Number of documents inserted: " + res.insertedCount);
        db.close();
      });
    });
  })
  .catch(function(err) {
    //handle error
  });
