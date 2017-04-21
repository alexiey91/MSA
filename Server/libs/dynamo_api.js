var AWS = require('aws-sdk');
var sqs_api = require('./sqs_api.js');

AWS.config.loadFromPath('./utils/config.json'); // required


var dynamodbCl = new AWS.DynamoDB.DocumentClient();

// Speed up calls to hasOwnProperty
var hasOwnProperty = Object.prototype.hasOwnProperty;

function isEmpty(obj) {
    /** Return true if a dynamoDb read object is empty */
    // null and undefined are "empty"
    if (obj == null) return true;

    // Assume if it has a length property with a non-zero value
    // that that property is correct.
    if (obj.length > 0) return false;
    if (obj.length === 0) return true;

    // If it isn't an object at this point
    // it is empty, but it can't be anything *but* empty
    // Is it empty?  Depends on your application.
    if (typeof obj !== "object") return true;

    // Otherwise, does it have any properties of its own?
    // Note that this doesn't handle
    // toString and valueOf enumeration bugs in IE < 9
    for (var key in obj) {
        if (hasOwnProperty.call(obj, key)) return false;
    }

    return true;
}

exports.create_topic = function create_topic(topic_name, user_id) {
    /** Create a new topic named topic_name (if not exists) by a user with userID user_id
     */

    var tempParam = {
        TableName: "Topic",
        Key: {
            "topicName": topic_name
        }
    };

    dynamodbCl.get(tempParam, function (err, data) {
        if (err) {
            console.error("Unable to read item. Error JSON:", JSON.stringify(err, null, 2));
        }

        else if (!isEmpty(data)) {
            console.error("Item already exists:", topic_name);
        }

        else {

            var params = {
                TableName: "Topic",
                Item: {
                    "topicName": topic_name,
                    "userID": user_id
                }
            };

            dynamodbCl.put(params, function (err, data) {
                if (err) {
                    console.error("Unable to add topic", topic_name, ". Error JSON:", JSON.stringify(err, null, 2));
                } else {
                    console.log("PutItem succeeded:", topic_name);
                }
            });
        }
    });
}

exports.scanAllTopics = function scanAllTopics(res) {
    /** Scans all topicName in "Topic" table and send the response by res */

    var params = {
        TableName: "Topic",
        ProjectionExpression: "#tn",
        ExpressionAttributeNames: {
            "#tn": "topicName",
        }
    };

    /** Scan a maximum of 1 MB  */

    dynamodbCl.scan(params, function (err, data) {

        if (err) {
            console.error("Unable to scan the table. Error JSON:", JSON.stringify(err, null, 2));
        } else {

            console.log("Scan succeeded.");

            res.send(data.Items);
        }
    });

}

exports.deleteTopic = function deleteTopic(topic_name, user_id) {
    /** Deletes the topic from Topic table if and only if user_id is the userID of creator */

    var tempParam = {
        TableName: "Topic",
        Key: {
            "topicName": topic_name
        }
    };

    dynamodbCl.get(tempParam, function (err, data) {
        if (err) {
            console.error("Unable to read item. Error JSON:", JSON.stringify(err, null, 2));
        }

        else if (!isEmpty(data) && data.Item.userID == user_id) {
            dynamodbCl.delete(tempParam, function (err, data) {
                if (err) {
                    console.error("Unable to delete item. Error JSON:", JSON.stringify(err, null, 2));
                } else {
                    console.log("DeleteItem succeeded:", JSON.stringify(data, null, 2));
                }
            })
        }
    });
}

module.exports.dynamodbCl = dynamodbCl;