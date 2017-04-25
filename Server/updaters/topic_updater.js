const dynamo_api = require("../libs/dynamo_api.js");

/** Handles the creation/deletion topic requests */

exports.topic_handler = function topic_handler(list_msg) {
    /** Handles creation or deletion for each msg in list_msg  */


    list_msg.forEach(function (msg) {

        var msgJSON = JSON.parse(msg.Body);

        if (msgJSON.type == "CREATE") {
            console.log("CREATION: ", msgJSON);
            dynamo_api.create_topic(msgJSON.topicName, msgJSON.userID);
        }

        else { // DELETE
            console.log("DELETION: ", msgJSON);
            dynamo_api.delete_topic(msgJSON.topicName, msgJSON.userID)
        }

    }, this);

}

