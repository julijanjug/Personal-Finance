package com.example.julijanjug.pocketbank;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ParseException;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by julijanjug on 12/01/2019.
 */

public class MySimpleCursorAdapter extends SimpleCursorAdapter {

    Context cont;

    public MySimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        cont = context;
    }

    @Override
    public void setViewText(TextView v, String text){

        if (v.getId() == R.id.amountTextView) {
            SharedPreferences sp = cont.getSharedPreferences("currency",MODE_PRIVATE);
            String valuta = sp.getString("currency", "EUR");

            text = currency_kalkulator(Float.parseFloat(text), valuta);
            v.setText(text+ " "+ valuta);
        }else if(v.getId() == R.id.dateTextView){
            SimpleDateFormat df = new SimpleDateFormat("dd.MM. H:m");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                v.setText(df.format(formatter.parse(text)));

            }catch (java.text.ParseException e) {
                v.setText(text);
            }
        }else{
            v.setText(text);
        }
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
}
