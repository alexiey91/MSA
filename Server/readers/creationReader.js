/** 
 * This process reads from creationQueue incoming request for topic creation/deletion
 * 
 */
const polling_api = require('../utils/polling.js');

polling_api.polling(5000,"creationQueue");