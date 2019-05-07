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
    private Handler mHandler = null;
    private ScanCallback mScanCallback = null;

    public BlueToothUtils(Context context) {
        this.context = context;

        final BluetoothManager bluetoothManager =
                (BluetoothManager) this.context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    public void setHandler(Handler handler)
    {
        mHandler = handler;
    }

    public void setScanCallback(ScanCallback callback)
    {
        mScanCallback = callback;
    }

    public boolean getBLESupported()
    {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.e(toString(), "ble is not supported");
            return false;
        }
        return true;
    }

    public boolean getBLEEnabled()
    {
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.e(toString(),"ble is not enabled");
            return false;
        }
        return true;
    }

    public void scanLeDevice(final boolean enable, List<ScanFilter> filters, ScanSettings scanSettings) {
        if (enable && mScanCallback != null) {
            // Stops scanning after a pre-defined scan period.
            mScanning = true;
            bluetoothAdapter.getBluetoothLeScanner().startScan(filters, scanSettings, mScanCallback);
        } else {
            mScanning = false;
            bluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
        }
    }

    public boolean isScanning()
    {
        return mScanning;
    }
}
