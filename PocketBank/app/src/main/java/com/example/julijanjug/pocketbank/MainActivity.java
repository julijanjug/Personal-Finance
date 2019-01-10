package com.example.julijanjug.pocketbank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // set title of toolbar
        getSupportActionBar().setTitle("Dashboard");

        Button btnAdd = (Button)findViewById(R.id.btn_add);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddTransactionActivity.class));
            }
        });

        //Graf
        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series);

    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        TextView currency_text = (TextView) findViewById(R.id.textView6);
        SharedPreferences sp = getSharedPreferences("currency",MODE_PRIVATE);
        SharedPreferences send_currency = getSharedPreferences("poslana_trenutna_valuta",MODE_PRIVATE);

        //String trenutna_valuta = current_currency.getString("current_currency", "");
        String trenutna_valuta = send_currency.getString("poslana_trenutna_valuta", "EUR");

        //SPREMENI TRENUTNO STANJE NA STANJE IZ BAZE, ZEJ DELA KOT DA BI VEDNO MEL 100 TRENUTNE VALUTE
        int trenutno_stanje = 100;
        String zelena_valuta = sp.getString("currency", "EUR");
        int is_it_euro = 0;

        if (sp.getString("currency", "").equals("EUR")){
            is_it_euro = 1;
        }
        else{
            is_it_euro = 0;
        }

        String izracunano = currency_kalkulator(trenutna_valuta, trenutno_stanje, zelena_valuta, is_it_euro);
        currency_text.setText("Balance:" + " " + izracunano + " " + sp.getString("currency", ""));
    }

    public String currency_kalkulator(String trenutna_valuta, int trenutno_stanje, String zelena_valuta, int je_evro){
        double pretvorjeno_v_evro = 0;
        double pretvorjeno_v_zeleno = 0;

        TreeMap<String,Double> konverzije_euro = new TreeMap<String,Double>();
        konverzije_euro.put("EUR",1.00000);
        konverzije_euro.put("USD",1.14885);
        konverzije_euro.put("GBP",0.90054);
        konverzije_euro.put("AUD",1.60312);
        konverzije_euro.put("JPY",124.77);
        konverzije_euro.put("CZK",25.6293);
        konverzije_euro.put("HRK",7.42337);
        konverzije_euro.put("HUF",321.461);
        konverzije_euro.put("JMD",146.211);
        konverzije_euro.put("RUB",76.8361);

        double convert_value = konverzije_euro.get(trenutna_valuta);

        if(je_evro == 0){
            pretvorjeno_v_evro = trenutno_stanje / convert_value;
            Toast.makeText(this,String.valueOf(pretvorjeno_v_evro),Toast.LENGTH_SHORT).show();
            if(zelena_valuta.equals("EUR")){
                return  String.valueOf(pretvorjeno_v_evro);
            }
            else{
                pretvorjeno_v_zeleno = pretvorjeno_v_evro * konverzije_euro.get(zelena_valuta);
                return String.valueOf(pretvorjeno_v_zeleno);
            }
        }
        else{
            pretvorjeno_v_zeleno = trenutno_stanje * konverzije_euro.get(zelena_valuta);
            return String.valueOf(pretvorjeno_v_zeleno);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            // open settings
        }

        return super.onOptionsItemSelected(item);
    }
}
