var express = require('express');
var router = express.Router();
var infrastrukturaController = require('../controllers/infrastrukturaController.js');

/*
 * GET
 */
router.get('/', infrastrukturaController.list);
//router.get('/infra', infrastrukturaController.showInfra);
router.get('/posodobi', infrastrukturaController.dodajPodatke);

/*
 * GET
 */
router.get('/:id', infrastrukturaController.show);

/*
 * POST
 */
router.post('/', infrastrukturaController.create);

/*
 * PUT
 */
router.put('/:id', infrastrukturaController.update);

/*
 * DELETE
 */
router.delete('/:id', infrastrukturaController.remove);

module.exports = router;
