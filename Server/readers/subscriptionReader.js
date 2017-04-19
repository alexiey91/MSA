/** 
 * This process reads from subscriptionQueue incoming request for topic subscription/unsubscription
 * 
 */
const polling_api = require('../utils/polling.js');

polling_api.polling(5000,"subscriptionQueue");