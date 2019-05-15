package com.perfect.freshair.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.perfect.freshair.Model.CurrentStatus;
import com.perfect.freshair.Model.DustGPS;

import java.sql.Timestamp;
import java.util.ArrayList;

public class StatusDBHandler extends SQLiteOpenHelper {
    public static final String TAG = "StatusDBHandler";
    public static final int DB_VER = 1;
    private static final String DB_NAME = "StatusDB.db";
    public static final String TABLE_LOCATION = "status";

    public enum Column {
        ID, TIMESTAMP, PM25, PM100, LAT, LNG, ACC,
        TOTAL_SATE, USE_SATE, ELAPSE_TIME, POSITION_STATUS;
    }

    public StatusDBHandler(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE " +TABLE_LOCATION+ "("
                +Column.ID+ " INTEGER PRIMARY KEY,"
                +Column.TIMESTAMP+ " INTEGER,"
                +Column.PM25+ " INTEGER,"
                +Column.PM100+ " INTEGER,"
                +Column.LAT+ " REAL,"
                +Column.LNG+ " REAL,"
                +Column.ACC+ " REAL,"
                +Column.TOTAL_SATE+ " INTEGER,"
                +Column.USE_SATE+ " INTEGER,"
                +Column.ELAPSE_TIME+ " INTEGER,"
                +Column.POSITION_STATUS+ " TEXT);";

        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_LOCATION+ ";");
        onCreate(db);
    }

    public void drop() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_LOCATION+ ";");
        onCreate(db);
    }
    public void add(CurrentStatus currentStatus) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_LOCATION, null, currentStatus.toValues());

        db.close();
    }

    public CurrentStatus latestRow() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " +TABLE_LOCATION+ " LIMIT 1;";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst())
            return new CurrentStatus(cursor);
        else
            return null;
    }
}
