/** Receives all messages read from queues */

process.once('message', m => {

    m.data.forEach(function (element) {
        console.log(element);

        switch (element.MessageAttributes.MsgType.StringValue) {
            case "C":
                console.log("CREATION\n");
                break;

            case "N":
                console.log("NOTIFICATION\n");
                break;

            case "S":
                console.log("SUBSCRIPTION\n");
                break;

            default:
                break;
        }
    }, this);

});