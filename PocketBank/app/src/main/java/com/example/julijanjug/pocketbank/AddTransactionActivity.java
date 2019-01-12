package com.example.julijanjug.pocketbank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

public class AddTransactionActivity extends AppCompatActivity {

    Spinner spinner2; //currencies
    Spinner spinner3; //transaction type
    EditText TEamount;
    EditText TEnote;
    long idOftransaction = 0;
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        myDb = new DatabaseHelper(this);

        TEamount = (EditText) findViewById(R.id.editText);
        TEnote = (EditText) findViewById(R.id.editText2);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner3 = (Spinner) findViewById(R.id.spinner3);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // set title of toolbar
        getSupportActionBar().setTitle("Add Transaction");
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //Setup all spinners
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.currencies_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        spinner3 = (Spinner) findViewById(R.id.spinner3);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.transaction_type_array, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter3);

    }
    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                idOftransaction = bundle.getLong("_id");
                myDb = new DatabaseHelper(this);
                Cursor cursor=myDb.getTransaction(idOftransaction);

                if(cursor.moveToFirst()) {
                    String category = cursor.getString(1);
                    String type = cursor.getString(2);
                    float amount = cursor.getInt(2);
                    String comment = cursor.getString(4);

                    TEamount.setText(String.valueOf(amount));
                    TEnote.setText(comment);
                }
            }
        }
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_transaction, menu);
        return true;
    }

    public double currencyToEur(double value, String valuta){
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

        BigDecimal convertion = new BigDecimal(konverzije_euro.get(valuta));
        BigDecimal value_big = new BigDecimal(value);
        return value_big.divide(convertion, MathContext.DECIMAL32).doubleValue();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }else if(item.getItemId() == R.id.confirm) {
            saveTransaction();
            finish();
            // confirm transaction and return to dashboard

        }

        return super.onOptionsItemSelected(item);
    }

    public String getCurrentDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate=new Date(System.currentTimeMillis());
        return df.format(currentDate);
    }

    private void saveTransaction() {
        if (getIntent().getExtras() == null) {
            double amount = Double.valueOf(TEamount.getText().toString());
            String note = TEnote.getText().toString();

            SharedPreferences sp = getSharedPreferences("logged", MODE_PRIVATE);
            int user_id = sp.getInt("user_id", 0);
            if (amount != 0) {
                if(spinner3.getSelectedItem().toString().equals("Expense")){ //ali je expense ali income
                    amount = -amount;
                }
                amount = currencyToEur(amount, spinner2.getSelectedItem().toString()); //pretvorba v evre

                String[] columns = {"TransValue", "Note", "Date_Of_Transaction", "User_ID", "Account_ID"};
                String[] values = {Double.toString(amount), note, getCurrentDate(), Integer.toString(user_id), Integer.toString(sp.getInt("accID", 1))};
                myDb.setTableName("Transactions");
                myDb.insertDataSpecific(columns, values);

                Toast toast = Toast.makeText(getApplicationContext(), "Transaction saved.", Toast.LENGTH_SHORT);
                toast.show();

                onBackPressed();
            } else {

                Toast.makeText(this, "Transaction not saved.", Toast.LENGTH_LONG).show();
            }
        } else {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            double amount = Double.valueOf(TEamount.getText().toString());
            String note = TEnote.getText().toString();

            if (bundle != null) {
                idOftransaction = bundle.getLong("_id");
                myDb.updateTransaction(idOftransaction, amount, note);

                Toast.makeText(this, "Succesfully updated.", Toast.LENGTH_LONG).show();

                Intent intent1=new Intent(this,MainActivity.class);
                startActivity(intent1);
            } else {

                Toast.makeText(this, "Transaction not updated.", Toast.LENGTH_LONG).show();
            }

        }
    }
}