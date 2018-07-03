module.exports = function(app){

    // Verif code scanné
    app.post('/scan', async function(req, res){

      var request = scan(req.body.foretagkod,
                         req.body.ordernr );

      await app.executeSQLQuery(req, res, request);
    });
}




function scan (foretagkod, ordernr){
  return `SELECT ISNULL(
            (
                SELECT TOP 1 1
                FROM  q_2bv_rechprepatp WITH (NOLOCK)
                LEFT OUTER JOIN  [fr] WITH (NOLOCK)
                ON  q_2bv_rechprepatp.foretagkod=fr.foretagkod
                AND q_2bv_rechprepatp.ftgnr=fr.ftgnr

                WHERE (
                q_2bv_rechprepatp.foretagkod= ${foretagkod}
                 AND q_2bv_rechprepatp.ordernr= ${ordernr}
                )
            )
            ,0) as ok;`
}
