const express = require('express');
const app = express();
const routes = require('./routing/routes.js');

app.use('/pubSub', routes);

app.listen(8080, function () {
  console.log('PubSub app listening on port 8080!');
});