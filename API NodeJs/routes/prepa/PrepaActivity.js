module.exports = function(app){
  
    //Recuperation des champs necessaires
    app.post('/neededFields', async function(req, res){
      var request = neededFields(req.body.foretagkod,
                                 req.body.ordernr,
                                 req.body.q_gclibrubrique);
      await app.executeSQLQuery(req, res, request);
    });

    // Remplissage des champs
    app.post('/fillFields', async function(req, res){
      var request = fillFields(req.body.foretagkod,
                               req.body.ordernr,
                               req.body.dummyuniqueid,
                               req.body.ordradnr,
                               req.body.ordrestnr,
                               req.body.ordradnrstrpos,
                               req.body.batchid);

      await app.executeSQLQuery(req, res, request);
    });

    // Verif batchid scanné
    app.post('/verifLot', async function(req, res){

      var request = verifLot(req.body.artnr,
                             req.body.batchid );

      await app.executeSQLQuery(req, res, request);
    });

    // Recuperation ua 2, 6 et 8
    app.post('/getUa268', async function(req, res){

      var request = getUa268(req.body.artnr,
                             req.body.batchid );

      await app.executeSQLQuery(req, res, request);
    });


    // Bouton valider
    app.post('/validateBtn', async function(req, res){

      var request = validateBtn(req.body.foretagkod,
                                req.body.q_gcbp_ua1,
                                req.body.q_gcbp_ua3,
                                req.body.q_gcbp_ua5,
                                req.body.q_gcbp_ua7,
                                req.body.q_gcbp_ua9,
                                req.body.batchid,
                                req.body.ordernr,
                                req.body.q_gclibrubrique,
                                req.body.dummyuniqueid,
                                req.body.ordradnr,
                                req.body.ordrestnr,
                                req.body.ordradnrstrpos );
      console.log(request);

      await app.executeSQLQuery(req, res, request);
    });

    // Bouton rupture
    app.post('/ruptureBtn', async function(req, res){

      var request = ruptureBtn();

      await app.executeSQLQuery(req, res, request);
    });

    // Bouton recharge
    app.post('/rechargeBtn', async function(req, res){

      var request = rechargeBtn();

      await app.executeSQLQuery(req, res, request);
    });

    // Bouton etiquette manquante
    app.post('/etiqMqtBtn', async function(req, res){

      var request = etiqMqtBtn();

      await app.executeSQLQuery(req, res, request);
    });
}












function neededFields (foretagkod, ordernr, q_gclibrubrique){
  return `SELECT	q_2bt_prepa.batchid,
                  q_2bt_prepa.OrderNr,
                  q_2bt_prepa.ordradnr,
                  q_2bt_prepa.OrdRadNrStrPos,
                  q_2bt_prepa.ordrestnr,
                  q_2bt_prepa.DummyUniqueId

              FROM q_2bt_prepa WITH(NOLOCK)

              WHERE q_2bt_prepa.foretagkod      = ${foretagkod}
              AND   q_2bt_prepa.ordernr         = ${ordernr}
              AND   q_2bt_prepa.q_gclibrubrique = ${q_gclibrubrique}`
}

function fillFields (foretagkod, ordernr, dummyuniqueid, ordradnr, ordrestnr, ordradnrstrpos, batchid){
  return `SELECT  ar.Lagstalle,
                  q_2bt_prepa.OrderNr,
                  q_2bt_prepa.OrdRadNr,
                  ar.q_gcar_lib1,
                  orp.q_unitefac,
                  q_2bt_prepa.q_gcbp_ua1,
                  q_2bt_prepa.q_gcbp_ua2,
                  q_2bt_prepa.q_gcbp_ua3,
                  q_2bt_prepa.q_gcbp_ua5,
                  q_2bt_prepa.q_gcbp_ua6,
                  q_2bt_prepa.q_gcbp_ua7,
                  q_2bt_prepa.q_gcbp_ua8,
                  q_2bt_prepa.q_gcbp_ua9,
                  fr.ftgnamn,
                  q_2bt_prepa.q_gcbp_ua1_2,
                  q_2bt_prepa.q_gcbp_ua2_2,
                  q_2bt_prepa.q_gcbp_ua3_2,
                  q_2bt_prepa.q_gcbp_ua5_2,
                  q_2bt_prepa.q_gcbp_ua6_2,
                  q_2bt_prepa.q_gcbp_ua7_2,
                  q_2bt_prepa.q_gcbp_ua8_2,
                  q_2bt_prepa.q_gcbp_ua9_2,
                  ar.artnr,
                  ar.artftgspec2,
                  ar.artftgspec3

              FROM q_2bt_prepa WITH(NOLOCK)

              INNER JOIN ar ar WITH(NOLOCK)
                ON	q_2bt_prepa.artnr		    = ar.artnr
                AND q_2bt_prepa.ForetagKod  = ar.ForetagKod

              INNER JOIN orp orp WITH(NOLOCK)
                  ON	q_2bt_prepa.ForetagKod	  	= orp.ForetagKod
                  AND q_2bt_prepa.OrderNr		    	= orp.OrderNr
                  AND q_2bt_prepa.ordradnr    		= orp.ordradnr
                  AND q_2bt_prepa.OrdRadNrStrPos  = orp.OrdRadNrStrPos
                  AND q_2bt_prepa.ordrestnr		    = orp.ordrestnr

             INNER JOIN fr fr WITH(NOLOCK)
                ON	q_2bt_prepa.ftgnr		    = fr.FtgNr
                AND q_2bt_prepa.ForetagKod  = fr.ForetagKod

              WHERE q_2bt_prepa.foretagkod	    = ${foretagkod}
              AND	  q_2bt_prepa.ordernr		      = ${ordernr}
              AND	  q_2bt_prepa.dummyuniqueid		= '${dummyuniqueid}'
              AND	  q_2bt_prepa.ordradnr		    = ${ordradnr}
              AND	  q_2bt_prepa.ordrestnr		    = ${ordrestnr}
              AND	  q_2bt_prepa.ordradnrstrpos  = ${ordradnrstrpos}
              AND	  q_2bt_prepa.batchid		      = '${batchid}'`
}


function verifLot (artnr, batchid){
  return `SELECT ISNULL(
            (
                SELECT TOP 1 1
                FROM bat WITH(NOLOCK)
                WHERE (
                      batchid = '${batchid}'
                 AND  artnr   = '${artnr}'
                )
            )
            ,0) as exist;`
}

function getUa268 (artnr, batchid){
  return `  SELECT  q_gcbp_ua2,
                    q_gcbp_ua6,
                    q_gcbp_ua8
            FROM bat WITH(NOLOCK)
            WHERE (
                 batchid = '${batchid}'
            AND  artnr   = '${artnr}'
            );`
}



function validateBtn (foretagkod,
                      ua1,
                      ua3,
                      ua5,
                      ua7,
                      ua9,
                      batchid,
                      ordernr,
                      q_gclibrubrique,
                      dummyuniqueid,
                      ordradnr,
                      ordrestnr,
                      ordradnrstrpos){
  return `UPDATE q_2bt_prepa

              SET q_gcbp_ua1 = ${ua1},
              SET q_gcbp_ua2 = ${ua3} / ${ua1},
              SET q_gcbp_ua3 = ${ua3},
              SET q_gcbp_ua5 = ${ua5},
              SET q_gcbp_ua7 = ${ua7},
              SET q_gcbp_ua9 = ${ua9},
              SET batchid    = ${batchid},
              SET q_pal_code = EXEC q_2bp_RRH_GetCodePalette (${foretagkod},${ordernr},${q_gclibrubrique})

      		WHERE foretagkod	    = ${foretagkod}
      		AND	  ordernr		      = ${ordernr}
      		AND	  dummyuniqueid		= '${dummyuniqueid}'
      		AND	  ordradnr		    = ${ordradnr}
      		AND	  ordrestnr		    = ${ordrestnr}
      		AND	  ordradnrstrpos  = ${ordradnrstrpos}`
}



function ruptureBtn (foretagkod){
  return ``
}



function rechargeBtn (foretagkod){
  return ``
}



function etiqMqtBtn (foretagkod){
  return ``
}
