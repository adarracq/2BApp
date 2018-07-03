module.exports = function(app){
    app.post('/connect', async function(req, res){
      await app.executeSQLQuery(req, res, "SELECT 1" );
    });

    //other routes..
}
