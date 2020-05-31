var MongoClient = require('mongodb').MongoClient;
var mongoose = require("mongoose");
const express = require("express");
const assert = require('assert');

var app = require('express')();
var http = require('http').createServer(app);
var io = require('socket.io')(http);

app.get('/', (req, res) => {
  var pot = (__dirname + '/');
  pot = pot.substring(0,pot.length - 3);
  res.sendFile( pot + 'gps.html');
});

http.listen(7000, () => {
  console.log('streznik tece na *:7000');
});

var url = "mongodb://127.0.0.1:27017";
const dbName = 'projekt_database';
var longitudes = [];
var latitudes = [];

MongoClient.connect("mongodb://127.0.0.1:27017", function (err, client) {
  if(err) throw err;
  console.log("povezano");
  const db = client.db(dbName);

  db.collection("longitudes").find({}).toArray(function(err, result) {
    if (err) throw err;
    var dolzina = result.length;

    /*
    var nekaj = result[1];
    nekaj = nekaj._id.toString();
    console.log(nekaj);
    */
    var i;
    for(i = 0; i < dolzina; i++){
      var longS = result[i];
      longS = longS.longitude;
      longitudes.push(longS);

      var latS = result[i];
      latS = latS.latitude;
      latitudes.push(latS);

    }

    client.close();
    console.log("zapiram povezavo")
  });
});
