var MongoClient = require('mongodb').MongoClient;
var mongoose = require("mongoose");
const express = require("express");
const assert = require('assert');

var app = require('express')();
var http = require('http').createServer(app);
var io = require('socket.io')(http);

app.get('/', (req, res) => {
  var pot = (__dirname + '/');
  pot = pot.substring(0,pot.length);
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


    var nekaj = result[1];
    nekaj = nekaj._id.toString();
    console.log(nekaj);

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
/*
longitudes.push("15.653236");
longitudes.push("15.653150");
longitudes.push("15.653816");
longitudes.push("15.654954");
longitudes.push("15.655018");
latitudes.push("46.563006");
latitudes.push("46.563788");
latitudes.push("46.564349");
latitudes.push("46.564349");
latitudes.push("46.559943");
*/
io.on('connection', (socket) => {
  console.log('povezal se je odjemalec');

  socket.on('RequestUpdate', (msg) => {
    console.log("zahtevek");
    //moram poracunat razlike v stanju vozisc, funkcija
    //posljem na socket
    io.emit('izris', longitudes, latitudes);

  });

  socket.on('disconnect', () => {
    console.log('odjemalec se je odvezal');
  });
});
