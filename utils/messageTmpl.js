/** SQS messages template and utils */

// Messages Id (max 10) -- Usefull for deleteMessageBatch
var messageId = ["FirstMessage", "SecondMessage", "ThirdMessage", "FourthMessage", "FifthMessage", "SixthMessage",
    "EighthMessage", "NinthMessage", "TenthMessage"];

exports.messageId = messageId;


/**
 * MESSAGE FORMAT:
 * 
 *      NOTIFICATIONQUEUE:
 *      
 *      CREATIONQUEUE:
 * 
 *          var params = {
            DelaySeconds: 10,
            MessageAttributes: {
                "MsgType": {
                    DataType: "String",
                    StringValue: "C" // Creation
                },
                "TopicName": {
                    DataType: "String",
                    StringValue: "..." // Required
                },
                "UserId": {
                    DataType: "String",
                    StringValue: "..." // Required
                }
            },
            MessageBody: "...", // Required
            QueueUrl: data.QueueUrl
        };
 * 
 * 
 * 
 * 
 *      SUBSCRIPTIONQUEUE:
 * 
 */