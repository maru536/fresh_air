package com.perfect.freshair.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtils {
    public static final String PREFERENCES_DEVICE = "device";
    public static final String ADDRESS = "address";
    public static final String PREFERENCES_USER = "user";
    public static final String PREFERENCES_GPS_REQUEST_TYPE = "gps_request_type";
    public static final String ID = "id";
    public static final String GPS_REQUEST = "gps_request";
    public static final String NETWORK_REQUEST = "network_request";
    public static final String PASSIVE_REQUEST = "passive_request";
    public static final String PREFERENCES_THRESHOLD = "threshold";
    public static final String ACCURACY = "accuracy";
    public static final String NUM_SATELLITE = "num_satellite";
    public static final String ELAPSED_TIME = "elapsed_time";
    public static final String TIMEOUT = "timeout";
    public static final String PREFERENCES_ITER = "iterate";
    public static final String ITERATE = "iterate";

    public static boolean saveDeviceAddress(Context _appContext, String _deviceAddress) {
        SharedPreferences device = _appContext.getSharedPreferences(PREFERENCES_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = device.edit();
        editor.putString(ADDRESS, _deviceAddress);
        return editor.commit();
    }

    public static String getDeviceAddress(Context _appContext) {
        SharedPreferences device = _appContext.getSharedPreferences(PREFERENCES_DEVICE, Context.MODE_PRIVATE);
        return device.getString(ADDRESS, "");
    }

    public static boolean saveUser(Context _appContext, String _userId) {
        SharedPreferences user = _appContext.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user.edit();
        editor.putString(ID, _userId);
        return editor.commit();
    }

    public static boolean clearUser(Context _appContext) {
        SharedPreferences user = _appContext.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user.edit();
        editor.clear();
        return editor.commit();
    }

    public static String getUser(Context _appContext) {
        SharedPreferences user = _appContext.getSharedPreferences(PREFERENCES_USER, Context.MODE_PRIVATE);
        return user.getString(ID, "");
    }

    public static boolean saveGpsRequest(Context _appContext, boolean _gpsRequest) {
        SharedPreferences requestGps = _appContext.getSharedPreferences(PREFERENCES_GPS_REQUEST_TYPE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = requestGps.edit();
        editor.putBoolean(GPS_REQUEST, _gpsRequest);
        return editor.commit();
    }

    public static boolean getGpsRequest(Context _appContext) {
        SharedPreferences user = _appContext.getSharedPreferences(PREFERENCES_GPS_REQUEST_TYPE, Context.MODE_PRIVATE);
        return user.getBoolean(GPS_REQUEST, true);
    }

    public static boolean saveNetworkRequest(Context _appContext, boolean _networkRequest) {
        SharedPreferences requestGps = _appContext.getSharedPreferences(PREFERENCES_GPS_REQUEST_TYPE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = requestGps.edit();
        editor.putBoolean(NETWORK_REQUEST, _networkRequest);
        return editor.commit();
    }

    public static boolean getNetworkRequest(Context _appContext) {
        SharedPreferences user = _appContext.getSharedPreferences(PREFERENCES_GPS_REQUEST_TYPE, Context.MODE_PRIVATE);
        return user.getBoolean(NETWORK_REQUEST, true);
    }

    public static boolean savePassiveRequest(Context _appContext, boolean _passiveRequest) {
        SharedPreferences requestGps = _appContext.getSharedPreferences(PREFERENCES_GPS_REQUEST_TYPE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = requestGps.edit();
        editor.putBoolean(PASSIVE_REQUEST, _passiveRequest);
        return editor.commit();
    }

    public static boolean getPassiveRequest(Context _appContext) {
        SharedPreferences user = _appContext.getSharedPreferences(PREFERENCES_GPS_REQUEST_TYPE, Context.MODE_PRIVATE);
        return user.getBoolean(PASSIVE_REQUEST, true);
    }
}
