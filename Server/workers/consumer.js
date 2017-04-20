/** Receives all messages read from queues */

process.once('message', m => {

    console.log("QUEUE NAME:", m.queue , "\n\n");

    m.data.forEach(function (element) {
        console.log("MESSAGE BODY:", element.Body, "\n\n");
    }, this);

});