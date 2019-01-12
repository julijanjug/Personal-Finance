package com.example.julijanjug.pocketbank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private EditText monthlyLimitText;
    private EditText dailyLimitText;
    private String selectedItem;
    private int selectedItemPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // set title of toolbar
        getSupportActionBar().setTitle("Settings");
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Button btnAdd = (Button) findViewById(R.id.add_account);

        //Sets an event listener for when the Create account button is clicked
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, AddAccountActivity.class));
            }
        });

        Button btnDelete = (Button) findViewById(R.id.delete_account);

        //Sets an event listener for when the Delete account button is clicked
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });

        SharedPreferences current_currency = getSharedPreferences("trenutna_valuta", MODE_PRIVATE);
        current_currency.edit().putString("trenutna_valuta", "EUR").apply();

        //Setup spinner
        Spinner spinner = (Spinner) findViewById(R.id.def_currency);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.currencies_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);

        //Sets an event listener to change the monthly limit each time the text is changed
        monthlyLimitText = (EditText) findViewById(R.id.monthly_limit);
        monthlyLimitText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                SharedPreferences sp = getSharedPreferences("currency", MODE_PRIVATE);
                if (!monthlyLimitText.getText().toString().equals(""))
                    sp.edit().putFloat("monthlyLimit", Float.parseFloat(monthlyLimitText.getText().toString())).apply();
                else
                    sp.edit().putFloat("monthlyLimit", 0.0f).apply();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        //Sets an event listener to change the daily limit each time the text is changed
        dailyLimitText = (EditText) findViewById(R.id.daily_limit);
        dailyLimitText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                SharedPreferences sp = getSharedPreferences("currency", MODE_PRIVATE);
                if (!dailyLimitText.getText().toString().equals(""))
                    sp.edit().putFloat("dailyLimit", Float.parseFloat(dailyLimitText.getText().toString())).apply();
                else
                    sp.edit().putFloat("dailyLimit", 0.0f).apply();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        fillAccountsList();
    }

    public void deleteAccount(){
        DatabaseHelper dbh = new DatabaseHelper(this);
        dbh.setTableName("Account");
        boolean success = false;
        try{
            success=dbh.deleteAccount(selectedItem);
            Toast.makeText(SettingsActivity.this, "Account was successfully deleted!", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(getIntent());
        }catch (Exception e){
            Log.d("NAPAKA_DELETE",e.toString());
        }

    }

    public void fillAccountsList(){
        DatabaseHelper dbh = new DatabaseHelper(this);
        dbh.setTableName("Account");

        Cursor accounts = dbh.getAllData();
        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<String> descList = new ArrayList<>();
        ArrayList<String> bankList = new ArrayList<>();
        if (accounts.moveToFirst()) {
            while (!accounts.isAfterLast()) {
                String name = accounts.getString(accounts.getColumnIndex("Name"));
                String desc = accounts.getString(accounts.getColumnIndex("Description"));
                String banka = accounts.getString(accounts.getColumnIndex("Bank_ID"));
                nameList.add(name);
                descList.add(desc);
                bankList.add(banka);
                accounts.moveToNext();
            }
        }

        ListView accountsList = (ListView) findViewById(R.id.accountsLV);
        if(!nameList.isEmpty()){
            accountsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    selectedItem = parent.getItemAtPosition(position).toString();
                    String[] accountDetails = selectedItem.split(",");
                    String accountName = accountDetails[1].substring(6);
                    accountName=accountName.replace("}","");
                    String[] accountOnlyname = accountName.split(" - ");
                    accountName=accountOnlyname[0];
                    Toast.makeText(SettingsActivity.this, "Account "+accountName+" is selected", Toast.LENGTH_SHORT).show();

                    selectedItem=accountName;
                    SharedPreferences sp = getSharedPreferences("logged", MODE_PRIVATE);
                    sp.edit().putString("account", selectedItem).apply();
                    selectedItemPos = position;

                    DatabaseHelper newDbh=new DatabaseHelper(SettingsActivity.this);
                    newDbh.setTableName("Account");
                    Cursor res = newDbh.getAllData();
                    while (res.moveToNext()) {
                        if(res.getString(res.getColumnIndex("Name")).equals(selectedItem)) {
                            sp.edit().putInt("accID", res.getInt(res.getColumnIndex("_id"))).apply();
                        }
                    }
                    newDbh.close();
                }
            });
        }

        dbh.setTableName("Bank");
        HashMap<String, String> banke = new HashMap<String, String>();
        Cursor allBanks = dbh.getAllData();
        if (allBanks.moveToFirst()) {
            while (!allBanks.isAfterLast()) {
                String name = allBanks.getString(allBanks.getColumnIndex("Name"));
                String banka = allBanks.getString(allBanks.getColumnIndex("_id"));
                banke.put(banka, name);
                allBanks.moveToNext();
            }
        }


        ArrayList<HashMap<String, String>> sezAccounts = new ArrayList<HashMap<String, String>>();
        if (!nameList.isEmpty()) {
            for (int i = 0; i < nameList.size(); i++) {
                HashMap<String, String> item = new HashMap<String, String>();
                item.put("Name", nameList.get(i) + " - " + banke.get(bankList.get(i)));
                item.put("Desc", descList.get(i));
                sezAccounts.add(item);
            }
            accountsList.setAdapter(new SimpleAdapter(this, sezAccounts, android.R.layout.simple_list_item_2, new String[]{"Name", "Desc"}, new int[]{android.R.id.text1, android.R.id.text2}));
        }

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
            SharedPreferences sp = getSharedPreferences("logged",MODE_PRIVATE);
            sp.edit().putBoolean("logged",false).apply();
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

        monthlyLimitText = (EditText) findViewById(R.id.monthly_limit);
        dailyLimitText = (EditText) findViewById(R.id.daily_limit);

        monthlyLimitText.setText(Float.toString(sp.getFloat("monthlyLimit", 0.00f)));
        dailyLimitText.setText(Float.toString(sp.getFloat("dailyLimit", 0.00f)));

        fillAccountsList();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}