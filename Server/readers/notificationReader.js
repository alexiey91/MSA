/** 
 * This process reads from notificationQueue incoming request for publish messages
 * 
 */
const polling_api = require('../utils/polling.js');

polling_api.polling(5000,"notificationQueue");