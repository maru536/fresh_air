package com.perfect.freshair.View;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.perfect.freshair.Common.CommonEnumeration;
import com.perfect.freshair.R;
import com.perfect.freshair.Service.GPSService;

public class MainActivity extends NavActivity implements View.OnClickListener {

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private static final int STATE_DISCOVERED = 3;
    private static final int STATE_RECEIVED = 4;

    private TextView txtConnection;
    private TextView txtValue;
    private ProgressBar progressBar;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private Handler mHandler;
    private int airsensorValue = 0;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check whether or not BLE is supported
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.BLE_NOT_SUPPORTED, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // Ensures Bluetooth is available on the device and it is enabled. If not,
            // displays a dialog requesting user permission to enable Bluetooth.
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, CommonEnumeration.REQUEST_ENABLE_BT);
            }
        }

        //for communication with BLE, check the permission
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    CommonEnumeration.REQUEST_COARSE_LOCATION_PERMISSIONS);
        }

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                setConnectionState(msg.what);
            }
        };

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        txtConnection = (TextView) findViewById(R.id.activity_main_txt_bluetooth_connection);
        txtConnection.setOnClickListener(this);
        txtValue = (TextView)findViewById(R.id.activity_main_txt_bluetooth_value);
        progressBar = (ProgressBar) findViewById(R.id.activity_main_progress);
        setConnectionState(STATE_DISCONNECTED);

        Intent intent = new Intent(this, GPSService.class);
        startService(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_main_txt_bluetooth_connection:
                if (mConnectionState == STATE_DISCONNECTED) {
                    Intent intent = new Intent(this, ScanDeviceActivity.class);
                    startActivityForResult(intent, CommonEnumeration.REQUEST_ACTIVITY_SCAN);
                }
                break;
        }
    }

    void setConnectionState(int state) {
        switch (state) {
            case STATE_DISCONNECTED:
                mConnectionState = STATE_DISCONNECTED;
                txtValue.setVisibility(View.INVISIBLE);
                txtConnection.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                break;
            case STATE_CONNECTING:
                mConnectionState = STATE_CONNECTING;
                txtValue.setVisibility(View.INVISIBLE);
                txtConnection.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                break;
            case STATE_CONNECTED:
                mConnectionState = STATE_CONNECTED;
                txtValue.setVisibility(View.INVISIBLE);
                txtConnection.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                break;
            case STATE_DISCOVERED:
                mConnectionState = STATE_DISCOVERED;
                txtValue.setVisibility(View.VISIBLE);
                txtConnection.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                break;
            case STATE_RECEIVED:
                mConnectionState = STATE_RECEIVED;
                txtValue.setText(airsensorValue+getResources().getString(R.string.AIRSENSOR_UNIT));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        close();
    }

    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CommonEnumeration.REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    //go ahead...
                } else {
                    Toast.makeText(this, R.string.BLE_NOT_SUPPORTED, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case CommonEnumeration.REQUEST_ACTIVITY_SCAN:
                if (resultCode == RESULT_OK) {
                    mBluetoothAdapter.cancelDiscovery();
                    BluetoothDevice device = data.getParcelableExtra("device");
                    if (device != null) {
                        mBluetoothGatt = device.connectGatt(this, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
                        mHandler.sendEmptyMessage(STATE_CONNECTING);
                    }
                }
                break;
        }

    }

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


    // Various callback methods defined by the BLE API.
    private final BluetoothGattCallback mGattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                    int newState) {
                    String intentAction;
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        mHandler.sendEmptyMessage(STATE_CONNECTED);
                        mBluetoothGatt.discoverServices();

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        mHandler.sendEmptyMessage(STATE_DISCONNECTED);
                    }
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        setCharacteristicNotification(gatt);
                        mHandler.sendEmptyMessage(STATE_DISCOVERED);
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
                    airsensorValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT32, 0);
                    mHandler.sendEmptyMessage(STATE_RECEIVED);
                }
            };
}
