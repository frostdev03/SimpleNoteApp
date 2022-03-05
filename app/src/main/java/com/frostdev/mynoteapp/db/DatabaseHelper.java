package com.frostdev.mynoteapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DATABSE_NAME = "dbnoteapp";
    private static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_TABLE_NOTE = String.format("CREATE TABLE %s"
            + "(%s INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " %s TEXT NOT NULL,"+
            " %s TEXT NOT NULL," +
            "%s TEXT NOT NULL)",
            DatabaseContract.TABLE_NAME,
            DatabaseContract.NoteColumns._ID,
            DatabaseContract.NoteColumns.TITLE,
            DatabaseContract.NoteColumns.DESCRIPTION,
            DatabaseContract.NoteColumns.DATE
    );

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABSE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_NOTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_NAME);
        onCreate(db);
    }
}
