package com.a2bsystemapp.a2bsystemapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.*;

import cz.msebera.android.httpclient.Header;


public class PrepaActivity extends AppCompatActivity {

    private android.widget.Button mValidButton;
    private android.widget.Button mRuptureButton;
    private android.widget.Button mRechargeButton;
    private android.widget.Button mEtiqButton;
    private android.widget.Button mRetourButton;

    private TextView mClient1;
    private TextView mClient2;

    private TextView mOrdernr;
    private TextView mOrdernr2;

    private TextView mLigne;

    private TextView mArticle1;
    private TextView mArticle2;

    private TextView mLot1;
    private EditText mLot2;

    private TextView mGamme;
    private TextView mGamme2;

    private TextView mColis1;
    private TextView mColis2;
    private EditText mColis3;

    private TextView mPiece1;
    private TextView mPiece2;
    private EditText mPiece3;

    private TextView mTarre1;
    private TextView mTarre2;
    private EditText mTarre3;

    private TextView mPoidBrut1;
    private TextView mPoidBrut2;
    private TextView mPoidBrut3;

    private TextView mPoidNet1;
    private TextView mPoidNet2;
    private EditText mPoidNet3;

    private int position;
    private int nbLignes;
    private String barcode;

    private String Port;
    private String IP;
    private String Bdd;
    private String Foretagkod;

    private String batchid;
    private String dummyuniqueid;
    private String ordernr;
    private String ordradnr;
    private String ordradnrstrpos;
    private String ordrestnr;
    private String url;
    private String nBatchid;
    private String artnr;

    int ua1;
    int ua2;
    int ua3;
    float ua5;
    float ua6;
    float ua7;
    float ua8;
    float ua9;
    int ua1_2;
    int ua2_2;
    int ua3_2;
    float ua5_2;
    float ua6_2;
    float ua7_2;
    float ua8_2;
    float ua9_2;

    boolean lotOk;
    private String q_pal_code;

    private Toast wrongLot;

    AsyncHttpClient client = new AsyncHttpClient();

    private void refresh (){
        if(mLot2.getText().toString().length() > 2) {
            nBatchid = mLot2.getText().toString().substring(3).replaceAll("^0+", "");
            getUa();
        }
        mColis3.setText("" + ua1);
        mPiece3.setText("" + ua3);
        mTarre3.setText("" + (Math.round(ua6 * 100.0) / 100));
        mPoidBrut3.setText("" + (Math.round(ua5 * 100.0) / 100));
        mPoidNet3.setText("" + (Math.round(ua9 * 100.0) / 100));

        mColis2.setText("" + ua1_2);
        mPiece2.setText("" + (Math.round(ua3_2 * 100.0) / 100));
        mTarre2.setText("" + (Math.round(ua6_2 * 100.0) / 100));
        mPoidBrut2.setText("" + (Math.round(ua5_2 * 100.0) / 100));
        mPoidNet2.setText("" + (Math.round(ua9_2 * 100.0) / 100));
    }

    private void Validate() {
        // Chargement des champs
        url = "http://" + IP + ":" + Port + "/validateBtn";
        // Parametres body de la requete
        RequestParams params = new RequestParams();
        params.put("user", Sy2.user);
        params.put("password", Sy2.password);
        params.put("bdd", Bdd);
        params.put("foretagkod", Foretagkod);
        params.put("ordernr", ordernr);
        params.put("q_pal_code", q_pal_code);
        params.put("dummyuniqueid", dummyuniqueid);
        params.put("ordradnr", ordradnr);
        params.put("ordrestnr", ordrestnr);
        params.put("ordradnrstrpos", ordradnrstrpos);
        params.put("batchid", nBatchid);
        params.put("q_gcbp_ua1", mColis3.getText().toString());
        params.put("q_gcbp_ua3", mPiece3.getText().toString());
        params.put("q_gcbp_ua5", mPoidBrut3.getText().toString());
        params.put("q_gcbp_ua7", mTarre3.getText().toString());
        params.put("q_gcbp_ua9", mPoidNet3.getText().toString());
        params.setUseJsonStreamer(true);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                position = (position + 1) % nbLignes;
                Intent PrepaActivity = new Intent(PrepaActivity.this, PrepaActivity.class);
                PrepaActivity.putExtra("barcode", barcode);
                PrepaActivity.putExtra("nbLignes", nbLignes);
                PrepaActivity.putExtra("position", position);
                PrepaActivity.this.finish();
                startActivity(PrepaActivity);

            }

            @Override public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) { }
        });
    }

    // EXEC q_2bp_RRH_GetCodePalette foretagkod, ordernr}, q_gclibrubrique
    private void getPalCode(){
        // Chargement des champs
        url = "http://" + IP + ":" + Port + "/getPalCode";
        // Parametres body de la requete
        RequestParams params = new RequestParams();
        params.put("user", Sy2.user);
        params.put("password", Sy2.password);
        params.put("bdd", Bdd);
        params.put("foretagkod", Foretagkod);
        params.put("ordernr", ordernr);
        params.put("q_gclibrubrique", barcode.toString().split(";")[1].replaceAll("[\n]+", ""));
        params.setUseJsonStreamer(true);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject currentRow = Helper.GetFirstRow(responseBody);
                try{
                    q_pal_code = currentRow.getString("q_pal_code");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) { }
        });
    }

    private boolean verifLot(){
        // Chargement des champs
        url = "http://" + IP + ":" + Port + "/verifLot";
        // Parametres body de la requete
        RequestParams params = new RequestParams();
        params.put("user", Sy2.user);
        params.put("password", Sy2.password);
        params.put("bdd", Bdd);
        params.put("foretagkod", Foretagkod);
        params.put("artnr", artnr);
        params.put("batchid", nBatchid);
        params.setUseJsonStreamer(true);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject currentRow = Helper.GetFirstRow(responseBody);
                try {
                    if(currentRow.getString("exist").equalsIgnoreCase("1")){
                        lotOk = true;
                    }
                    else {
                        lotOk = false;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) { }
        });
        return lotOk;
    }

    private void getUa(){
        if (verifLot() == true) {
            // Chargement des champs
            url = "http://" + IP + ":" + Port + "/getUa268";
            // Parametres body de la requete
            RequestParams params = new RequestParams();
            params.put("user", Sy2.user);
            params.put("password", Sy2.password);
            params.put("bdd", Bdd);
            params.put("foretagkod", Foretagkod);
            params.put("artnr", artnr);
            params.put("batchid", nBatchid);
            params.setUseJsonStreamer(true);

            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    JSONObject currentRow = Helper.GetFirstRow(responseBody);
                    try {
                        //maj ua2 ua6 et ua 8 depuis bat
                        ua2_2 = Integer.parseInt(currentRow.getString("q_gcbp_ua2").toString());
                        ua6_2 = Float.parseFloat(currentRow.getString("q_gcbp_ua6").toString());
                        ua8_2 = Float.parseFloat(currentRow.getString("q_gcbp_ua8").toString());
                        System.out.println("maj ok :::" + currentRow.getString("q_gcbp_ua2").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                }
            });
        }
    }

    private void Rupture() {
        // Chargement des champs
        url = "http://" + IP + ":" + Port + "/ruptureBtn";
        // Parametres body de la requete
        RequestParams params = new RequestParams();
        params.put("user", Sy2.user);
        params.put("password", Sy2.password);
        params.put("bdd", Bdd);
        params.put("foretagkod", Foretagkod);
        params.put("ordernr", ordernr);
        params.put("dummyuniqueid", dummyuniqueid);
        params.put("ordradnr", ordradnr);
        params.setUseJsonStreamer(true);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) { }

            @Override public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) { }
        });
    }

    private void Recharge() {
        // Chargement des champs
        url = "http://" + IP + ":" + Port + "/rechargeBtn";
        // Parametres body de la requete
        RequestParams params = new RequestParams();
        params.put("user", Sy2.user);
        params.put("password", Sy2.password);
        params.put("bdd", Bdd);
        params.put("foretagkod", Foretagkod);
        params.put("ordernr", ordernr);
        params.put("dummyuniqueid", dummyuniqueid);
        params.put("ordradnr", ordradnr);
        params.setUseJsonStreamer(true);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) { }

            @Override public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) { }
        });
    }

    private void RuptureUpdateQoffnr(){
        // Chargement des champs
        url = "http://" + IP + ":" + Port + "/ruptureFindLines";
        // Parametres body de la requete
        RequestParams params = new RequestParams();
        params.put("user", Sy2.user);
        params.put("password", Sy2.password);
        params.put("bdd", Bdd);
        params.put("foretagkod", Foretagkod);
        params.put("ordernr", ordernr);
        params.put("ordradnr", ordradnr);
        params.setUseJsonStreamer(true);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject currentRow = Helper.GetFirstRow(responseBody);
                try {
                    if (currentRow.getString("exist").equalsIgnoreCase("0")){
                        RuptureUpdateOrp();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) { }
        });
    }

    private void RuptureUpdateOrp(){
        // Chargement des champs
        url = "http://" + IP + ":" + Port + "/ruptureUpdateOrp";
        // Parametres body de la requete
        RequestParams params = new RequestParams();
        params.put("user", Sy2.user);
        params.put("password", Sy2.password);
        params.put("bdd", Bdd);
        params.put("foretagkod", Foretagkod);
        params.put("ordernr", ordernr);
        params.put("ordradnr", ordradnr);
        params.setUseJsonStreamer(true);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) { }

            @Override public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) { }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepa);

        //Recuperation ordernr, gamme, nombre de lignes et code palette
        Intent intent = getIntent();
        barcode = intent.getStringExtra("barcode");
        position = intent.getIntExtra("position",0);
        nbLignes = intent.getIntExtra("nbLignes",1);
        getPalCode();

        mValidButton    = (android.widget.Button) findViewById(R.id.prepa_buttonValider);
        mRuptureButton  = (android.widget.Button) findViewById(R.id.prepa_buttonRupture);
        mRechargeButton = (android.widget.Button) findViewById(R.id.prepa_buttonRecharge);
        mEtiqButton     = (android.widget.Button) findViewById(R.id.prepa_buttonEtiqMqt);
        mRetourButton   = (android.widget.Button) findViewById(R.id.prepa_buttonRetour);

        mClient1        = (TextView)              findViewById(R.id.prepa_client1);
        mClient2        = (TextView)              findViewById(R.id.prepa_client2);
        mOrdernr2       = (TextView)              findViewById(R.id.prepa_NoCommande1);
        mOrdernr        = (TextView)              findViewById(R.id.prepa_NoCommande2);
        mLigne          = (TextView)              findViewById(R.id.prepa_NoLigne);
        mArticle1       = (TextView)              findViewById(R.id.prepa_Article1);
        mArticle2       = (TextView)              findViewById(R.id.prepa_Article2);
        mLot1           = (TextView)              findViewById(R.id.prepa_Lot1);
        mLot2           = (EditText)              findViewById(R.id.prepa_Lot2 );
        mGamme          = (TextView)              findViewById(R.id.prepa_Gamme1);
        mGamme2         = (TextView)              findViewById(R.id.prepa_Gamme2);
        mColis1         = (TextView)              findViewById(R.id.prepa_Colis1);
        mColis2         = (TextView)              findViewById(R.id.prepa_Colis2);
        mColis3         = (EditText)              findViewById(R.id.prepa_Colis3);
        mPiece1         = (TextView)              findViewById(R.id.prepa_Piece1);
        mPiece2         = (TextView)              findViewById(R.id.prepa_Piece2);
        mPiece3         = (EditText)              findViewById(R.id.prepa_Piece3);
        mTarre1         = (TextView)              findViewById(R.id.prepa_Tarre1);
        mTarre2         = (TextView)              findViewById(R.id.prepa_Tarre2);
        mTarre3         = (EditText)              findViewById(R.id.prepa_Tarre3);
        mPoidBrut1      = (TextView)              findViewById(R.id.prepa_PoidBrut1);
        mPoidBrut2      = (TextView)              findViewById(R.id.prepa_PoidBrut2);
        mPoidBrut3      = (TextView)              findViewById(R.id.prepa_PoidBrut3);
        mPoidNet1       = (TextView)              findViewById(R.id.prepa_PoidNet1);
        mPoidNet2       = (TextView)              findViewById(R.id.prepa_PoidNet2);
        mPoidNet3       = (EditText)              findViewById(R.id.prepa_PoidNet3);


        //Recup de l'IP / Port / BDD
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(ConfigActivity.Config, 0);
        IP         = sharedPreferences.getString("IP",null);
        Port       = sharedPreferences.getString("Port",null);
        Bdd        = sharedPreferences.getString("BDD",null);
        Foretagkod = sharedPreferences.getString("Foretagkod",null);

        //Recuperation des lignes
        AsyncHttpClient client = new AsyncHttpClient();

        //url de la requete
        url = "http://" + IP + ":" + Port + "/neededFields";

        // Parametres body de la requete
        RequestParams params = new RequestParams();
        params.put("user", Sy2.user);
        params.put("password", Sy2.password);
        params.put("bdd", Bdd);
        params.put("foretagkod", Foretagkod);
        params.put("ordernr", barcode.toString().split(";")[0]);
        params.put("q_gclibrubrique", barcode.toString().split(";")[1].replaceAll("[\n]+", ""));
        params.setUseJsonStreamer(true);


        client.post(url,params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {

                JSONObject currentRow = Helper.GetRowAt(response,position);
                try {
                    // Resultat de la requete dans l'objet current
                    batchid = currentRow.getString("batchid");
                    dummyuniqueid = currentRow.getString("DummyUniqueId");
                    ordernr = currentRow.getString("OrderNr");
                    ordradnr = currentRow.getString("ordradnr");
                    ordradnrstrpos = currentRow.getString("OrdRadNrStrPos");
                    ordrestnr = currentRow.getString("ordrestnr");

                    AsyncHttpClient client2 = new AsyncHttpClient();
                    // Chargement des champs
                    url = "http://" + IP + ":" + Port + "/fillFields";
                    // Parametres body de la requete
                    RequestParams params = new RequestParams();
                    params.put("user", Sy2.user);
                    params.put("password", Sy2.password);
                    params.put("bdd", Bdd);
                    params.put("foretagkod", Foretagkod);
                    params.put("ordernr", ordernr);
                    params.put("dummyuniqueid", dummyuniqueid);
                    params.put("ordradnr", ordradnr);
                    params.put("ordrestnr", ordrestnr);
                    params.put("ordradnrstrpos", ordradnrstrpos);
                    params.put("batchid", batchid);
                    params.setUseJsonStreamer(true);


                    client2.post(url, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            JSONObject currentRow = Helper.GetFirstRow(responseBody);
                            try {
                                // Remplissage des champs
                                artnr = currentRow.getString("artnr");

                                // Poids net variable?
                                if(currentRow.getString("artftgspec2").equalsIgnoreCase("0")) {
                                    mPoidNet3.setEnabled(false);
                                    mPoidNet3.setBackground(getResources().getDrawable(R.drawable.border));
                                }
                                // Nb pieces variable?
                                if(currentRow.getString("artftgspec3").equalsIgnoreCase("0")) {
                                    mPiece3.setEnabled(false);
                                    mPiece3.setBackground(getResources().getDrawable(R.drawable.border));
                                }

                                mClient2.setText(currentRow.getString("ftgnamn"));
                                mOrdernr.setText(currentRow.getString("OrderNr"));
                                mArticle2.setText(currentRow.getString("q_gcar_lib1"));
                                mLigne.setText(currentRow.getString("OrdRadNr"));
                                mGamme2.setText(barcode.toString().split(";")[1].replaceAll("[\n]+", ""));

                                mColis2.setText(currentRow.getString("q_gcbp_ua1_2"));
                                mPiece2.setText(currentRow.getString("q_gcbp_ua3_2"));
                                mTarre2.setText(currentRow.getString("q_gcbp_ua6_2"));
                                mPoidBrut2.setText(currentRow.getString("q_gcbp_ua5_2"));
                                mPoidNet2.setText(currentRow.getString("q_gcbp_ua9_2"));

                                mColis3.setText(currentRow.getString("q_gcbp_ua1"));
                                mPiece3.setText(currentRow.getString("q_gcbp_ua3"));
                                mTarre3.setText(currentRow.getString("q_gcbp_ua6"));
                                mPoidBrut3.setText(currentRow.getString("q_gcbp_ua5"));
                                mPoidNet3.setText(currentRow.getString("q_gcbp_ua9"));

                                ua1   = Integer.parseInt(currentRow.getString("q_gcbp_ua1").toString());
                                ua2   = Integer.parseInt(currentRow.getString("q_gcbp_ua2").toString());
                                ua3   = Integer.parseInt(currentRow.getString("q_gcbp_ua3").toString());
                                ua5   = Float.parseFloat(currentRow.getString("q_gcbp_ua5").toString());
                                ua6   = Float.parseFloat(currentRow.getString("q_gcbp_ua6").toString());
                                ua7   = Float.parseFloat(currentRow.getString("q_gcbp_ua7").toString());
                                ua8   = Float.parseFloat(currentRow.getString("q_gcbp_ua8").toString());
                                ua9   = Float.parseFloat(currentRow.getString("q_gcbp_ua9").toString());
                                ua1_2 = Integer.parseInt(currentRow.getString("q_gcbp_ua1_2").toString());
                                ua2_2 = Integer.parseInt(currentRow.getString("q_gcbp_ua2_2").toString());
                                ua3_2 = Integer.parseInt(currentRow.getString("q_gcbp_ua3_2").toString());
                                ua5_2 = Float.parseFloat(currentRow.getString("q_gcbp_ua5_2").toString());
                                ua6_2 = Float.parseFloat(currentRow.getString("q_gcbp_ua6_2").toString());
                                ua7_2 = Float.parseFloat(currentRow.getString("q_gcbp_ua7_2").toString());
                                ua8_2 = Float.parseFloat(currentRow.getString("q_gcbp_ua8_2").toString());
                                ua9_2 = Float.parseFloat(currentRow.getString("q_gcbp_ua9_2").toString());

                                if(ua1 != 0) {
                                    // Piece u
                                    ua2_2 = ua3 / ua1;
                                    // Poids u
                                    ua8_2 = ua9 / ua1;
                                    // Poids brut
                                    ua5 = ua1 * ( ua8 + ua6 );
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                        }
                    }
                    @Override public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) { }
                });
                // catch premiere requete
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, java.lang.Throwable error) {
                mClient1.setText("Erreur de chargement");
            }
            @Override public void onRetry(int retryNo) { }
        });

        //Bouton Valider
        mValidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wrongLot = Toast.makeText(getApplicationContext(), "Erreur lot", Toast.LENGTH_SHORT);

                //Test du lot
                if (mLot2.getText().toString().length() == 0){
                    nBatchid = "N/A";
                    Validate();
                }
                else if (mLot2.getText().toString().length() != 12){
                    wrongLot.show();
                }
                else if ( !(Foretagkod.substring(2,4).equalsIgnoreCase(mLot2.getText().toString().substring(0,2)))) {
                    wrongLot.show();
                }
                else {
                    //batchid = 10 derniers carac du lot sans les 0
                    nBatchid = mLot2.getText().toString().substring(3).replaceAll("^0+", "");
                    Validate();
                }
            }
        });

        //Bouton Rupture
        mRuptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PrepaActivity.this);
                builder.setTitle("CONFIRMATION");
                builder.setMessage("Confirmer rupture ?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Rupture();
                        RuptureUpdateQoffnr();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Annulation", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

        //Bouton Recharge
        mRechargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PrepaActivity.this);
                builder.setTitle("CONFIRMATION");
                builder.setMessage("Confirmer recharge ?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Recharge();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Annulation", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });


        //Bouton retour : retour au scan
        mRetourButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrepaActivity.this.finish();
            }
        });

        mLot2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                getUa();
                ua3 = ua1 * ua2_2;
                ua9 = ua1 * ua8_2;
                ua5 = ua1 * ( ua6_2 + ua8_2 );
                refresh();
            }
        });

        mColis3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Verif champ nul
                if ( mColis3.getText().length() == 0 ){
                    mColis3.setText("0");
                }
                ua1   = Integer.parseInt(mColis3.getText().toString());
                ua3   = ua2 * ua1;
                ua9   = ua8 * ua1;
                ua7   = ua6 * ua1;
                ua5   = ua1 * ( ua8 + ua6 );
                refresh();
            }
        });

        mPiece3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Verif champ nul
                if ( mPiece3.getText().length() == 0 ){
                    mPiece3.setText("0");
                }
                ua3 =   Integer.parseInt(mPiece3.getText().toString());
                ua2 =   ua3 / ua1;
                refresh();
            }
        });

        mTarre3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Verif champ nul
                if ( mTarre3.getText().length() == 0 ){
                    mTarre3.setText("0");
                }
                ua6   = Float.parseFloat(mTarre3.getText().toString());
                ua5   = ua1 * ( ua6 + ua8 );

                refresh();
            }
        });

        mPoidBrut3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Verif champ nul
                if ( mPoidBrut3.getText().length() == 0 ){
                    mPoidBrut3.setText("0");
                }
                ua5   = Float.parseFloat(mPoidBrut3.getText().toString());

                refresh();
            }
        });

        mPoidNet3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Verif champ nul
                if ( mPoidNet3.getText().length() == 0 ){
                    mPoidNet3.setText("0");
                }
                ua9 = Float.parseFloat(mPoidNet3.getText().toString());
                if (ua9 <= 0) {
                    ua8 = 0;
                }
                else {
                    ua8 = ua9 / ua1;
                }
                ua5   = ua1 * ( ua6 + ua8 );
                refresh();
            }
        });
    }
}
