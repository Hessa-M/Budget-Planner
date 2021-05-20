package com.example.budget.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.budget.data.ItemContract.ItemEntry;

public class ItemDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "budget.db";
    private static final int DATABASE_VERSION = 1;

    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ITEMS_TABLE =  "CREATE TABLE " + ItemEntry.TABLE_NAME + " ("
                + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_AMOUNT + " FLOAT NOT NULL DEFAULT 0, "
                + ItemEntry.COLUMN_TYPE + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_PHOTO + " TEXT, "
                + ItemEntry.COLUMN_PHONE + " TEXT);";

        db.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}



