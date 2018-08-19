package com.perfect.freshair.View;

import android.content.Intent;
import android.os.Bundle;

import com.perfect.freshair.R;
import com.perfect.freshair.Service.GPSService;

public class MainActivity extends NavActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, GPSService.class);
        startService(intent);
    }
}
