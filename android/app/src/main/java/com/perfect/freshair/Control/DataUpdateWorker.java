package com.perfect.freshair.Control;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.perfect.freshair.Callback.MyBLEScanCallback;
import com.perfect.freshair.R;

public class DataUpdateWorker extends Worker {

    private BluetoothAdapter bluetoothAdapter;
    private boolean mScanning = false;
    private MyBLEScanCallback mScanCallback = new MyBLEScanCallback();

    public DataUpdateWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e("worker","working");

        if (!getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.e(toString(),"ble is not supported");
        }

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.e(toString(),"ble is not enabled");
        }



        return Result.success();
    }



    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mScanning = true;
            bluetoothAdapter.getBluetoothLeScanner().startScan(mScanCallback);
        } else {
            mScanning = false;
        }
    }
}
