package com.example.julijanjug.pocketbank;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddAccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // set title of toolbar
        getSupportActionBar().setTitle("Add Account");
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        DatabaseHelper dbh = new DatabaseHelper(this);
        dbh.setTableName("Bank");
        Cursor banks = dbh.getAllData();
        ArrayList<String> list = new ArrayList<>();
        if (banks.moveToFirst()) {
            while (!banks.isAfterLast()) {
                String name = banks.getString(banks.getColumnIndex("Name"));
                list.add(name);
                banks.moveToNext();
            }
        }

        String[] bankArray = new String[list.size()];
        bankArray=list.toArray(bankArray);

        Spinner spinner = (Spinner) findViewById(R.id.sp_bank);
        if(spinner.getSelectedItem()==null) {
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, bankArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }

        //spinner.setOnItemSelectedListener(AddAccountActivity.this);
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_transaction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }else if(item.getItemId() == R.id.confirm) {
            EditText name = (EditText) findViewById(R.id.et_account);
            if(name.getText().toString().equals(""))
                Toast.makeText(this, "You have to enter an account name!", Toast.LENGTH_SHORT).show();
            else{
                saveAccount();
                finish();
            }
            // confirm transaction and return to dashboard
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveAccount(){
        DatabaseHelper dbh = new DatabaseHelper(this);
        dbh.setTableName("Bank");

        EditText name = (EditText) findViewById(R.id.et_account);
        EditText desc = (EditText) findViewById(R.id.et_description);
        Spinner spinner = (Spinner) findViewById(R.id.sp_bank);
        String bankName = spinner.getSelectedItem().toString();

        String[] cols = {"Name", "Description", "Bank_ID", "User_ID"};
        Cursor res = dbh.getAllData();
        String bankID = "1";
        while (res.moveToNext()) {
            if(bankName.equals(res.getString(res.getColumnIndex("Name")))){
                bankID = res.getString(res.getColumnIndex("_id"));
                break;
            }
        }
        SharedPreferences sp = getSharedPreferences("logged",MODE_PRIVATE);

        String[] data = {name.getText().toString(), desc.getText().toString(), bankID, Integer.toString(sp.getInt("user_id",1))};
        dbh.setTableName("Account");
        dbh.insertDataSpecific(cols, data);

        res=dbh.getAllData();
        while (res.moveToNext()) {
            if(res.getString(res.getColumnIndex("Name")).equals(name.getText().toString())) {
                sp.edit().putInt("accID", res.getInt(res.getColumnIndex("_id"))).apply();
                sp.edit().putString("account", res.getString(res.getColumnIndex("Name"))).apply();
            }
        }
    }

}
