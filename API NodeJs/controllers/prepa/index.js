module.exports = function(app){
  require('./PrepaActivity.js')(app);
  require('./PrepaListActivity.js')(app);
  require('./PrepaScanActivity.js')(app);
}
