package com.perfect.freshair.Control;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.perfect.freshair.API.DustServerInterface;
import com.perfect.freshair.Callback.LocationCallback;
import com.perfect.freshair.Callback.ResponseCallback;
import com.perfect.freshair.Common.CommonEnumeration;
import com.perfect.freshair.Controller.GpsController;
import com.perfect.freshair.DB.MeasurementDBHandler;
import com.perfect.freshair.Model.Gps;
import com.perfect.freshair.Model.Measurement;
import com.perfect.freshair.Model.Dust;
import com.perfect.freshair.Utils.BlueToothUtils;
import com.perfect.freshair.Utils.MyBLEPacketUtilis;
import com.perfect.freshair.Utils.PreferencesUtils;

import java.util.Random;

public class DataUpdateWorker extends Worker {
    private static final String TAG = "DataUpdateWorker";

    private BlueToothUtils blueToothUtils;
    private boolean isDustReceive;
    private boolean isLocationReceive;
    private Measurement receivedMeasurement;
    private Gps receivedGps;
    private Dust receivedDust;
    private MeasurementDBHandler measurementDBHandler;
    private DustServerInterface serverInterface = null;
    private GpsController gpsController;

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

            init();
            isDustReceive = true;
            receivedDust = new Dust(MyBLEPacketUtilis.getMajor(majorMinor), MyBLEPacketUtilis.getMinor(majorMinor));
            blueToothUtils.scanLeDevice(false, scanCallback);
            gpsController.requestGPS(locationCallback);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    private void init() {
        isLocationReceive = false;
        isDustReceive = false;
        receivedDust = null;
        receivedMeasurement = null;
    }

    private final ResponseCallback responseCallback = new ResponseCallback() {
        @Override
        public void responseCallback(int _resultCode) {
            Log.i(TAG, ""+ _resultCode);
        }
    };

    public DataUpdateWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        measurementDBHandler = new MeasurementDBHandler(getApplicationContext());
        gpsController = new GpsController(getApplicationContext());
        //gpsUtils = new GpsController(getApplicationContext());
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i(this.toString(), "working");
        /*SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.my_preference_ble_file_key), Context.MODE_PRIVATE);
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
                init();
                blueToothUtils.scanLeDevice(true, filters, scanSettings, scanCallback);
            } else {
                Log.i(this.toString(), "bluetooth is not enabled or supported.");
            }
        }
        */

        init();
        Random random = new Random();
        receivedDust = new Dust(random.nextInt(150), random.nextInt(150));
        isDustReceive = true;
        gpsController.requestGPS(locationCallback);

        return Result.success();
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationChanged(Location location) {
            isLocationReceive = true;
            receivedGps = new Gps(location.getProvider(), location.getLatitude(), location.getLongitude(), location.getAccuracy());

            if (isAllReceive()) {
                receivedMeasurement = new Measurement(System.currentTimeMillis(), receivedDust, receivedGps);
                if (serverInterface == null)
                    serverInterface = new DustServerInterface();

                Log.i(this.toString(), receivedMeasurement.toString());
                measurementDBHandler.add(receivedMeasurement);
                serverInterface.postDust(PreferencesUtils.getUser(getApplicationContext()), receivedMeasurement, responseCallback);

                Intent intent = new Intent();
                intent.setAction(CommonEnumeration.dataUpdateAction);
                intent.putExtra("pm100", receivedMeasurement.getDust().getPm100());
                intent.putExtra("pm25", receivedMeasurement.getDust().getPm25());
                getApplicationContext().sendBroadcast(intent);
            }
        }
    };

    private boolean isAllReceive() {
        return (isDustReceive && isLocationReceive && receivedDust != null && receivedGps != null);
    }
}
