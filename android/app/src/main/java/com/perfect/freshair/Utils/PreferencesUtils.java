package com.perfect.freshair.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtils {
    public static final String PREFERENCES_DEVICE = "device";
    public static final String ADDRESS = "address";

    public static boolean saveDeviceAddress(Context _appContext, String _deviceAddress) {
        SharedPreferences device = _appContext.getSharedPreferences(PREFERENCES_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = device.edit();
        editor.putString(ADDRESS, _deviceAddress);
        return editor.commit();
    }

    public static String getDeviceAddress(Context _appContext) {
        SharedPreferences device = _appContext.getSharedPreferences(PREFERENCES_DEVICE, Context.MODE_PRIVATE);
        return device.getString(ADDRESS, null);
    }
}
