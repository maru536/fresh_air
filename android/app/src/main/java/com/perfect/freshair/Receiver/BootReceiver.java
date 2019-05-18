package com.perfect.freshair.Receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.perfect.freshair.API.GPSServerInterface;
import com.perfect.freshair.Callback.GpsCallback;
import com.perfect.freshair.Callback.ResponseCallback;
import com.perfect.freshair.Callback.TimeoutCallback;
import com.perfect.freshair.Common.CommonEnumeration;
import com.perfect.freshair.DB.DustGPSDBHandler;
import com.perfect.freshair.Model.DustGPS;
import com.perfect.freshair.Model.Gps;
import com.perfect.freshair.Model.GpsSetting;
import com.perfect.freshair.Utils.GPSUtils;
import com.perfect.freshair.Utils.PreferencesUtils;

import java.util.Set;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private static final int STATE_DISCOVERED = 3;
    private static final int STATE_RECEIVED = 4;

    public static final int MIN_LOCATION_UPDATE_TIME = 0;
    public static final int MIN_LOCATION_UPDATE_DISTANCE = 0;

    private int connectionState = STATE_DISCONNECTED;
    private int dustVaule = -1;
    private BluetoothGatt bluetoothGatt;
    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;
    private Context appContext;
    private GPSUtils mGPSUtils;
    private GPSServerInterface mAPIServer;
    private DustGPSDBHandler mLocDB;
    private GpsSetting gpsSetting;

    @Override
    public void onReceive(Context _context, Intent _intent) {
        this.appContext = _context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mGPSUtils = new GPSUtils(_context);
        mAPIServer = new GPSServerInterface();
        mLocDB = new DustGPSDBHandler(this.appContext);

        this.gpsSetting = new GpsSetting(
                PreferencesUtils.getGpsRequest(this.appContext),
                PreferencesUtils.getNetworkRequest(this.appContext),
                PreferencesUtils.getPassiveRequest(this.appContext)
        );

        this.handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                setConnectionState(msg.what);
            }
        };

        if (this.bluetoothAdapter != null && this.bluetoothAdapter.isEnabled() && this.appContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            String addr = PreferencesUtils.getDeviceAddress(this.appContext);
            if (addr != null) {
                BluetoothDevice device = null;
                Set<BluetoothDevice> pairedDevices = this.bluetoothAdapter.getBondedDevices();

                for (BluetoothDevice curDevice : pairedDevices) {
                    if (addr.equals(curDevice.getAddress())) {
                        device = curDevice;
                        break;
                    }
                }

                bleConnect(device);
            }
        }
    }

    private void bleConnect(BluetoothDevice device) {
        this.bluetoothGatt = device.connectGatt(appContext, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
        this.handler.sendEmptyMessage(STATE_CONNECTING);
    }

    void setConnectionState(int state) {
        switch (state) {
            case STATE_DISCONNECTED:
                connectionState = STATE_DISCONNECTED;
                break;
            case STATE_CONNECTING:
                connectionState = STATE_CONNECTING;
                break;
            case STATE_CONNECTED:
                connectionState = STATE_CONNECTED;
                break;
            case STATE_DISCOVERED:
                connectionState = STATE_DISCOVERED;
                break;
            case STATE_RECEIVED:
                connectionState = STATE_RECEIVED;
                break;
        }
    }

    private final BluetoothGattCallback mGattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                    int newState) {
                    String intentAction;
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        handler.sendEmptyMessage(STATE_CONNECTED);
                        bluetoothGatt.discoverServices();
                        Log.i(TAG, "BLE connect");
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        handler.sendEmptyMessage(STATE_DISCONNECTED);
                        Log.i(TAG, "BLE disconnect");
                        bleConnect(gatt.getDevice());
                    }
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        setCharacteristicNotification(gatt);
                        handler.sendEmptyMessage(STATE_DISCOVERED);
                    }
                }

                @Override
                // Result of a characteristic read operation
                public void onCharacteristicRead(BluetoothGatt gatt,
                                                 BluetoothGattCharacteristic characteristic,
                                                 int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        //not used
                    }
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);
                    dustVaule = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT32, 0);
                    handler.sendEmptyMessage(STATE_RECEIVED);
                    Log.i(TAG, "Received value: " + dustVaule);
                    //Toast.makeText(appContext, "Received value: ", Toast.LENGTH_LONG).show();
                    mGPSUtils.requestGPS(new GpsCallback() {
                        @Override
                        public void onGpsChanged(Gps gps) {

                        }
                    });
                }
            };

    private void setCharacteristicNotification(BluetoothGatt gatt) {

        BluetoothGattService blueinoService = gatt.getService(CommonEnumeration.UUID_SERVICE);
        if (blueinoService == null)
            return;

        BluetoothGattCharacteristic receiveCharacteristic = blueinoService.getCharacteristic(CommonEnumeration.UUID_RECEIVE);
        if (receiveCharacteristic == null)
            return;

        gatt.setCharacteristicNotification(receiveCharacteristic, true);
        BluetoothGattDescriptor descriptor = receiveCharacteristic.getDescriptor(CommonEnumeration.UUID_CLIENT_CONFIGURATION);
        if (descriptor == null)
            return;

        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location _location) {
            DustGPS newLoc = new DustGPS(dustVaule, _location);
            mAPIServer.postDustGPS(PreferencesUtils.getUser(appContext), newLoc, mResponseCallback);
            Log.i(TAG, "Provider: " +_location.getProvider()+ "Loc: " +newLoc.toString());
            mLocDB.add(newLoc);
            mGPSUtils.stopGPS();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.i(TAG, "onStatusChanged: String) " + s + "/int) " + i + "/bundle) " + bundle.toString());
        }

        @Override
        public void onProviderEnabled(String s) {
            Log.i(TAG, "onProviderEnabled: " + s);
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.i(TAG, "onProviderDisabled: " + s);
        }
    };

    private final ResponseCallback mResponseCallback = new ResponseCallback() {
        @Override
        public void responseCallback(int _resultCode) {

        }
    };
}
