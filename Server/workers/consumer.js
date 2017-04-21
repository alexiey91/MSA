const tp_upd = require("../updaters/topic_updater.js");


/** Receives all messages read from queues */

process.once('message', m => {

    console.log("QUEUE NAME:", m.queue, "\n\n");


    switch (m.queue) {
        case "creationQueue":
            tp_upd.topic_handler(m.data);
            break;

        case "notificationQueue":
            break;

        case "subscriptionQueue":
            break;

        default:
            break;
    }
});