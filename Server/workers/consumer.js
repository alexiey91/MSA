const tp_upd = require("../updaters/topic_updater.js");
const nt_upd = require("../updaters/notification_updater.js");
const sb_upd = require("../updaters/subscription_updater.js");

/** Receives all messages read from queues */

process.once('message', m => {

    console.log("QUEUE NAME:", m.queue, "\n");

    switch (m.queue) {
        case "creationQueue":
            tp_upd.topic_handler(m.data);
            break;

        case "notificationQueue":
            nt_upd.notification_handler(m.data);
            break;

        case "subscriptionQueue":
            sb_upd.subscription_handler(m.data);
            break;

        default:
            break;
    }
});