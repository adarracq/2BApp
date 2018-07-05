module.exports = function(app){

    // Chargement des lignes
    app.post('/loadLines', async function(req, res){

      var request = loadLines(req.body.foretagkod,
                              req.body.ordernr,
                              req.body.q_gclibrubrique );

      await app.executeSQLQuery(req, res, request);
    });
}








function loadLines (foretagkod, ordernr, q_gclibrubrique){
  return `SELECT	q_2bt_prepa.batchid,
                  ar.q_gcar_lib1,
                  orp.q_unitefac,
                  q_2bt_prepa.q_gcbp_ua1,
                  q_2bt_prepa.q_gcbp_ua3,
                  q_2bt_prepa.q_gcbp_ua9,
                  ISNULL(q_2bt_prepa.q_pal_code,0) as q_pal_code,
                  q_2bt_prepa.foretagkod,
                  q_2bt_prepa.OrderNr,
                  q_2bt_prepa.ordradnr,
                  q_2bt_prepa.OrdRadNrStrPos,
                  q_2bt_prepa.ordrestnr,
                  fr.ftgnamn

              FROM q_2bt_prepa WITH(NOLOCK)

              INNER JOIN ar ar WITH(NOLOCK)
                  ON	q_2bt_prepa.artnr		= ar.artnr
                  AND q_2bt_prepa.ForetagKod  = ar.ForetagKod

              INNER JOIN orp orp WITH(NOLOCK)
                  ON	q_2bt_prepa.ForetagKod		= orp.ForetagKod
                  AND q_2bt_prepa.OrderNr			= orp.OrderNr
                  AND q_2bt_prepa.ordradnr		= orp.ordradnr
                  AND q_2bt_prepa.OrdRadNrStrPos  = orp.OrdRadNrStrPos
                  AND q_2bt_prepa.ordrestnr		= orp.ordrestnr

              INNER JOIN fr fr WITH(NOLOCK)
                     ON	q_2bt_prepa.ftgnr		    = fr.FtgNr
                     AND q_2bt_prepa.ForetagKod  = fr.ForetagKod

              WHERE q_2bt_prepa.foretagkod	    = ${foretagkod}
              AND	  q_2bt_prepa.ordernr		      = ${ordernr}
              AND   q_2bt_prepa.q_gclibrubrique = ${q_gclibrubrique};`
}
