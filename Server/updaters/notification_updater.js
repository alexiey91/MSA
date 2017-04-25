const sqs_api = require("../libs/dynamo_api.js");

/** Handles the incoming notifications */

exports.notification_handler = function notification_handler(list_msg) {
    /** Handles the notification in each msg of list_msg  */


    list_msg.forEach(function (msg) {

        var msgJSON = JSON.parse(msg.Body);

        // only publish messages
        // publish message on topic if and only if topic exists in table Topic
        dynamo_api.send_notification(msgJSON.topicName, msgJSON.messageBody);

    }, this);

}
