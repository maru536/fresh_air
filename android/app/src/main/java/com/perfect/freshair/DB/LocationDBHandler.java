package com.perfect.freshair.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import com.perfect.freshair.Model.LocationData;

import java.sql.Timestamp;
import java.util.ArrayList;

public class LocationDBHandler extends SQLiteOpenHelper {
    public static final String TAG = "LocationDBHandle";
    public static final int DB_VER = 1;
    private static final String DB_NAME = "LocationDB.db";
    public static final String TABLE_LOCATION = "locations";

    public enum Column {
        ID("id", 0), PROVIDER(LocationData.Key.PROVIDER.getKey(), 1),
        TIME(LocationData.Key.TIME.getKey(), 2), ELAPSE_TIME(LocationData.Key.ELAPSE_TIME.getKey(), 3),
        LAT(LocationData.Key.LAT.getKey(), 4), LNG(LocationData.Key.LNG.getKey(), 5),
        ALT(LocationData.Key.ALT.getKey(), 6), ACC(LocationData.Key.ACC.getKey(), 7),
        SPEED(LocationData.Key.SPEED.getKey(), 8), SPEED_ACC(LocationData.Key.SPEED_ACC.getKey(), 9), VERT_ACC(LocationData.Key.VERT_ACC.getKey(), 10),
        BEARING(LocationData.Key.BEARING.getKey(), 11), BEARING_ACC(LocationData.Key.BEARING_ACC.getKey(), 12);

        private String mKey;
        private int mIdx;

        private Column(String key, int idx) { mKey = key; mIdx = idx; }

        public String getKey() { return mKey; }

        public int getIdx() { return mIdx; }
    }

    public LocationDBHandler(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE " +TABLE_LOCATION+ "("
                +Column.ID.getKey()+ " INTEGER PRIMARY KEY,"
                +Column.PROVIDER.getKey()+ " TEXT,"
                +Column.TIME.getKey()+ " INTEGER,"
                +Column.ELAPSE_TIME.getKey()+ " INTEGER,"
                +Column.LAT.getKey()+ " REAL,"
                +Column.LNG.getKey()+ " REAL,"
                +Column.ALT.getKey()+ " REAL,"
                +Column.ACC.getKey()+ " REAL,"
                +Column.SPEED.getKey()+ " REAL,"
                +Column.SPEED_ACC.getKey()+ " REAL,"
                +Column.VERT_ACC.getKey()+ " REAL,"
                +Column.BEARING.getKey()+ " REAL,"
                +Column.BEARING_ACC.getKey()+ " REAL);";

        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_LOCATION+ ";");
        onCreate(db);
    }

    public void add(LocationData loc) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_LOCATION, null, loc.toValues());
        db.close();
    }

    public ArrayList<LocationData> search(String type, Timestamp startTime, Timestamp endTime, int minAcc, int maxAcc) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<LocationData> locations = new ArrayList<>();
        String query = "SELECT * FROM " +TABLE_LOCATION+ " WHERE ";

        if (!type.equals(LocationData.ALL))
            query += Column.PROVIDER.getKey()+ " = \"" +type+ "\" AND ";

        query += Column.TIME.getKey()+ " >= " +startTime.getTime()+ " AND "
                +Column.TIME.getKey()+ " <= " +endTime.getTime()+ " AND "
                +Column.ACC.getKey()+ " >= " +minAcc+ " AND "
                +Column.ACC.getKey()+ " <= " +maxAcc+
                " ORDER BY " +Column.TIME.getKey()+ " ASC;";

        Log.i(TAG, query);
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null &&  cursor.moveToFirst()) {
            do {
                locations.add(new LocationData(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return locations;
    }
}
