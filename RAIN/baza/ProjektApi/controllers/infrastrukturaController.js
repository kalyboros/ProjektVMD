var infrastrukturaModel = require('../models/infrastrukturaModel.js');
const puppeteer = require('puppeteer');
const url = 'https://www.promet.si/portal/sl/stevci-prometa.aspx';
const $ = require('cheerio');
var array = [];
var MongoClient = require('mongodb').MongoClient;
var url2 = "mongodb://localhost:27017/";

/**
 * infrastrukturaController.js
 *
 * @description :: Server-side logic for managing infrastrukturas.
 */
module.exports = {

    /**
     * infrastrukturaController.list()
     */
    list: function (req, res) {
        infrastrukturaModel.find(function (err, infrastruktura) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting infrastruktura.',
                    error: err
                });
            }
            //return res.redirect("infrastruktura");
            return res.render("izpis", {infrastruktura: infrastruktura});
        });
    },

    /**
     * infrastrukturaController.show()
     */
    show: function (req, res) {
        var id = req.params.id;
        infrastrukturaModel.findOne({_id: id}, function (err, infrastruktura) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting infrastruktura.',
                    error: err
                });
            }
            if (!infrastruktura) {
                return res.status(404).json({
                    message: 'No such infrastruktura'
                });
            }
            return res.json(infrastruktura);
        });
    },
    dodajPodatke: function (req, res) {
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
                        myobj.push({ lokacija: array[i*10+65], stanje_vozisca: array[i*10+72], hitrost: array[i*10+70], razmik: array[i*10+71]});
                    }
                    dbo.collection("infrastrukturas").insertMany(myobj, function(err, res) {
                        if (err) throw err;
                        console.log("Number of documents inserted: " + res.insertedCount);
                        db.close();
                    });
                });
                return res.redirect('/');
            })
            .catch(function(err) {
                //handle error
            });
    }
    ,
    /*showInfra: function (req, res) {
        res.render('izpis');
    }
    ,*/

    /**
     * infrastrukturaController.create()
     */
    create: function (req, res) {
        var infrastruktura = new infrastrukturaModel({
			lokacija : req.body.lokacija,
			stanje_vozisca : req.body.stanje,
			hitrost : req.body.hitrost,
			razmik : req.body.razmik

        });

        infrastruktura.save(function (err, infrastruktura) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating infrastruktura',
                    error: err
                });
            }
            return res.status(201).json(infrastruktura);
        });
    },

    /**
     * infrastrukturaController.update()
     */
    update: function (req, res) {
        var id = req.params.id;
        infrastrukturaModel.findOne({_id: id}, function (err, infrastruktura) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting infrastruktura',
                    error: err
                });
            }
            if (!infrastruktura) {
                return res.status(404).json({
                    message: 'No such infrastruktura'
                });
            }

            infrastruktura.lokacija = req.body.lokacija ? req.body.lokacija : infrastruktura.lokacija;
			infrastruktura.stanje_vozisca = req.body.stanje_vozisca ? req.body.stanje_vozisca : infrastruktura.stanje_vozisca;
			infrastruktura.hitrost = req.body.hitrost ? req.body.hitrost : infrastruktura.hitrost;
			infrastruktura.razmik = req.body.razmik ? req.body.razmik : infrastruktura.razmik;
			
            infrastruktura.save(function (err, infrastruktura) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating infrastruktura.',
                        error: err
                    });
                }

                return res.json(infrastruktura);
            });
        });
    },

    /**
     * infrastrukturaController.remove()
     */
    remove: function (req, res) {
        var id = req.params.id;
        infrastrukturaModel.findByIdAndRemove(id, function (err, infrastruktura) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the infrastruktura.',
                    error: err
                });
            }
            return res.status(204).json();
        });
    }
};
