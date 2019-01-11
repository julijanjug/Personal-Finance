package com.example.julijanjug.pocketbank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.TreeMap;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // set title of toolbar
        getSupportActionBar().setTitle("Settings");
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        SharedPreferences sp = getSharedPreferences("currency",MODE_PRIVATE);
        SharedPreferences current_currency = getSharedPreferences("trenutna_valuta",MODE_PRIVATE);
        current_currency.edit().putString("trenutna_valuta","EUR").apply();

        SharedPreferences send_currency = getSharedPreferences("poslana_trenutna_valuta",MODE_PRIVATE);

        //Setup spinner
        Spinner spinner = (Spinner) findViewById(R.id.def_currency);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.currencies_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);

    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }else if(item.getItemId() == R.id.settings) {
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            // open settings
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String sSelected=parent.getItemAtPosition(position).toString();

        SharedPreferences sp = getSharedPreferences("currency",MODE_PRIVATE);
        sp.edit().putString("currency",sSelected).apply();
    }

    @Override
    public void onResume(){
        super.onResume();
        TreeMap<String,Integer> vrednosti = new TreeMap<String,Integer>();
        vrednosti.put("EUR",1);
        vrednosti.put("USD",2);
        vrednosti.put("GBP",3);
        vrednosti.put("AUD",4);
        vrednosti.put("JPY",5);
        vrednosti.put("CZK",6);
        vrednosti.put("HRK",7);
        vrednosti.put("HUF",8);
        vrednosti.put("JMD",9);
        vrednosti.put("RUB",10);

        Spinner spinner = (Spinner) findViewById(R.id.def_currency);

        SharedPreferences sp = getSharedPreferences("currency",MODE_PRIVATE);
        String current = sp.getString("currency", "EUR");
        int indeks = vrednosti.get(current);

        spinner.setSelection(indeks-1);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
