package com.example.julijanjug.pocketbank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

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
            db.execSQL("create table Account(Account_ID integer primary key autoincrement, User_ID integer, Bank_ID integer, Name text, Balance real, foreign key (User_ID) references Users(User_ID),foreign key (Bank_ID) references Banks(Bank_ID));");
            db.execSQL("create table Bank(Bank_ID integer primary key autoincrement, Name text);");
            db.execSQL("create table DailyBalance(DailyBalanceID integer primary key autoincrement, User_ID integer, Balance real, DateOf text,foreign key (User_ID) references Users(User_ID));");
            db.execSQL("create table Transactions(Transaction_ID integer primary key autoincrement, Account_ID integer, TransValue real, Date_Of_Transaction text,foreign key (Account_ID) references Account(Acount_ID));");
            db.execSQL("create table Users(User_ID integer primary key autoincrement, Username text, Password text);");
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
            db.execSQL("drop table if exists DailyBalance;");
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
}