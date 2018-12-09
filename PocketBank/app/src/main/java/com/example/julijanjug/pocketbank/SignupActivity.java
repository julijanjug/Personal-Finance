package com.example.julijanjug.pocketbank;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            // set title of toolbar
            getSupportActionBar().setTitle("Sign up");
            // add back arrow to toolbar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Button btnSignup = (Button)findViewById(R.id.btn_signup);
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

        EditText email = (EditText) findViewById(R.id.input_email1);
        EditText password1 = (EditText) findViewById(R.id.input_password1);
        EditText password2 = (EditText) findViewById(R.id.input_password2);

        if (email.getText().toString().isEmpty()) {
            Toast.makeText(this, "You must enter your email.", Toast.LENGTH_SHORT).show();
            signingup = false;
            return;
        }

        if (password1.getText().toString().isEmpty() || password2.getText().toString().isEmpty()) {
            Toast.makeText(this, "You must enter your password.", Toast.LENGTH_SHORT).show();
            signingup = false;
            return;
        }

        if (!password1.getText().toString().equals(password2.getText().toString())) {
            Toast.makeText(this, "Passwords don't match.", Toast.LENGTH_SHORT).show();
            signingup = false;
            return;
        }

        //TODO: vnos podatkov v bazo

        startActivity(new Intent(SignupActivity.this, MainActivity.class));
        signingup = false;
    }
}
