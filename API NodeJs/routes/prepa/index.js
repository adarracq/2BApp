/*prepa.js
prepascan.js
prepalist.js*/

module.exports = function(app){
  require('./PrepaActivity.js')(app);
  require('./PrepaListActivity.js')(app);
  require('./PrepaScanActivity.js')(app);
}
