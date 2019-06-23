package com.perfect.freshair.Control;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.perfect.freshair.API.GPSServerInterface;
import com.perfect.freshair.Callback.GpsCallback;
import com.perfect.freshair.Callback.ResponseCallback;
import com.perfect.freshair.Common.CommonEnumeration;
import com.perfect.freshair.DB.StatusDBHandler;
import com.perfect.freshair.Model.CurrentStatus;
import com.perfect.freshair.Model.Dust;
import com.perfect.freshair.Model.Gps;
import com.perfect.freshair.Model.GpsProvider;
import com.perfect.freshair.Model.LatestDust;
import com.perfect.freshair.Model.Position;
import com.perfect.freshair.Model.PositionStatus;
import com.perfect.freshair.Model.Satellite;
import com.perfect.freshair.R;
import com.perfect.freshair.Utils.BlueToothUtils;
import com.perfect.freshair.Utils.GpsUtils;
import com.perfect.freshair.Utils.MyBLEPacketUtilis;
import com.perfect.freshair.Utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

public class DataUpdateWorker extends Worker {
    private static final String TAG = "DataUpdateWorker";

    private BlueToothUtils blueToothUtils;
    private boolean isDustReceive;
    private LatestDust latestDust;
    private Dust receivedDust;
    private StatusDBHandler statusDBHandler;
    private GpsUtils gpsUtils;
    private GPSServerInterface serverInterface = null;

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            byte[] data = result.getScanRecord().getBytes();
            byte[] majorMinor = MyBLEPacketUtilis.getMajorMinor(data);
            StringBuffer sb = new StringBuffer();
            for(int i=0;i<data.length;i++)
            {
                sb.append(data[i]);
                sb.append(" ");
            }
            Log.e("bytes", sb.toString());

            isDustReceive = true;
            receivedDust = new Dust(MyBLEPacketUtilis.getMajor(majorMinor), MyBLEPacketUtilis.getMinor(majorMinor));
            latestDust = new LatestDust(System.currentTimeMillis(), receivedDust);
            if (serverInterface == null)
                serverInterface = new GPSServerInterface();

            statusDBHandler.add(new CurrentStatus(System.currentTimeMillis(), receivedDust, new Gps(new Position(0.0, 0.0), GpsProvider.UNKNOWN, 0.0f, new Satellite(0,0), 0, PositionStatus.UNKNOWN)));
            serverInterface.postDust(PreferencesUtils.getUser(getApplicationContext()), latestDust, responseCallback);

            //gpsUtils.requestGPS(gpsCallback);
            blueToothUtils.scanLeDevice(false, scanCallback);
            Log.i("Major", latestDust.getPm25() + "");
            Log.i("Minor", latestDust.getPm100() + "");

            Intent intent = new Intent();
            intent.setAction(CommonEnumeration.dataUpdateAction);
            intent.putExtra("pm100", receivedDust.getPm100());
            intent.putExtra("pm25", receivedDust.getPm25());
            getApplicationContext().sendBroadcast(intent);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };



    private final ResponseCallback responseCallback = new ResponseCallback() {
        @Override
        public void responseCallback(int _resultCode) {
            Log.i(TAG, ""+ _resultCode);
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
        Log.i(this.toString(), "working");
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.my_preference_ble_file_key), Context.MODE_PRIVATE);
        String defaultValue = "none";
        String deviceAddr = sharedPreferences.getString(getApplicationContext().getString(R.string.my_preference_ble_addr_key), defaultValue);
        if (!deviceAddr.equals(defaultValue)) {
            Log.i(this.toString(), deviceAddr);
            blueToothUtils = new BlueToothUtils(getApplicationContext());
            if (blueToothUtils.getBLEEnabled() && blueToothUtils.getBLESupported()) {
                List<ScanFilter> filters = new ArrayList<ScanFilter>();
                ScanFilter.Builder scanFilterBuilder = new ScanFilter.Builder();
                ScanFilter scanFilter = scanFilterBuilder.setDeviceAddress(deviceAddr).build();
                filters.add(scanFilter);
                ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
                ScanSettings scanSettings = scanSettingsBuilder.build();
                isDustReceive = false;
                receivedDust = null;
                latestDust = null;
                blueToothUtils.scanLeDevice(true, filters, scanSettings, scanCallback);
            } else {
                Log.i(this.toString(), "bluetooth is not enabled or supported.");
            }
        }
        return Result.retry();
    }

    private GpsCallback gpsCallback = new GpsCallback() {
        @Override
        public void onGpsChanged(Gps gps) {
            if (isDustReceive)
                statusDBHandler.add(new CurrentStatus(System.currentTimeMillis(), receivedDust, gps));
        }
    };
}
