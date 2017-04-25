/** A signals handler */


exports.handle_signal = function handle_signal(lc, on_sig, then_sig) {
    /** Handle termination signals */
    /** @param lc is a list of child process
        @param on_sig is input signal
        @param then_sig is output signal
    */

    process.on(on_sig, (code) => {

        lc.forEach(function (element) {
            element.kill(then_sig);
        }, this);

        process.exit();
    });
}