var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var longitudeSchema = new Schema({
	'longitude' : String,
	'latitude' : String,
	'pospesek' : String,
	'time_stamp' : Date
});

module.exports = mongoose.model('longitude', longitudeSchema);
