var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var infrastrukturaSchema = new Schema({
	'lokacija' : String,
	'stanje_vozisca' : String,
	'hitrost' : String,
	'razmik' : String
});

module.exports = mongoose.model('infrastruktura', infrastrukturaSchema);
