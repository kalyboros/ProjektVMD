
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

      
      //shranim lokacije v polje
      var lokacijaS = result[i];
      lokacijaS = lokacijaS.lokacija;
      locations.push(lokacijaS);
      //shranim stanja v polje
      var stanjeS = result[i];
      stanjeS = stanjeS.stanje_vozisca;
      status.push(stanjeS);
      //console.log(stanjeS);
      //shranim hitrosti
      var hitrostS = result[i];
      hitrostS = hitrostS.hitrost;
      speeds.push(parseFloat(hitrostS));
      //shranim razmike
      var razmikS = result[i];
      razmikS = razmikS.razmik;
      razmik.push(razmikS);      


    }

    client.close();
    console.log("zapiram povezavo")
    //pretvorim result v tako dato da jo lahko analiziram -> array
  });     
});

//po tem ko parsam podatke iz baze jih se moram spremeniti v obliko katero bo lahko client obdelal in displayal
  
function onlyUnique(value, index, self) { 
    return self.indexOf(value) === index;
}

function average(list) {
    let total = 0;

    for (num of list) {
        total += num;
    }

    return total/list.length;
}

//server-client logika
io.on('connection', (socket) => {
  console.log('povezal se je odjemalec');

  socket.on('RequestUpdate', (msg) => {

    //izracun hitrosti za vsak kraj
    var kumulativneHitrosti = [];
    var uniqueLokacija = locations.filter( onlyUnique );

    for(var i = 0; i <= speeds.length; i++){
      //console.log(kumulativneHitrosti[i]);
      //console.log(hitrost[i]);
      var pre = 0;
      for(var j = 0; j < i; j++){
        pre = pre + parseInt(speeds[j]);
      }
      if(i != 0){
        var kumulativna = parseInt(speeds[i-1])+pre;
      }else{
        var kumulativna = parseInt(speeds[i]);
      }
      //console.log(typeof hitrost[i]);

      kumulativneHitrosti.push(kumulativna);
    }
    io.emit('FinalizeLineChart', kumulativneHitrosti, uniqueLokacija);

    //varianca razmika, najvecji razmik, najmanjsi, povprečni razmik
    var povprRazmik = 0;
    var najmanjsiRazmik = 1000;
    var najvecjiRazmik = 0;
    for(var i = 0; i < razmik.length; i++ ){
        povprRazmik += parseFloat(razmik[i]);
        stevilka = parseFloat(razmik[i]);
        if(stevilka < najmanjsiRazmik){
          najmanjsiRazmik = stevilka;
        }
        if(stevilka > najvecjiRazmik && stevilka < 350){
          najvecjiRazmik = stevilka;
        }

        //console.log(razmik[i]);
        //console.log(typeof razmik[i]);
    }

    var varianca = najvecjiRazmik - najmanjsiRazmik;
    povprRazmik = povprRazmik/razmik.length;
    //console.log(povprRazmik + " " + najvecjiRazmik + " " + varianca);

    io.emit('FinalizeDistanceChart', najmanjsiRazmik, najvecjiRazmik, varianca);

    //povprecne hitrosti
    var locationSpeeds = {};

    for (locationIndex in locations) {

        location = locations[locationIndex];
        speed = speeds[locationIndex];

        locationSpeeds[location] = ( locationSpeeds[location] || [] ).concat([speed])
    }

    var uniqueLocations = [];
    var averageSpeeds = [];

    for (location of Object.keys(locationSpeeds)) {
      //Object.keys(locationSpeeds);
      uniqueLocations.push(location);
      averageSpeeds.push(average(locationSpeeds[location]));
    }

    io.emit('AverageSpeedsChart', uniqueLocations, averageSpeeds);

    //stanje vozisc
    var statusCounts = {};

    for (s of status) {
        statusCounts[s] = ( statusCounts[s] || 0 ) + 1
    }

    var uniqueStatus = Object.keys(statusCounts);
    var counts = [];

    for (status of uniqueStatus) {
        counts.push(statusCounts[status])
    }

    io.emit('RoadStatusChart',uniqueStatus,counts);

    //standardni odklon ipd
    var averageSpeed = 0;
    for(var i = 0; i < speeds.length; i++){
      averageSpeed += speeds[i];
    }
    averageSpeed = averageSpeed/speeds.length;

    var odklon = 0;
    for(var i = 0; i < speeds.length; i++){
      odklon = (speeds[i] + averageSpeed)
      odklon = Math.pow(odklon,2);
    }
    odklon = odklon/speeds.length;
    odklon = Math.sqrt(odklon);
    //console.log(averageSpeed);
    io.emit('FinalizeBarDataChart',averageSpeed, povprRazmik, odklon);


  });

  socket.on('disconnect', () => {
    console.log('odjemalec se je odvezal');
  });

});
