package com.perfect.freshair.Utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.perfect.freshair.Common.CommonEnumeration;
import com.perfect.freshair.R;

public class BlueToothUtils {
    //check whether or not BLE is supported
    public static boolean isAvailable(Context _context) {
        return false;
    }

    public static boolean getPermission() {
        return false;
    }

    public static boolean requestBluetoothOn() {
        return false;
    }
}
