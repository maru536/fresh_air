package com.perfect.freshair.View;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.perfect.freshair.Common.BluetoothEnumeration;
import com.perfect.freshair.Control.BLEScanArrayAdapter;
import com.perfect.freshair.Model.MyBLEDevice;
import com.perfect.freshair.R;
import com.perfect.freshair.Utils.BlueToothUtils;
import com.perfect.freshair.Utils.MyBLEPacketUtilis;

import java.util.ArrayList;


public class DeviceRegisterActivity extends AppCompatActivity {

    private Button button;
    private ListView list;
    private BLEScanArrayAdapter listAdapter;
    private ArrayList<MyBLEDevice> items;

    private enum ScanState {
        START_SCAN,
        STOP_SCAN,
    }

    private ScanState scanState = ScanState.STOP_SCAN;
    private BlueToothUtils bleUtil;
    private Handler handler;
    private final Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            setScanState(ScanState.STOP_SCAN);
        }
    };
    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            final MyBLEDevice tempDevice = new MyBLEDevice(result.getDevice().getName(), result.getDevice().getAddress());
            final ScanRecord scanRecord = result.getScanRecord();
            byte[] data = scanRecord.getBytes();
            byte[] majorMinor = MyBLEPacketUtilis.getMajorMinor(data);
            Log.i("Major", MyBLEPacketUtilis.getMajor(majorMinor)+"");
            Log.i("Minor", MyBLEPacketUtilis.getMajor(majorMinor)+"");
            if (!items.contains(tempDevice)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        items.add(tempDevice);
                        listAdapter.notifyDataSetChanged();
                        byte[] data = scanRecord.getBytes();
                        byte[] majorMinor = MyBLEPacketUtilis.getMajorMinor(data);
                        Log.i("Major", MyBLEPacketUtilis.getMajor(majorMinor)+"");
                        Log.i("Minor", MyBLEPacketUtilis.getMajor(majorMinor)+"");
                    }
                });

            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.i("onScanFailed", "failed");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_register);

        //toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getStringArray(R.array.nav_strings)[0]);

        //handler
        handler = new Handler();

        //widget
        button = (Button) findViewById(R.id.ac_div_register_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (scanState) {
                    case START_SCAN:
                        if (handler != null) {
                            handler.removeCallbacks(scanRunnable);
                        }
                        setScanState(ScanState.STOP_SCAN);
                        break;
                    case STOP_SCAN:
                        handler.postDelayed(scanRunnable, 10000);
                        setScanState(ScanState.START_SCAN);
                        break;
                }
            }
        });

        //list
        items = new ArrayList<MyBLEDevice>();
        list = (ListView) findViewById(R.id.ac_div_register_list);
        listAdapter = new BLEScanArrayAdapter(this, items);
        list.setAdapter(listAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.my_preference_ble_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.my_preference_ble_addr_key), items.get(position).getAddr());
                editor.commit();
                Toast.makeText(getApplicationContext(), items.get(position).getAddr(), Toast.LENGTH_SHORT).show();
            }
        });

        bleUtil = new BlueToothUtils(this);
        if (bleUtil.getBLESupported()) {
            if (!bleUtil.getBLEEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, BluetoothEnumeration.REQUEST_ENABLE_BLUETOOTH);
            }
        } else {
            Toast.makeText(this, "Bluetooth service is not supported in this device", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BluetoothEnumeration.REQUEST_ENABLE_BLUETOOTH:
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth service must be enabled", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        setScanState(ScanState.STOP_SCAN);
        if (handler != null) {
            handler.removeCallbacks(scanRunnable);
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setScanState(ScanState state) {
        if (bleUtil != null) {
            scanState = state;
            switch (state) {
                case START_SCAN:
                    button.setText(getString(R.string.MY_BLE_SCANNING));
                    Log.i(toString(), "start scan");
                    bleUtil.scanLeDevice(true, scanCallback);
                    break;
                case STOP_SCAN:
                    button.setText(getString(R.string.MY_BLE_SCAN));
                    Log.i(toString(), "stop scan");
                    bleUtil.scanLeDevice(false, scanCallback);
                    break;
            }
        }
    }

}
