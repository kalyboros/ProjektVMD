var longitudeModel = require('../models/longitudeModel.js');

/**
 * longitudeController.js
 *
 * @description :: Server-side logic for managing longitudes.
 */
module.exports = {

    /**
     * longitudeController.list()
     */
    list: function (req, res) {
        longitudeModel.find(function (err, longitudes) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting longitude.',
                    error: err
                });
            }
            return res.json(longitudes);
        });
    },

    /**
     * longitudeController.show()
     */
    show: function (req, res) {
        var id = req.params.id;
        longitudeModel.findOne({_id: id}, function (err, longitude) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting longitude.',
                    error: err
                });
            }
            if (!longitude) {
                return res.status(404).json({
                    message: 'No such longitude'
                });
            }
            return res.json(longitude);
        });
    },

    /**
     * longitudeController.create()
     */
    create: function (req, res) {
        var longitude = new longitudeModel({
			longitude : req.body.longitude,
            latitude : req.body.latitude,
            pospesek : 5

        });

        longitude.save(function (err, longitude) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating longitude',
                    error: err
                });
            }
            return res.status(201).json(longitude);
        });
    },

    /**
     * longitudeController.update()
     */
    update: function (req, res) {
        var id = req.params.id;
        longitudeModel.findOne({_id: id}, function (err, longitude) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting longitude',
                    error: err
                });
            }
            if (!longitude) {
                return res.status(404).json({
                    message: 'No such longitude'
                });
            }

            longitude.number = req.body.number ? req.body.number : longitude.number;
			
            longitude.save(function (err, longitude) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating longitude.',
                        error: err
                    });
                }

                return res.json(longitude);
            });
        });
    },

    /**
     * longitudeController.remove()
     */
    remove: function (req, res) {
        var id = req.params.id;
        longitudeModel.findByIdAndRemove(id, function (err, longitude) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the longitude.',
                    error: err
                });
            }
            return res.status(204).json();
        });
    }
};
