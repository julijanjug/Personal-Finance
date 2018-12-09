package com.example.julijanjug.pocketbank;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private boolean loggingIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnSignup = (Button)findViewById(R.id.btn_signup);
        Button btnLogin = (Button)findViewById(R.id.btn_login);
        Button btnFaq = (Button)findViewById(R.id.btn_faq);


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        btnFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, FaqActivity.class));
            }
        });
    }

    private void login() {
        if (loggingIn){
            return;
        }
        loggingIn = true;

        EditText email = (EditText) findViewById(R.id.input_email);
        EditText password = (EditText) findViewById(R.id.input_password);

        if (email.getText().toString().isEmpty()) {
            Toast.makeText(this, "You must enter your email.", Toast.LENGTH_SHORT).show();
            loggingIn = false;
            return;
        }

        if (password.getText().toString().isEmpty()) {
            Toast.makeText(this, "You must enter your password.", Toast.LENGTH_SHORT).show();
            loggingIn = false;
            return;
        }

        //TODO: pregled podatkov v bazi
//        API.login(email.toString(), password.toString()) { response ->
//            if (response.success && response is API.LoginResponse) {
//                Toast.makeText(this, "Login successful.", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "Login unsuccessful (${response.message ?: "/"})", Toast.LENGTH_SHORT).show()
//            }
//            loggingIn = false
//        }
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        loggingIn = false;
    }
}
