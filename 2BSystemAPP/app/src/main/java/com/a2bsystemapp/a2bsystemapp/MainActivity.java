package com.a2bsystemapp.a2bsystemapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mPrepaButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPrepaButton = (Button) findViewById(R.id.prepa_button);

        //Ouvre activité de preparation
        mPrepaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent PrepaScanActivity = new Intent(MainActivity.this, PrepaScanActivity.class);
                startActivity(PrepaScanActivity);
            }
        });
    }
}
