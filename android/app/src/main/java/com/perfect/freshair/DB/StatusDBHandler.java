package com.perfect.freshair.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.perfect.freshair.Model.CurrentStatus;

import java.util.ArrayList;
import java.util.List;

public class StatusDBHandler extends SQLiteOpenHelper {
    public static final String TAG = "StatusDBHandler";
    public static final int DB_VER = 2;
    private static final String DB_NAME = "StatusDB.db";
    public static final String TABLE_LOCATION = "status";

    public enum Column {
        ID, TIMESTAMP, PM25, PM100;
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
                +Column.PM100+ " INTEGER);";

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
        String query = "SELECT * FROM " +TABLE_LOCATION+ " ORDER BY " +Column.TIMESTAMP+ " DESC LIMIT 1;";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst())
            return new CurrentStatus(cursor);
        else
            return null;
    }

    public List<CurrentStatus> search(long startTime, long endTime) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<CurrentStatus> searchedList = new ArrayList<>();
        String query = "SELECT * FROM " +TABLE_LOCATION+ " WHERE ";

        query += Column.TIMESTAMP+ " >= " +startTime+ " AND "
                + Column.TIMESTAMP+ " < " +endTime+ ";";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null &&  cursor.moveToFirst()) {
            do {
                searchedList.add(new CurrentStatus(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return searchedList;
    }
}
