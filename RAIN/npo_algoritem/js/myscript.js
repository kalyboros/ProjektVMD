
//baza stuff
var MongoClient = require('mongodb').MongoClient;
var mongoose = require("mongoose");
const express = require("express");
const assert = require('assert');


//server stuff
var app = require('express')();
var http = require('http').createServer(app);
var io = require('socket.io')(http);

//serverji
app.get('/', (req, res) => {
  var pot = (__dirname + '/');
  pot = pot.substring(0,pot.length - 3);
  res.sendFile( pot + 'algoritem.html');
});

http.listen(7000, () => {
  console.log('streznik tece na *:7000');
});


//povezovanje z bazo
var url = "mongodb://127.0.0.1:27017";
const dbName = 'projekt_database';
var longitudes = [];
var latitudes = [];
var timestamps = [];

var longDiffs = [];
var latDiffs = [];
var vectorDiffsX = [];
var vectorDiffsY = [];
var timeDiff = [];
var nC = [];

var boolLong = [];
var boolLat = [];

var prevoznost = [];
var status = [];


MongoClient.connect("mongodb://127.0.0.1:27017", function (err, client) {   
  if(err) throw err;
  console.log("povezano");
  const db = client.db(dbName);

  db.collection("longitudes").find({}).toArray(function(err, result) {
    if (err) throw err;
    var dolzina = result.length;

    var i;
    for(i = 0; i < dolzina; i++){
      var longS = result[i];
      longS = longS.longitude;
      longitudes.push(longS);

      var latS = result[i];
      latS = latS.latitude;
      latitudes.push(latS);

      var timeS = result[i];
      timeS = timeS.time_stamp;
      timestamps.push(timeS);
      //console.log(timestamps[i]);
    }

    client.close();
    console.log("zapiram povezavo")
  });     
});

function algoritem(){
  //racunal bom razlike na 3 in 4 mestu po long/lat
  for(var i = 0; i < longitudes.length; i++){
    var integerP = parseFloat(longitudes[i-1]) * 1000000;
    var integerN = parseFloat(longitudes[i]) * 1000000;
    var diff = Math.abs(integerN-integerP)
    if(i == 0){
      longDiffs.push(0);
    }
    if(i != 0){
      longDiffs.push(diff);
    }
    
    //console.log(longDiffs[i]);
  }

  for(var i = 0; i < latitudes.length; i++){
    var integerP = parseFloat(latitudes[i-1]) * 1000000;
    var integerN = parseFloat(latitudes[i]) * 1000000;
    var diff = Math.abs(integerN-integerP)
    if(i == 0){
      latDiffs.push(0);
    }
    if(i != 0){
      latDiffs.push(diff);
    }
    
    //console.log(latDiffs[i]);
  }

  //racun vektorjev glede na prejsnje koordinate
  for(var i = 0; i < latitudes.length; i++){
    var Latp = parseFloat(latitudes[i-1]) * 1000000;
    var Latn = parseFloat(latitudes[i]) * 1000000;

    var Longp = parseFloat(longitudes[i-1]) * 1000000;
    var Longn = parseFloat(longitudes[i]) * 1000000;

    var diffX = Math.abs(Latn - Latp);
    var diffY = Math.abs(Longn - Longp);

    if(i == 0){
      vectorDiffsX.push(0);
      vectorDiffsY.push(0);
    }else{
      vectorDiffsX.push(diffX);
      vectorDiffsY.push(diffY);
    } 
  }


  //razlike v času
  for(var i = 0; i < timestamps.length; i++){
    //console.log(timestamps[i].substr(6,8));
    var sum = i+1;
    nC.push(sum);

    if(i == 0){
      timeDiff.push(0);
    }else{
      if(parseInt(timestamps[i].substr(4,6)) > parseInt(timestamps[i-1].substr(4,6))){
        var prevDiff = Math.abs(60 - parseInt(timestamps[i-1].substr(6,8)));
        timeDiff.push(prevDiff + parseInt(timestamps[i].substr(6,8)));
      }else{
        timeDiff.push(parseInt(timestamps[i].substr(6,8)) - parseInt(timestamps[i-1].substr(6,8)));
      }
      
    }
    //console.log(timeDiff[i]);
  }

  //še štetje odmikov in sklep ali je vozišče dobro ali slabo
  var gr1 = 0;
  var gr2 = 0;
  for(var i = 0; i < latDiffs.length; i++){
    if (latDiffs[i] < 350) {
      boolLat.push(0);
    }else{
      boolLat.push(1);
      gr1++;
    }

    if(longDiffs[i] < 350){
      boolLong.push(0);
    }else{
      boolLong.push(1);
      gr2++;
    }
  }

  var n = latDiffs.length;
  n = parseInt(n/2);
  if (n > gr2 && n > gr1 ) {
    console.log("Vozisce je dobro prevozno");
    prevoznost.push(latDiffs.length);
    prevoznost.push(gr1);
    prevoznost.push(gr2);
    status.push(1);
  }else{
    console.log("Vozisce je slabo prevozno");
    prevoznost.push(latDiffs.length);
    prevoznost.push(gr1);
    prevoznost.push(gr2);
    status.push(0);
  }



}


//server-client logika
io.on('connection', (socket) => {
  console.log('povezal se je odjemalec');

  socket.on('RequestUpdate', (msg) => {
    //console.log(n);
    //moram poracunat razlike v stanju vozisc, funkcija
    algoritem();
    //posljem na socket
    io.emit('prevoznostCesteLong',timestamps,longDiffs);
    io.emit('prevoznostCesteLat',timestamps,latDiffs);
    io.emit('vektorji', vectorDiffsX, vectorDiffsY);
    io.emit('CasovneRazlike', timeDiff,nC);
    io.emit('prevoznost', prevoznost,status);

  });

  socket.on('disconnect', () => {
    console.log('odjemalec se je odvezal');
  });

});
