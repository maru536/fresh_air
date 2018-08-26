package com.perfect.freshair.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.perfect.freshair.Model.DustWithLocation;

import java.sql.Timestamp;
import java.util.ArrayList;

public class DustLocationDBHandler extends SQLiteOpenHelper {
    public static final String TAG = "LocationDBHandle";
    public static final int DB_VER = 2;
    private static final String DB_NAME = "LocationDB.db";
    public static final String TABLE_LOCATION = "locations";

    public enum Column {
        ID(0), PROVIDER(1), TIME(2), ELAPSE_TIME(3),
        LAT(4), LNG(5), ALT(6), ACC(7),
        SPEED(8), SPEED_ACC(9), VERT_ACC(10), BEARING(11),
        BEARING_ACC(12), DUST(13);

        private int mIdx;

        private Column(int idx) { mIdx = idx; }

        public int getIdx() { return mIdx; }
    }

    public DustLocationDBHandler(Context context) {
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
                +Column.BEARING_ACC+ " REAL, "
                +Column.DUST+ "INTEGER);";

        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_LOCATION+ ";");
        onCreate(db);
    }

    public void add(DustWithLocation dustWithLocation) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_LOCATION, null, dustWithLocation.toValues());
        db.close();
    }

    public ArrayList<DustWithLocation> search(String type, Timestamp startTime, Timestamp endTime, int minAcc, int maxAcc) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DustWithLocation> dustWithLocations = new ArrayList<>();
        String query = "SELECT * FROM " +TABLE_LOCATION+ " WHERE ";

        if (!type.equals(DustWithLocation.ProviderType.ALL))
            query += Column.PROVIDER+ " = \"" +type+ "\" AND ";

        query += Column.TIME+ " >= " +startTime.getTime()+ " AND "
                +Column.TIME+ " <= " +endTime.getTime()+ " AND "
                +Column.ACC+ " >= " +minAcc+ " AND "
                +Column.ACC+ " <= " +maxAcc+
                " ORDER BY " +Column.TIME+ " ASC;";

        Log.i(TAG, query);
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null &&  cursor.moveToFirst()) {
            do {
                dustWithLocations.add(new DustWithLocation (cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return dustWithLocations;
    }
}
