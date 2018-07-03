package com.a2bsystemapp.a2bsystemapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class PrepaListActivity extends AppCompatActivity {

    private String Port;
    private String IP;
    private String Bdd;
    private String Foretagkod;
    private String ordernr;
    private String gamme;
    private String quantity;
    private int nbLignes;
    private String url;
    private ListView listView;
    private String barcode;
    private TextView mHeader;
    private String prep = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = (ListView) findViewById(R.id.listView);
        mHeader = (TextView) findViewById(R.id.header);


        //Recuperation ordernr et gamme dans orderAndGamme
        Intent intent  = getIntent();
        barcode = intent.getStringExtra("barcode");
        ordernr = barcode.toString().split(";")[0];
        gamme   = barcode.toString().split(";")[1].replaceAll("[\n]+", "");

        //Recup de l'IP / Port / BDD
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(ConfigActivity.Config, 0);
        IP         = sharedPreferences.getString("IP",null);
        Port       = sharedPreferences.getString("Port",null);
        Bdd        = sharedPreferences.getString("BDD",null);
        Foretagkod = sharedPreferences.getString("Foretagkod",null);


        //Recuperation des lignes
        AsyncHttpClient client = new AsyncHttpClient();

        //url de la requete
        url = "http://" + IP + ":" + Port + "/loadLines";

        // Parametres body de la requete
        RequestParams params = new RequestParams();
        params.put("user", Sy2.user);
        params.put("password", Sy2.password);
        params.put("bdd", Bdd);
        params.put("foretagkod", Foretagkod);
        params.put("ordernr", ordernr);
        params.put("q_gclibrubrique", gamme);
        params.setUseJsonStreamer(true);


        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                int i = 0;
                ArrayList<HashMap<String, String>> listItem = new ArrayList<>();
                //On déclare la HashMap qui contiendra les informations pour une ligne
                HashMap<String, String> map;

                while( Helper.GetRowAt(responseBody , i) != null) {
                    i = i + 1;
                }
                nbLignes = i;

                for( int j = 0 ; j < i ; j++ ) {
                    JSONObject currentRow = Helper.GetRowAt(responseBody , j);
                    try {
                        //Rempli l'en-tete
                        mHeader.setText("COMMANDE N° " + ordernr + "   CLIENT : " + currentRow.getString("ftgnamn"));

                        if(!currentRow.getString("q_pal_code").equalsIgnoreCase("null")) {
                            //met en vert les lignes avec un code palette
                            //listView.
                            //lv.getChildAt(j).setBackgroundColor(Color.parseColor("#B6FE7D"));
                            //prep = " déja prepa";

                        }

                        if (currentRow.getString("q_unitefac").replaceAll(" ", "").equalsIgnoreCase("c")) {
                            quantity = currentRow.getString("q_gcbp_ua1") + " Colis" + prep;
                        } else if (currentRow.getString("q_unitefac").replaceAll(" ", "").equalsIgnoreCase("st")) {
                            quantity = currentRow.getString("q_gcbp_ua3") + " Pièces" + prep;
                        } else if (currentRow.getString("q_unitefac").replaceAll(" ", "").equalsIgnoreCase("k")) {
                            quantity = currentRow.getString("q_gcbp_ua9") + " Kg" + prep;
                        }

                        //Création d'une HashMap pour insérer les informations de la premiere ligne
                        map = new HashMap<String, String>();

                        //on insère l'article et la quantité
                        map.put("article",currentRow.getString("q_gcar_lib1"));
                        map.put("quantite", quantity);

                        listItem.add(map);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                SimpleAdapter adapter = new SimpleAdapter (PrepaListActivity.this, listItem, R.layout.prepa_line,
                        new String[] {"article", "quantite"}, new int[] {R.id.article, R.id.quantite});

                listView.setAdapter(adapter);

                // Ecoute des clicks sur les lignes
                listView.setClickable(true);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent PrepaActivity = new Intent(PrepaListActivity.this, PrepaActivity.class);
                        PrepaActivity.putExtra("barcode", barcode);
                        PrepaActivity.putExtra("nbLignes",nbLignes);
                        PrepaActivity.putExtra("position", position);

                        startActivity(PrepaActivity);
                    }
                });

                //while(listView.getChildCount()<1){}

                for(int iRow = 0; iRow < listView.getAdapter().getCount(); iRow++)
                {
                    View currentRowView = listView.getChildAt(iRow);
                    //currentRowView.setBackgroundColor(getResources().getColor(R.color.green));
                }
            }

            @Override public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) { }
            @Override public void onRetry(int retryNo) { }
        });
    }
}

/*
à corriger !!!
public class PrepaListAdapter extends SimpleAdapter {
    public PrepaListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
    }
}*/