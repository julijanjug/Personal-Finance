package com.example.julijanjug.pocketbank;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class FaqActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // set title of toolbar
        getSupportActionBar().setTitle("FAQ");
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        TextView que1 = (TextView)findViewById(R.id.que1);
        que1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView ans = findViewById(R.id.ans1);
                if(ans.getVisibility() == View.GONE){
                    ans.setVisibility(View.VISIBLE);
                }else{
                    ans.setVisibility(View.GONE);
                }
            }
        });
        TextView que2 = (TextView)findViewById(R.id.que2);
        que2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView ans = findViewById(R.id.ans2);
                if(ans.getVisibility() == View.GONE){
                    ans.setVisibility(View.VISIBLE);
                }else{
                    ans.setVisibility(View.GONE);
                }            }
        });
        TextView que3 = (TextView)findViewById(R.id.que3);
        que3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView ans = findViewById(R.id.ans3);
                if(ans.getVisibility() == View.GONE){
                    ans.setVisibility(View.VISIBLE);
                }else{
                    ans.setVisibility(View.GONE);
                }            }
        });

        TextView que4 = (TextView)findViewById(R.id.que4);
        que4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView ans = findViewById(R.id.ans4);
                if(ans.getVisibility() == View.GONE){
                    ans.setVisibility(View.VISIBLE);
                }else{
                    ans.setVisibility(View.GONE);
                }            }
        });
        TextView que5 = (TextView)findViewById(R.id.que5);
        que5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView ans = findViewById(R.id.ans5);
                if(ans.getVisibility() == View.GONE){
                    ans.setVisibility(View.VISIBLE);
                }else{
                    ans.setVisibility(View.GONE);
                }            }
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
}
