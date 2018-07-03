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

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    private TextView mIdText;
    private EditText mId;
    private EditText mPassword;
    private Button mConnexionButton;
    private Button mConfigButton;
    private Sy2 userId = new Sy2();
    private String Port;
    private String IP;
    private String Bdd;
    private String Foretagkod;
    private Toast tLoginProcess;
    private Toast tLoginError;

    private void Connection(){
        tLoginProcess = Toast.makeText(getApplicationContext(), "Connexion en cours...", Toast.LENGTH_LONG);
        tLoginError = Toast.makeText(getApplicationContext(), "Echec de la connexion.", Toast.LENGTH_SHORT);
        AsyncHttpClient client = new AsyncHttpClient();

        //url de la requete
        String  url = "http://" + IP + ":" + Port + "/connect";
        // Parametres body de la requete
        RequestParams params = new RequestParams();
        params.put("user", mId.getText().toString());
        params.put("password", mPassword.getText().toString());
        params.put("bdd", Bdd);
        params.put("foretagkod", Foretagkod);
        params.setUseJsonStreamer(true);


        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
                tLoginProcess.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                //Enregistrement des logs
                userId.setUser(mId.getText().toString());
                userId.setPassword(mPassword.getText().toString());
                Intent MainActivity = new Intent(LoginActivity.this, MainActivity.class);
                tLoginProcess.cancel();
                startActivity(MainActivity);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                tLoginProcess.cancel();
                tLoginError.show();
            }

            @Override
            public void onRetry(int retryNo) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mIdText          = (TextView) findViewById(R.id.id_text);
        mId              = (EditText) findViewById(R.id.identifiant);
        mPassword        = (EditText) findViewById(R.id.password);
        mConnexionButton = (Button)   findViewById(R.id.connexion_button);
        mConfigButton    = (Button)   findViewById(R.id.config_button);

        //Recup de l'IP et du port
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(ConfigActivity.Config, 0);
        IP         = sharedPreferences.getString("IP",null);
        Port       = sharedPreferences.getString("Port",null);
        Bdd        = sharedPreferences.getString("BDD",null);
        Foretagkod = sharedPreferences.getString("Foretagkod",null);

        //Desactive le bouton connexion
        mConnexionButton.setEnabled(false);

        mId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Reactive le bouton connexion
                mConnexionButton.setEnabled(s.toString().length() != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Ouvre activité config
        mConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ConfigActivity = new Intent(LoginActivity.this, ConfigActivity.class);
                startActivity(ConfigActivity);
            }
        });

        //Connexion a la bdd
        mConnexionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connection();
            }
        });
    }
}