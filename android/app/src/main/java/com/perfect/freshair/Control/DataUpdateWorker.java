package com.perfect.freshair.Control;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.perfect.freshair.Callback.GpsCallback;
import com.perfect.freshair.DB.StatusDBHandler;
import com.perfect.freshair.Model.CurrentStatus;
import com.perfect.freshair.Model.Dust;
import com.perfect.freshair.Model.Gps;
import com.perfect.freshair.Model.GpsProvider;
import com.perfect.freshair.Model.Position;
import com.perfect.freshair.Model.PositionStatus;
import com.perfect.freshair.Model.Satellite;
import com.perfect.freshair.R;
import com.perfect.freshair.Utils.BlueToothUtils;
import com.perfect.freshair.Utils.GpsUtils;
import com.perfect.freshair.Utils.MyBLEPacketUtilis;

import java.util.ArrayList;
import java.util.List;

public class DataUpdateWorker extends Worker {

    private BlueToothUtils blueToothUtils;
    private boolean isDustReceive;
    private Dust receivedDust;
    private StatusDBHandler statusDBHandler;
    private GpsUtils gpsUtils;

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            byte[] data = result.getScanRecord().getBytes();
            byte[] majorMinor = MyBLEPacketUtilis.getMajorMinor(data);

            isDustReceive = true;
            receivedDust = new Dust(MyBLEPacketUtilis.getMajor(majorMinor), MyBLEPacketUtilis.getMajor(majorMinor));
            gpsUtils.requestGPS(gpsCallback);
            blueToothUtils.scanLeDevice(false, scanCallback);
            Log.i("Major", MyBLEPacketUtilis.getMajor(majorMinor)+"");
            Log.i("Minor", MyBLEPacketUtilis.getMajor(majorMinor)+"");
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    public DataUpdateWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        statusDBHandler = new StatusDBHandler(getApplicationContext());
        gpsUtils = new GpsUtils(getApplicationContext());
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i("worker","working");
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.my_preference_ble_file_key), Context.MODE_PRIVATE);
        String defaultValue = "none";
        String deviceAddr = sharedPreferences.getString(getApplicationContext().getString(R.string.my_preference_ble_addr_key), defaultValue);
        if(deviceAddr.equals(defaultValue))
        {
            return Result.success();
        }else
        {
            Log.i("worker",deviceAddr);
            blueToothUtils = new BlueToothUtils(getApplicationContext());
            List<ScanFilter> filters = new ArrayList<ScanFilter>();
            ScanFilter.Builder scanFilterBuilder = new ScanFilter.Builder();
            ScanFilter scanFilter = scanFilterBuilder.setDeviceAddress(deviceAddr).build();
            filters.add(scanFilter);

            ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
            ScanSettings scanSettings = scanSettingsBuilder.build();

            isDustReceive = false;
            receivedDust = null;
            blueToothUtils.scanLeDevice(true, filters, scanSettings, scanCallback);
        }
        return Result.success();
    }

    private GpsCallback gpsCallback = new GpsCallback() {
        @Override
        public void onGpsChanged(Gps gps) {
            if (isDustReceive)
                statusDBHandler.add(new CurrentStatus(System.currentTimeMillis(), receivedDust, gps));
        }
    };
}
