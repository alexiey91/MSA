const dynamo_api = require("../libs/dynamo_api.js");

/** Handles the incoming subscriptions/unsubscriptions */

exports.subscription_handler = function subscription_handler(list_msg) {
    /** Handles the subscriptions/unsubscriptions in each msg of list_msg  */


    list_msg.forEach(function (msg) {

        var msgJSON = JSON.parse(msg.Body);

        if (msgJSON.type == "SUB") {
            dynamo_api.create_subscription(msgJSON.topicName, msgJSON.userID, msgJSON.filter);
        }

        else if (msgJSON.type == "UNSUB") {
            dynamo_api.delete_subscription(msgJSON.topicName, msgJSON.userID);
        }

        else {
            console.log("Do not support UNSUBALL\n");
        }

    }, this);

}


/** DYNAMO SUBSCRIPTION TABLE FORMAT:
 *  
 *  partition key       ---  sort key           ---  filter      
 *  topicName (string)  ---  userID (string)    ---  string   
 * 
 * 
 * 
 * 
 */