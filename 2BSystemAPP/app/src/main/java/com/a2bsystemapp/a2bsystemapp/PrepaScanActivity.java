package com.a2bsystemapp.a2bsystemapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class PrepaScanActivity extends AppCompatActivity {

    private TextView mScanText;
    private EditText mScan;
    private Button mValidButton;

    private String Port;
    private String IP;
    private String Bdd;
    private String Foretagkod;

    private void ValidateScan() {
        final Toast tScanProgress = Toast.makeText(getApplicationContext(), "Chargement en cours...", Toast.LENGTH_LONG);
        final Toast tScanError = Toast.makeText(getApplicationContext(), "Echec de la connexion.", Toast.LENGTH_SHORT);
        final Toast tScanUnknown = Toast.makeText(getApplicationContext(), "Numéro de commande inconnu", Toast.LENGTH_SHORT);
        // Ici, mettre le code qui traite la donnée scannée

        //Recuperation code barre
        final String barcode = mScan.getText().toString();
        AsyncHttpClient client = new AsyncHttpClient();
        //Requete verifiant l'existance de la commande
        String  url = "http://" + IP + ":" + Port + "/scan";
        // Parametres du body de la requete
        RequestParams params = new RequestParams();

        params.put("user", Sy2.user);
        params.put("password", Sy2.password);
        params.put("bdd", Bdd);
        params.put("foretagkod", Foretagkod);
        params.put("ordernr", barcode.split(";")[0]);
        params.setUseJsonStreamer(true);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                tScanProgress.show();
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {
                tScanProgress.cancel();
                // Renvoie 0 si aucune commande 1 sinon

                JSONObject currentRow = Helper.GetFirstRow(response);
                try {
                    if (Integer.parseInt(currentRow.getString("ok")) == 0) {
                        tScanUnknown.show();
                        PrepaScanActivity.this.finish();
                        Intent PrepaScanActivity = new Intent(PrepaScanActivity.this, PrepaScanActivity.class);
                        startActivity(PrepaScanActivity);
                    } else {
                        // Ouvre une activité scan en lui passant le code barre
                        Intent ListActivity = new Intent(PrepaScanActivity.this, PrepaListActivity.class);
                        ListActivity.putExtra("barcode", barcode);
                        startActivity(ListActivity);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, java.lang.Throwable error) {
                tScanProgress.cancel();
                tScanError.show();
            }

            @Override public void onRetry(int retryNo) { }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);


        mScanText = (TextView) findViewById(R.id.scan_text);
        mScan = (EditText) findViewById(R.id.scan);
        mValidButton = (Button) findViewById(R.id.valid_button);

        //Recup de l'IP / Port / BDD
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(ConfigActivity.Config, 0);
        IP         = sharedPreferences.getString("IP",null);
        Port       = sharedPreferences.getString("Port",null);
        Bdd        = sharedPreferences.getString("BDD",null);
        Foretagkod = sharedPreferences.getString("Foretagkod",null);

        //mValidButton.setEnabled(false);

        mScan.addTextChangedListener(new TextWatcher() {
            // Gestion du retour clavier ( pas le bruno :D )
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Si retour a la ligne (dernier caractere scan) + match regex commande
                if (s.toString().length() > 1 && s.charAt(s.length() - 1) == '\n') {
                    ValidateScan();
                }
            }

            // Méthodes obligatoires pour valider l'interface
            @Override public void afterTextChanged(Editable s) {}
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });

        //Bouton valider
        mValidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateScan();
            }
        });
    }

    //Ferme l'activité en cas de mise en arriere plan
    @Override
    protected void onPause() {
        super.onPause();
        PrepaScanActivity.this.finish();
    }
}
