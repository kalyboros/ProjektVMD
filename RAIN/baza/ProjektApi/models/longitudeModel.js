var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var longitudeSchema = new Schema({
	'longitude' : String,
	'latitude' : String,
	'pospesek' : String
});

module.exports = mongoose.model('longitude', longitudeSchema);
