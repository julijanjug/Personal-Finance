package com.example.julijanjug.pocketbank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "pocketBank.db";
    public static String TABLE_NAME = "Users";
    public static final String COL_2 = "USERNAME";
    public static final String COL_3 = "PASSWORD";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL("PRAGMA foreign_keys = 1;");
            db.execSQL("create table Account(_id integer primary key autoincrement, User_ID integer, Bank_ID integer, Name text, Balance real, Description text, foreign key (User_ID) references Users(_id),foreign key (Bank_ID) references Banks(_id));");
            db.execSQL("create table Bank(_id integer primary key autoincrement, Name text);");
            db.execSQL("create table Transactions(_id integer primary key autoincrement, Account_ID integer, TransValue real, Date_Of_Transaction text, Note text, User_ID integer, foreign key (User_ID) references Users(_id),foreign key (Account_ID) references Account(_id));");
            db.execSQL("create table Users(_id integer primary key autoincrement, Username text, Password text);");
        }
        catch(Exception e){
            Log.d("NAPAKA_ON_CREATE",e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            db.execSQL("drop table if exists Account;");
            db.execSQL("drop table if exists Bank;");
            db.execSQL("drop table if exists Transactions;");
            db.execSQL("drop table if exists Users;");
        }
        catch(Exception e){
            Log.d("NAPAKA_ON_UGRADE",e.toString());
        }

        onCreate(db);
    }

    //Gets all the columns and rows of the specified table
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res=null;
        try{
            res = db.rawQuery("select * from "+TABLE_NAME,null);
        }catch (Exception e){
            Log.d("NAPAKA_getAllData",e.toString());
        }

        return res;
    }

    //Inserts the data for the user
    public boolean insertData(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,username);
        contentValues.put(COL_3,password);
        long result = db.insert(TABLE_NAME,null, contentValues);
        if (result == -1) {
            return false;
        }
        else {
            return true;
        }
    }

    //Inserts the data into the specified table
    //"names" is an array with column names
    //"values" is an array of values that are to be stored
    public boolean insertDataSpecific(String[] names, String[] values){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for(int i=0; i<names.length; i++)
            contentValues.put(names[i],values[i]);
        long result = db.insert(TABLE_NAME,null, contentValues);
        if (result == -1) {
            return false;
        }
        else {
            return true;
        }
    }

    //Sets the name for the table
    public void setTableName(String tableName){
        TABLE_NAME = tableName;
    }

    public void updateTransaction(long id, double amount, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues editTransaction = new ContentValues();
        editTransaction.put("TransValue", amount);
        editTransaction.put("Note",note);

        db.update("Transactions", editTransaction, "_id=" + id, null);
        close();
    }

    public Cursor getUserTransaction(int user_id, int accID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res=null;
        try{
            String query="SELECT _id,DATE(Date_Of_Transaction) AS date, TransValue AS value, Note AS note FROM Transactions WHERE User_ID = "+ user_id + " AND Account_ID = "+accID;
            res = db.rawQuery(query, null);
        }catch (Exception e){
            Log.d("NAPAKA_getUserTransacti",e.toString());
        }

        return res;
    }

    public Cursor getTransaction(long transaction_id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res=null;
        try{
            String query="SELECT * FROM Transactions WHERE _id =  "+ transaction_id;
            res = db.rawQuery(query, null);
        }catch (Exception e){
            Log.d("NAPAKA_getUserTransacti",e.toString());
        }

        return res;
    }

    public int getUser(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res=null;
        try{
            res = db.query("Users",new String[]{"_id"}, "username='"+username+"'", null, null, null,
                    null );
        }catch (Exception e){
            Log.d("NAPAKA_getUser",e.toString());
        }

        if(res.getCount() >0){
            res.moveToFirst();
            return res.getInt(0);
        }else{
            return -1;
        }
    }

    public float getUserBalance(int user_id, int acc_id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res=null;
        try{
            res = db.query("Transactions",new String[]{"sum(TransValue)"}, "User_ID='"+user_id+"' AND Account_ID='"+acc_id+"'", null, null, null,
                    null );
        }catch (Exception e){
            Log.d("NAPAKA_getUserBalance",e.toString());
        }
        if(res.getCount() >0){
            res.moveToFirst();
            return res.getFloat(0);
        }else{
            return 0;
        }
    }

    public static Date subtractDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);

        return cal.getTime();
    }

    public Cursor getGroupedSumTransactionsFiveDays(int user_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Date daysAgo=subtractDays(date, 5);

        String dateToString=dateFormat.format(date);
        String dateToString2=dateFormat.format(daysAgo);

        String countQuery="SELECT date(Date_Of_Transaction) as 'date', sum(TransValue) as 'amount' FROM Transactions WHERE User_ID = '"+user_id+"' AND Date_Of_Transaction >= '"+dateToString2+"'"+" and "+"Date_Of_Transaction <= '"+dateToString+"' GROUP BY date(date)";

        return db.rawQuery(countQuery,null);
    }
    public Cursor getGroupedSumTransactionsTenDays(int user_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Date daysAgo=subtractDays(date, 10);

        String dateToString=dateFormat.format(date);
        String dateToString2=dateFormat.format(daysAgo);

        String countQuery="SELECT date(Date_Of_Transaction) as 'date', sum(TransValue) as 'amount' FROM Transactions WHERE User_ID = '"+user_id+"' AND Date_Of_Transaction >= '"+dateToString2+"'"+" and "+"Date_Of_Transaction <= '"+dateToString+"' GROUP BY date(date)";

        return db.rawQuery(countQuery,null);
    }
    public Cursor getMaxGroupedSumTransactionsFiveDays(int user_id){
        SQLiteDatabase db = this.getWritableDatabase();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Date daysAgo=subtractDays(date, 5);

        String dateToString=dateFormat.format(date);
        String dateToString2=dateFormat.format(daysAgo);

        String countQuery="SELECT max(amount) FROM (SELECT sum(TransValue) as 'amount' FROM Transactions WHERE User_ID = '"+user_id+"' AND Date_Of_Transaction >= '"+dateToString2+"'"+" and "+"Date_Of_Transaction <= '"+dateToString+"' GROUP BY date(Date_Of_Transaction) ORDER BY date(Date_Of_Transaction) DESC)";

        return db.rawQuery(countQuery,null);
    }
    public Cursor getMaxGroupedSumTransactionsTenDays(int user_id){
        SQLiteDatabase db = this.getWritableDatabase();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Date daysAgo=subtractDays(date, 10);

        String dateToString=dateFormat.format(date);
        String dateToString2=dateFormat.format(daysAgo);

        String countQuery="SELECT max(amount) FROM (SELECT sum(TransValue) as 'amount' FROM Transactions WHERE User_ID = '"+user_id+"' AND Date_Of_Transaction >= '"+dateToString2+"'"+" and "+"Date_Of_Transaction <= '"+dateToString+"' GROUP BY date(Date_Of_Transaction) ORDER BY date(Date_Of_Transaction) DESC)";

        return db.rawQuery(countQuery,null);
    }

    public void insertTenDaysTestTransactions(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date datum = new Date();
        Random rnd = new Random();
        int st;

        for (int i=0; i<11; i++){
            st = rnd.nextInt((20 - 10) + 1) + 10;
            String[] columns = {"TransValue", "Note", "Date_Of_Transaction", "User_ID"};
            String[] values = {Integer.toString(st*10), "test", dateFormat.format(datum), "1"};
            setTableName("Transactions");
            insertDataSpecific(columns, values);
            Log.d("testJulijan", datum.toString()+ "---"+ Integer.toString(st*10));
            datum = subtractDays(datum,1);
        }
    }

    public boolean deleteAccount(String accName){
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_NAME, "Name=?",new String[] {accName})>0;
    }
}