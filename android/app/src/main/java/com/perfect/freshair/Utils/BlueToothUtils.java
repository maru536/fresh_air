package com.perfect.freshair.Utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.perfect.freshair.Common.CommonEnumeration;
import com.perfect.freshair.R;

import java.util.List;

public class BlueToothUtils {

    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private boolean mScanning = false;

    public BlueToothUtils(Context context) {
        this.context = context;

        final BluetoothManager bluetoothManager =
                (BluetoothManager) this.context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    public boolean getBLESupported() {
        if(bluetoothAdapter == null)
            return false;
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.e(toString(), "ble is not supported");
            return false;
        }
        return true;
    }

    public boolean getBLEEnabled() {
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.e(toString(), "ble is not enabled");
            return false;
        }
        return true;
    }

    public void scanLeDevice(final boolean enable, List<ScanFilter> filters, ScanSettings scanSettings, ScanCallback callback) {
        if (bluetoothAdapter != null && callback != null && filters != null && scanSettings != null) {
            if(enable)
            {
                mScanning = true;
                bluetoothAdapter.getBluetoothLeScanner().startScan(filters, scanSettings, callback);
            }else
            {
                mScanning = false;
                bluetoothAdapter.getBluetoothLeScanner().stopScan(callback);
            }
        }
    }

    public void scanLeDevice(final boolean enable, ScanCallback callback) {
        if(bluetoothAdapter != null && callback != null) {
            if (enable ) {
                // Stops scanning after a pre-defined scan period.
                mScanning = true;
                bluetoothAdapter.getBluetoothLeScanner().startScan(callback);
            } else {
                mScanning = false;
                bluetoothAdapter.getBluetoothLeScanner().stopScan(callback);
            }
        }
    }

    public boolean isScanning() {
        return mScanning;
    }
}
