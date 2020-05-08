var infrastrukturaModel = require('../models/infrastrukturaModel.js');

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
        infrastrukturaModel.find(function (err, infrastrukturas) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting infrastruktura.',
                    error: err
                });
            }
            return res.json(infrastrukturas);
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

    /**
     * infrastrukturaController.create()
     */
    create: function (req, res) {
        var infrastruktura = new infrastrukturaModel({
			lokacija : req.body.lokacija,
			stanje_vozisca : req.body.stanje_vozisca,
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
