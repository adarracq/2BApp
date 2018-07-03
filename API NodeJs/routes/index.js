//default/index.js
//prepa/index.js

module.exports = function(app){
  require('./default/index.js')(app);
  require('./prepa/index.js')(app);
}
