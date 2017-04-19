/**
 * Polling of all queues
 */

const sqs_api = require('../libs/sqs_api.js');

exports.polling = function polling(mills, queue_name) {
    /** Polling of queue_name queue every mills milliseconds */

    sqs_api.readQueue(queue_name);

    setTimeout(function () {
        polling(mills, queue_name);
    }, mills);
}