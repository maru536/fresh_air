package com.perfect.freshair.Callback;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;

public class MyBLEScanCallback extends ScanCallback {

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        super.onScanResult(callbackType, result);
    }

    @Override
    public void onScanFailed(int errorCode) {
        super.onScanFailed(errorCode);
    }
}
