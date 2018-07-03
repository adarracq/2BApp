package com.a2bsystemapp.a2bsystemapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ConfigActivity extends AppCompatActivity {

    static final String Config = "Config";
    private static final String IP = "IP";
    private static final String Port = "Port";
    private static final String BDD = "BDD";
    private static final String Foretagkod = "Foretagkod";
    SharedPreferences sharedPreferences;

    private EditText mIP;
    private EditText mPort;
    private EditText mBDD;
    private EditText mForetagkod;
    private Button mValidButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        mIP          = (EditText) findViewById(R.id.config_ip2);
        mPort        = (EditText) findViewById(R.id.config_port2);
        mBDD         = (EditText) findViewById(R.id.config_bdd2);
        mForetagkod  = (EditText) findViewById(R.id.config_code_soc2);
        mValidButton = (Button)   findViewById(R.id.valid_button);

        sharedPreferences = getBaseContext().getSharedPreferences(Config,MODE_PRIVATE);

        //Rempli avec les données existantes
        if(sharedPreferences.contains(IP) && sharedPreferences.contains(Port) && sharedPreferences.contains(BDD) && sharedPreferences.contains(Foretagkod)){
            mIP.setText(sharedPreferences.getString(IP,null));
            mPort.setText(sharedPreferences.getString(Port,null));
            mBDD.setText(sharedPreferences.getString(BDD,null));
            mForetagkod.setText(sharedPreferences.getString(Foretagkod,null));
        }

        //Enregistre les données entrées en cas d'appui sur valider
        mValidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    sharedPreferences
                            .edit()
                            .putString(IP,mIP.getText().toString())
                            .putString(Port,mPort.getText().toString())
                            .putString(BDD,mBDD.getText().toString())
                            .putString(Foretagkod,mForetagkod.getText().toString())
                            .apply();
                ConfigActivity.this.finish();
            }
        });
    }
}
