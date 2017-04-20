var AWS = require('aws-sdk');
const cp = require('child_process');
const msgtmpl = require('../utils/messageTmpl.js');

AWS.config.loadFromPath('./utils/config.json');

// Create an SQS service object
var sqs = new AWS.SQS({ apiVersion: '2012-11-05' });

/** SQS API */

exports.createQueue = function createReadQueue(queue_name) {
    /** Creation of queue with queue_name if not exists */

    var params = {
        QueueNamePrefix: queue_name
    }
    sqs.listQueues(params, function (err, data) {
        if (err) console.log(err, err.stack); // an error occurred
        else if (typeof data.QueueUrls === "undefined") {
            var params_queue = {
                QueueName: queue_name,
                Attributes: {
                    'MessageRetentionPeriod': '14400'
                }
            };

            sqs.createQueue(params_queue, function (err, data) {
                if (err) {
                    console.log("Error", err);
                } else {
                    console.log("Success", data.QueueUrl);
                }
            });
        }
        else {
            console.log("Queue already exists: ", data);
        }
    });
}

exports.writeQueue = function writeQueue(queue_name, msg) {
    /** Write message on queue */

    var params = {
        QueueName: queue_name
    };

    sqs.getQueueUrl(params, function (err, data) {

        if (err) {
            console.log("Error", err);
        } else {
            console.log("Success", data.QueueUrl);
        }

        var params = {
            DelaySeconds: 10,
            MessageBody: msg,
            QueueUrl: data.QueueUrl
        };

        sqs.sendMessage(params, function (err, data) {
            if (err) {
                console.log("Error", err);
            } else {
                console.log("Success", data.MessageId);
            }
        });
    });
}


exports.readQueue = function readQueue(queue_name) {
    /** Read incoming messages from queue_name  */

    var params = {
        QueueName: queue_name
    };

    sqs.getQueueUrl(params, function (err, data) {

        if (err) {
            console.log("Error", err);
        } else {
            console.log("Success", data.QueueUrl);
        }

        var params = {
            AttributeNames: [
                "All"
            ],
            MaxNumberOfMessages: 10,
            MessageAttributeNames: [
                "All"
            ],
            QueueUrl: data.QueueUrl,
            VisibilityTimeout: 3,
            WaitTimeSeconds: 0
        };

        sqs.receiveMessage(params, function (err, data_read) {
            if (err) {
                console.log("Error receiving messages", queue_name);
            } else if (typeof data_read.Messages === 'undefined') {
                console.log("Empty Queue", queue_name);
            } else {

                const rec = cp.fork('./workers/consumer.js');

                rec.send({ data: data_read.Messages , queue: queue_name});

                var entries = [];
                var index = 0;

                data_read.Messages.forEach(function (element) {
                    entries.push({ Id: msgtmpl.messageId[index], ReceiptHandle: element.ReceiptHandle });
                    index++;
                }, this);

                /** to delete messages */
                deleteMessages(data.QueueUrl, entries);
            }
        });
    });
}

function deleteMessages(queueurl, entries) {
    /** Delete entries from queue by queueurl */

    var params = {
        Entries: entries,
        QueueUrl: queueurl
    };
    sqs.deleteMessageBatch(params, function (err, data) {
        if (err) console.log(err, err.stack); // an error occurred
        else console.log(data);           // successful response
    });
}