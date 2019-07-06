package com.perfect.freshair.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.perfect.freshair.Model.Measurement;

import java.util.ArrayList;
import java.util.List;

public class MeasurementDBHandler extends SQLiteOpenHelper {
    public static final String TAG = "MeasurementDBHandler";
    public static final int DB_VER = 6;
    private static final String DB_NAME = "StatusDB.db";
    public static final String TABLE_LOCATION = "status";

    public enum Column {
        ID, TIMESTAMP, PM25, PM100, LATITUDE, LONGITUDE, PROVIDER, ACCURACY;
    }

    public MeasurementDBHandler(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE " +TABLE_LOCATION+ "("
                +Column.ID+ " INTEGER PRIMARY KEY,"
                +Column.TIMESTAMP+ " INTEGER,"
                +Column.PM25+ " INTEGER,"
                +Column.PM100+ " INTEGER,"
                +Column.LATITUDE + " REAL,"
                +Column.LONGITUDE + " REAL,"
                +Column.PROVIDER+ " TEXT,"
                +Column.ACCURACY+ " REAL);";

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
    public void add(Measurement measurement) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_LOCATION, null, measurement.toValues());

        db.close();
    }

    public Measurement latestRow() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " +TABLE_LOCATION+ " ORDER BY " +Column.TIMESTAMP+ " DESC LIMIT 1;";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst())
            return new Measurement(cursor);
        else
            return null;
    }

    public List<Measurement> search(long startTime, long endTime) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Measurement> searchedList = new ArrayList<>();
        String query = "SELECT * FROM " +TABLE_LOCATION+ " WHERE ";

        query += Column.TIMESTAMP+ " >= " +startTime+ " AND "
                + Column.TIMESTAMP+ " < " +endTime+ ";";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null &&  cursor.moveToFirst()) {
            do {
                searchedList.add(new Measurement(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return searchedList;
    }

    public List<Measurement> searchAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Measurement> searchedList = new ArrayList<>();
        String query = "SELECT * FROM " +TABLE_LOCATION+ ";";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null &&  cursor.moveToFirst()) {
            do {
                searchedList.add(new Measurement(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return searchedList;
    }
}
