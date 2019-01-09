package com.example.julijanjug.pocketbank;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {

    private boolean signingup = false;
    DatabaseHelper myDb;
    EditText username,password1,password2;
    Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        myDb = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            // set title of toolbar
            getSupportActionBar().setTitle("Sign up");
            // add back arrow to toolbar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        btnSignup = (Button)findViewById(R.id.btn_signup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    private void signup() {
        if (signingup){
            return;
        }
        signingup = true;

        username = (EditText) findViewById(R.id.input_username);
        password1 = (EditText) findViewById(R.id.input_password1);
        password2 = (EditText) findViewById(R.id.input_password2);
        Cursor res = myDb.getAllData();

        if (username.getText().toString().isEmpty()) {
            Toast.makeText(this, "You must enter your username.", Toast.LENGTH_SHORT).show();
            signingup = false;
            return;
        }

        else if (password1.getText().toString().isEmpty() || password2.getText().toString().isEmpty()) {
            Toast.makeText(this, "You must enter your password.", Toast.LENGTH_SHORT).show();
            signingup = false;
            return;
        }

        else if (password1.getText().toString().length() < 4){
            Toast.makeText(this, "Your password must be atleast 4 characters long.", Toast.LENGTH_SHORT).show();
            signingup = false;
            return;
        }

        else if (!password1.getText().toString().equals(password2.getText().toString())) {
            Toast.makeText(this, "Passwords doesn't match.", Toast.LENGTH_SHORT).show();
            signingup = false;
            return;
        }
        else if(res.getCount() > 0){
            while (res.moveToNext()) {
                if(username.getText().toString().equals(res.getString(1))){
                    Toast.makeText(this, "Username already exists.", Toast.LENGTH_SHORT).show();
                    signingup = false;
                    return;
                }
                else{
                    signingup = myDb.insertData(username.getText().toString(),password1.getText().toString());
                }
            }
        }
        else{
            signingup = myDb.insertData(username.getText().toString(),password1.getText().toString());
        }

        startActivity(new Intent(SignupActivity.this, MainActivity.class));
        signingup = false;
    }
}
