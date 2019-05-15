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

import com.perfect.freshair.DB.StatusDBHandler;
import com.perfect.freshair.Model.CurrentStatus;
import com.perfect.freshair.Model.Dust;
import com.perfect.freshair.Model.Gps;
import com.perfect.freshair.Model.Position;
import com.perfect.freshair.Model.PositionStatus;
import com.perfect.freshair.R;
import com.perfect.freshair.Utils.BlueToothUtils;
import com.perfect.freshair.Utils.MyBLEPacketUtilis;

import java.util.ArrayList;
import java.util.List;

public class DataUpdateWorker extends Worker {

    private BlueToothUtils blueToothUtils;
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            byte[] data = result.getScanRecord().getBytes();
            byte[] majorMinor = MyBLEPacketUtilis.getMajorMinor(data);
            blueToothUtils.scanLeDevice(false, scanCallback);
            StatusDBHandler statusDBHandler = new StatusDBHandler(getApplicationContext());
            statusDBHandler.add(new CurrentStatus(System.currentTimeMillis(), new Dust(MyBLEPacketUtilis.getMajor(majorMinor), MyBLEPacketUtilis.getMajor(majorMinor)), new Gps(new Position(-1.0, -1.0), -1, 1, 1, 5000), PositionStatus.INDOOR));
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

            blueToothUtils.scanLeDevice(true, filters, scanSettings, scanCallback);
        }
        return Result.success();
    }

}
