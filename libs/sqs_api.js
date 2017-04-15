var AWS = require('aws-sdk');

AWS.config.loadFromPath('./utils/config.json');

// Create an SQS service object
var sqs = new AWS.SQS({ apiVersion: '2012-11-05' });


exports.createQueue = function createReadQueue(queue_name) {
    /** Creation of queue with queue_name if not exists */

    var params = {
        QueueNamePrefix: queue_name
    };
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

exports.writeQueue = function writeQueue(queue_name, topic, message) {
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
            MessageAttributes: {
                "Topic": {
                    DataType: "String",
                    StringValue: "topic"
                }
            },
            MessageBody: message,
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
            MaxNumberOfMessages: 1,
            MessageAttributeNames: [
                "All"
            ],
            QueueUrl: data.QueueUrl,
            VisibilityTimeout: 0,
            WaitTimeSeconds: 0
        };

        sqs.receiveMessage(params, function (err, data_read) {
            if (err) {
                console.log("Receive Error", err);
            } else if (typeof data_read.Messages === 'undefined') {
                console.log("Empty Queue", queue_name);
            } else {
                console.log("data_read_by_queue:", data_read);

                var deleteParams = {
                    QueueUrl: data.QueueUrl,
                    ReceiptHandle: data_read.Messages[0].ReceiptHandle
                };
                sqs.deleteMessage(deleteParams, function (err, data) {
                    if (err) {
                        console.log("Delete Error", err);
                    } else {
                        console.log("Message Deleted", data);
                    }
                });
            }
        });

    });
}
