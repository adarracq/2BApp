const express = require('express');
const bodyParser = require('body-parser');
var app = express();

app.use(bodyParser.json());


app.executeSQLQuery = async function(req, res, query){
  let sql = require('mssql');
  let dbConfig = {
    user: req.body.user,
    password: req.body.password,
    server: '192.168.0.3',
    database: req.body.bdd
  };
  let sc = new sql.ConnectionPool(dbConfig);
  await sc.connect();
  let rq = new sql.Request(sc);
  rq.query(query, function(err, resp) {
    if (err) {
      console.log("Erreur lors de l'exécution d'une requête SQL : - " + err);
    }else{
      res.send(JSON.stringify(resp));
    }
    sc.close();
  })
}

var routesManager = require('./routes/index.js')(app);


// Start server and listen on http://localhost:8081/
var server = app.listen(8081, function () {
    var host = server.address().address
    var port = server.address().port

    console.log("app listening at http://%s:%s", host, port)
});
