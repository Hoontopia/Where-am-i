package com.example.sec.whereami;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Factory on 2016-07-29.
 */
public class LOC_DB_Helper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "mylocs.db";
    private static final int DATABASE_VERSION = 2;

    public LOC_DB_Helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE locs ( _id INTEGER PRIMARY KEY"+ " AUTOINCREMENT, type TEXT, name TEXT, lat INTEGER, lng INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS locs");
        onCreate(db);
    }
}
