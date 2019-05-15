package com.perfect.freshair.View;

import android.content.Context;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.perfect.freshair.R;
import com.perfect.freshair.Utils.PreferencesUtils;

public class SettingActivity extends NavActivity {
    private CheckBox cbGpsRequest;
    private CheckBox cbNetworkRequest;
    private CheckBox cbPassiveRequest;
    private EditText etThresholdAcc;
    private EditText etThresholdNumSate;
    private EditText etThresholdTime;
    private EditText etThresholdTimeout;
    private EditText etIterate;
    private Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        appContext = this.getApplicationContext();

        cbGpsRequest = findViewById(R.id.gps_request);
        cbNetworkRequest = findViewById(R.id.network_request);
        cbPassiveRequest = findViewById(R.id.passive_request);
        etThresholdAcc = findViewById(R.id.threshold_acc);
        etThresholdNumSate = findViewById(R.id.threshold_num_sate);
        etThresholdTime = findViewById(R.id.threshold_elapsed_time);
        etThresholdTimeout = findViewById(R.id.threshold_timeout);
        etIterate = findViewById(R.id.iterate);

        boolean gpsRequest = PreferencesUtils.getGpsRequest(appContext);
        boolean networkRequest = PreferencesUtils.getNetworkRequest(appContext);
        boolean passiveRequest = PreferencesUtils.getPassiveRequest(appContext);
        int thresholdAcc = PreferencesUtils.getThresholdAcc(appContext);
        int thresholdNumSate = PreferencesUtils.getThresholdNumSate(appContext);
        int thresholdTime = PreferencesUtils.getThresholdElapsedTime(appContext);
        int thresholdTimeout = PreferencesUtils.getThresholdTimeout(appContext);
        int iterate = PreferencesUtils.getIterate(appContext);

        cbGpsRequest.setChecked(gpsRequest);
        cbNetworkRequest.setChecked(networkRequest);
        cbPassiveRequest.setChecked(passiveRequest);
        etThresholdAcc.setText(""+thresholdAcc);
        etThresholdNumSate.setText(""+thresholdNumSate);
        etThresholdTime.setText(""+thresholdTime);
        etThresholdTimeout.setText(""+thresholdTimeout);
        etIterate.setText(""+iterate);

        cbGpsRequest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PreferencesUtils.saveGpsRequest(appContext, b);
            }
        });

        cbNetworkRequest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PreferencesUtils.saveNetworkRequest(appContext, b);
            }
        });

        cbPassiveRequest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PreferencesUtils.savePassiveRequest(appContext, b);
            }
        });
    }

    @Override
    protected void onPause() {
        PreferencesUtils.saveThresholdAcc(appContext, Integer.valueOf(etThresholdAcc.getText().toString()));
        PreferencesUtils.saveThresholdNumSate(appContext, Integer.valueOf(etThresholdNumSate.getText().toString()));
        PreferencesUtils.saveThresholdElapsedTime(appContext, Integer.valueOf(etThresholdTime.getText().toString()));
        PreferencesUtils.saveThresholdTimeout(appContext, Integer.valueOf(etThresholdTimeout.getText().toString()));
        PreferencesUtils.saveIterate(appContext, Integer.valueOf(etIterate.getText().toString()));

        super.onPause();
    }
}
