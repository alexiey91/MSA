const dynamo_api = require('./libs/dynamo_api.js');
const sqs_api = require('./libs/sqs_api.js');

//sqs_api.writeQueue("ReadQueue","ebrei","Paolo è un grande ebreo.");
sqs_api.readQueue("ReadQueue");