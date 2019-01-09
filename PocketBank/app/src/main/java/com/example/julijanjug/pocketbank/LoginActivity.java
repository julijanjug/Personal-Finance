package com.example.julijanjug.pocketbank;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private boolean loggingIn = false;
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDb = new DatabaseHelper(this);
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

        EditText username = (EditText) findViewById(R.id.input_username);
        EditText password = (EditText) findViewById(R.id.input_password);
        Cursor res = myDb.getAllData();
        boolean neobstaja = true;

        if (username.getText().toString().isEmpty()) {
            Toast.makeText(this, "You must enter your username.", Toast.LENGTH_SHORT).show();
            loggingIn = false;
            return;
        }

        else if (password.getText().toString().isEmpty()) {
            Toast.makeText(this, "You must enter your password.", Toast.LENGTH_SHORT).show();
            loggingIn = false;
            return;
        }
        else if(res.getCount() == 0){
            Toast.makeText(this, "You must create an account first at the signup.", Toast.LENGTH_SHORT).show();
            loggingIn = false;
            return;
        }
        else{
            while (res.moveToNext()) {
                //String prvi = username.getText().toString();
                //String drugi = password.getText().toString();
                //String tretji = res.getString(1);
                //String cetrti = res.getString(2);
                if(username.getText().toString().equals(res.getString(1)) && password.getText().toString().equals(res.getString(2))){
                //if(prvi.equals(tretji) && drugi.equals(cetrti)){
                    loggingIn = true;
                    neobstaja = false;
                    break;
                }
            }
            res.close();
            if (neobstaja)
            {
                Toast.makeText(this, "Account with the given username and password dosen't exist.", Toast.LENGTH_SHORT).show();
                loggingIn = false;
                return;
            }
        }
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        loggingIn = false;
    }
}
