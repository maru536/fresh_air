package com.perfect.freshair.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.perfect.freshair.Model.DustGPS;

import java.sql.Timestamp;
import java.util.ArrayList;

public class DustGPSDBHandler extends SQLiteOpenHelper {
    public static final String TAG = "LocationDBHandle";
    public static final int DB_VER = 2;
    private static final String DB_NAME = "LocationDB.db";
    public static final String TABLE_LOCATION = "locations";

    public enum Column {
        ID, PROVIDER, TIME, ELAPSE_TIME,
        LAT, LNG, ALT, ACC,
        SPEED, SPEED_ACC, VERT_ACC, BEARING,
        BEARING_ACC, DUST, IS_POSTED;
    }

    public DustGPSDBHandler(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE " +TABLE_LOCATION+ "("
                +Column.ID+ " INTEGER PRIMARY KEY,"
                +Column.PROVIDER+ " TEXT,"
                +Column.TIME+ " INTEGER,"
                +Column.ELAPSE_TIME+ " INTEGER,"
                +Column.LAT+ " REAL,"
                +Column.LNG+ " REAL,"
                +Column.ALT+ " REAL,"
                +Column.ACC+ " REAL,"
                +Column.SPEED+ " REAL,"
                +Column.SPEED_ACC+ " REAL,"
                +Column.VERT_ACC+ " REAL,"
                +Column.BEARING+ " REAL,"
                +Column.BEARING_ACC+ " REAL,"
                +Column.DUST+ " INTEGER);";

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
    public void add(DustGPS dustGPS) {
        SQLiteDatabase db = this.getWritableDatabase();

        Log.i(TAG, dustGPS.toString());
        long result = db.insert(TABLE_LOCATION, null, dustGPS.toValues());

        db.close();
    }

    public ArrayList<DustGPS> search(String type, Timestamp startTime, Timestamp endTime, int minAcc, int maxAcc) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DustGPS> dustGPS = new ArrayList<>();
        String query = "SELECT * FROM " +TABLE_LOCATION+ " WHERE ";

        /*if (!type.equals(DustGPS.ProviderType.ALL))
            query += Column.PROVIDER+ " = \"" +type+ "\" AND ";
        */
        query += Column.TIME+ " >= " +startTime.getTime()+ " AND "
                +Column.TIME+ " <= " +endTime.getTime()+ " AND "
                +Column.ACC+ " >= " +minAcc+ " AND "
                +Column.ACC+ " <= " +maxAcc+
                " ORDER BY " +Column.TIME+ " ASC;";

        Log.i(TAG, query);
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null &&  cursor.moveToFirst()) {
            do {
                dustGPS.add(new DustGPS(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return dustGPS;
    }
}
