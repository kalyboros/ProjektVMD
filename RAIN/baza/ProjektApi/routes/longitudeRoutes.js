var express = require('express');
var router = express.Router();
var longitudeController = require('../controllers/longitudeController.js');

/*
 * GET
 */
router.get('/', longitudeController.list);

/*
 * GET
 */
router.get('/:id', longitudeController.show);

/*
 * POST
 */
router.post('/:param', longitudeController.create);

/*
 * PUT
 */
router.put('/:id', longitudeController.update);

/*
 * DELETE
 */
router.delete('/:id', longitudeController.remove);

module.exports = router;
