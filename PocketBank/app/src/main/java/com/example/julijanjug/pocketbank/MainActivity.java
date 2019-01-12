package com.example.julijanjug.pocketbank;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    String selected = "5 days";
    ListView listViewTransaction;
    CursorAdapter transactionAdapter;
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new DatabaseHelper(this);
        //myDb.insertTenDaysTestTransactions();
        SharedPreferences sp = getSharedPreferences("logged", MODE_PRIVATE);
        if (sp.getInt("bankeNotri", 0) == 0) {
            populateBanks();
            sp.edit().putInt("bankeNotri", 1).apply();
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // set title of toolbar
        getSupportActionBar().setTitle("Dashboard");

        Button btnAdd = (Button) findViewById(R.id.btn_add);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper dbh = new DatabaseHelper(MainActivity.this);
                dbh.setTableName("Account");
                Cursor noAccs = dbh.getAllData();
                if (noAccs.getCount() > 0)
                    startActivity(new Intent(MainActivity.this, AddTransactionActivity.class));
                else
                    Toast.makeText(MainActivity.this, "You have to first create an account in your settings.", Toast.LENGTH_SHORT).show();
            }
        });
        //transaction list
        listViewTransaction = (ListView) findViewById(R.id.listTransactions);
        listViewTransaction.setOnItemClickListener(viewTransactionListener);

        String[] from = new String[]{"date", "note", "value"};
        int[] to = new int[]{R.id.dateTextView, R.id.commentTextView, R.id.amountTextView};

        transactionAdapter = new SimpleCursorAdapter(MainActivity.this,
                R.layout.transaction_item, null, from, to, 0);
        listViewTransaction.setAdapter(transactionAdapter);
    }

    //clik event ob kliku na transakcijo v listView-u -> odpre se addTrasanction
    AdapterView.OnItemClickListener viewTransactionListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            final Intent viewContact = new Intent(MainActivity.this, AddTransactionActivity.class);
            viewContact.putExtra("_id",arg3);
            startActivity(viewContact);
        }
    };

    public void populateBanks(){
        DatabaseHelper dbh = new DatabaseHelper(this);

        dbh.setTableName("Bank");
        String[] cols = {"Name"};
        String[] data1 = {"Not Specified"};
        String[] data = {"SKB"};
        String[] data2 = {"NLB"};
        String[] data3 = {"Abanka"};
        String[] data4 = {"Delavska hranilnica"};
        String[] data5 = {"Nova KBM"};
        dbh.insertDataSpecific(cols, data1);
        dbh.insertDataSpecific(cols, data);
        dbh.insertDataSpecific(cols, data2);
        dbh.insertDataSpecific(cols, data3);
        dbh.insertDataSpecific(cols, data4);
        dbh.insertDataSpecific(cols, data5);
    }

    @Override
    public void onResume(){
        super.onResume();
        TextView currency_text = (TextView) findViewById(R.id.textView6);
        SharedPreferences sp = getSharedPreferences("currency",MODE_PRIVATE);
        SharedPreferences sp2 = getSharedPreferences("logged", MODE_PRIVATE);
        setupGraph();

        float trenutno_stanje = myDb.getUserBalance(sp2.getInt("user_id", 0),sp2.getInt("accID", 1));

        String valuta = sp.getString("currency", "EUR");
        String izracunano = currency_kalkulator(trenutno_stanje, valuta);
        currency_text.setText("Balance:" + " " + izracunano + " " + valuta);

        boolean sendNotification=sp2.getBoolean("notifications", false);
        if(sendNotification){
            if(sp.getFloat("dailyLimit",0.0f)!=0.0f){
                float dayLimit = sp.getFloat("dailyLimit", 0.0f);
                float withOffSet = dayLimit-((float)10/100*dayLimit);
                BigDecimal dailyLimit = new BigDecimal(dayLimit);
                BigDecimal diffDaily = dailyLimit.subtract(new BigDecimal(izracunano));
                if(Float.parseFloat(diffDaily.toString())<=0.0f)
                    startNotificationReachedDaily(3000);
                else if(Float.parseFloat(izracunano)>=withOffSet)
                    startNotificationDaily(3000);
            }

            if(sp.getFloat("monthlyLimit",0.0f)!=0.0f){
                float monthLimit = sp.getFloat("monthlyLimit", 0.0f);
                float withOffSet = monthLimit-((float)10/100*monthLimit);
                BigDecimal monthlyLimit = new BigDecimal(monthLimit);
                BigDecimal diffMonthly = monthlyLimit.subtract(new BigDecimal(izracunano));
                if(Float.parseFloat(diffMonthly.toString())<=0.0f)
                    startNotificationReachedMonthly(3000);
                if(Float.parseFloat(izracunano)>=withOffSet)
                    startNotificationMonthly(3000);
            }
        }


        int user_id = sp2.getInt("user_id", 0);
        int acc_id = sp2.getInt("accID", 0);
        transactionAdapter.changeCursor(myDb.getUserTransaction(user_id, acc_id));
    }

    //Pushes notification if the balance is reaching the daily limit
    public void startNotificationDaily(int delay){
        Intent notificationIntent = new Intent(this, LimitNotification.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    //Pushes notification if the balance is reaching the monthly limit
    public void startNotificationMonthly(int delay){
        Intent notificationIntent = new Intent(this, LimitNotificationMonth.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    //Pushes notification if the balance reached the monthly limit
    public void startNotificationReachedMonthly(int delay){
        Intent notificationIntent = new Intent(this, LimitReachedMonthly.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    //Pushes notification if the balance reached the daily limit
    public void startNotificationReachedDaily(int delay){
        Intent notificationIntent = new Intent(this, LimitReachedDaily.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }


    public String currency_kalkulator(float trenutno_stanje, String valuta){
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
        return String.format("%.2f", convertion.multiply(new BigDecimal(trenutno_stanje)));
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            // open settings
            return true;
        }

        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.removeAllSeries();

        switch (item.getItemId()) {
            case R.id.itemFivedays:
                selected="5 days";
                setupGraph();
                return super.onOptionsItemSelected(item);

            case R.id.itemTendays:
                selected="10 days";
                setupGraph();
                return super.onOptionsItemSelected(item);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setupGraph() {
        SharedPreferences sp2 = getSharedPreferences("logged", MODE_PRIVATE);
        int user_id = sp2.getInt("user_id", 0);

        Cursor cursor = null;
        switch(selected) {
            case "5 days" :  cursor = myDb.getGroupedSumTransactionsFiveDays(user_id);
                break;
            case "10 days":  cursor = myDb.getGroupedSumTransactionsTenDays(user_id);
                break;
        }
        if(cursor.getCount() == 0){
            return;
        }
        cursor.moveToFirst();

        //date formater
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        //nardimo DataPoint z točkami ki se bojo prikazovale na grafu (podatke dobimo iz baze)
        DataPoint[] dt = new DataPoint[cursor.getCount()];
        for (int i=0; i<cursor.getCount(); i++){
            try {
                date = format.parse(cursor.getString(0));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dt[i] = new DataPoint(date, cursor.getInt(1));
            cursor.moveToNext();
        }

        //Graph design
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dt);
        series.setDrawBackground(true);
        series.setBackgroundColor(Color.argb(80, 25, 118, 210));
        series.setColor(Color.rgb(25, 118, 210));
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(8);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.addSeries(series);    //dodamo točke na graf
        //for geting max amount of water
        switch(selected) {
            case "5 days" :  cursor = myDb.getMaxGroupedSumTransactionsFiveDays(user_id);
                break;
            case "10 days":  cursor = myDb.getMaxGroupedSumTransactionsTenDays(user_id);
                break;
        }
        if(cursor.getCount() == 0){
            return;
        }

        cursor.moveToFirst();
        int max = cursor.getInt(0); //vemo koliko je maximalna količina vode in lahko nastavimo višino y-osi
        graph.getViewport().setMaxY(max+50);
        graph.getViewport().setMinY(0);
        graph.getViewport().setYAxisBoundsManual(true);
        //formatiramo oznake na y in x oseh
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    String myDateStr = new SimpleDateFormat("dd").format(new Date((new Double(value)).longValue()));
                    if(myDateStr.substring(0,1).equals("0")){
                        myDateStr=myDateStr.substring(1);
                    }
                    return myDateStr;
                } else {
                    // show ml on y values
                    return super.formatLabel(value, isValueX);

                }
            }
        });

        graph.getGridLabelRenderer().setNumHorizontalLabels(5); // only 5  labels on x because of the space
        Date maxX = new Date();
        maxX.setHours(0);       //odrežemo ure in minute, pustimo samo datum da je graf lepši
        maxX.setMinutes(0);
        Date minX = new Date();
        minX.setHours(0);
        minX.setMinutes(0);
        switch(selected) {
            case "5 days" :  minX=myDb.subtractDays(maxX, 4);

                break;
            case "10 days":  minX=myDb.subtractDays(maxX, 9);
                break;
        }

        //nastavim omejitve od kje do kje je X-os (odvisno al je izbran 5 ali 10 dni)
        graph.getViewport().setMinX(minX.getTime());
        graph.getViewport().setMaxX(maxX.getTime());
        graph.getViewport().setXAxisBoundsManual(true);
    }
}
