const express = require('express');
const router = express.Router();
const dynamoapi = require('../libs/dynamo_api.js');

const bodyParser = require('body-parser');
const urlencodedParser = bodyParser.urlencoded({ extended: false });

router.use(bodyParser.json());

router.use(function timeLog(req, res, next) {
  console.log('Time: ', Date.now());
  next();
});

router.get('/allTopics', urlencodedParser, function (req, res) {
  /** Returns all available topics */

  dynamoapi.scan_all_topics(res);
});


module.exports = router;