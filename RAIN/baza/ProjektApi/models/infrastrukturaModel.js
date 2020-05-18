var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var infrastrukturaSchema = new Schema({
	'lokacija' : String,
	'stanje_vozisca' : String,
	'hitrost' : Number,
	'razmik' : Number
});

module.exports = mongoose.model('infrastruktura', infrastrukturaSchema);
