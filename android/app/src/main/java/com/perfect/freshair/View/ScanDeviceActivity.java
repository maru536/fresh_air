package com.perfect.freshair.View;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.perfect.freshair.Control.ScanArrayAdapter;
import com.perfect.freshair.R;

public class ScanDeviceActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning = false;
    private Handler mHandler;

    private ScanArrayAdapter arrayAdapter;
    private ListView listView;
    private TextView txtScan;
    private TextView txtCancel;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_device);

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        arrayAdapter = new ScanArrayAdapter();
        mHandler = new Handler();

        txtScan = (TextView)findViewById(R.id.activity_scan_txt_scan);
        txtCancel = (TextView)findViewById(R.id.activity_scan_txt_cancel);
        listView = (ListView)findViewById(R.id.activity_scan_listview);

        txtScan.setOnClickListener(this);
        txtCancel.setOnClickListener(this);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);


    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);
            arrayAdapter.cleanItems();
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!arrayAdapter.isAlreadyItem(device.getAddress()))
                            {
                                arrayAdapter.addItem(device);
                                arrayAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            };

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.activity_scan_txt_scan:
                scanLeDevice(true);
                break;
            case R.id.activity_scan_txt_cancel:
                scanLeDevice(false);
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent();
        mHandler.removeCallbacksAndMessages(null);
        scanLeDevice(false);
        BluetoothDevice device = (BluetoothDevice)adapterView.getItemAtPosition(i);
        intent.putExtra("device",device);
        setResult(RESULT_OK, intent);
        finish();
    }
}
