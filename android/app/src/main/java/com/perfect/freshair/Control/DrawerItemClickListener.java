package com.perfect.freshair.Control;


import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.perfect.freshair.View.AppInfoActivity;
import com.perfect.freshair.View.DeviceRegisterActivity;
import com.perfect.freshair.View.TestGPSActivity;

public class DrawerItemClickListener implements ListView.OnItemClickListener {

    Activity activity;

    public DrawerItemClickListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(position)
        {
            case 0://디바이스 연결
                activity.startActivity(new Intent(activity.getApplicationContext(), DeviceRegisterActivity.class));
                break;
            case 1://GPS 테스트
                activity.startActivity(new Intent(activity.getApplicationContext(), TestGPSActivity.class));
                break;
            case 2://앱 정보
                activity.startActivity(new Intent(activity.getApplicationContext(), AppInfoActivity.class));
                break;
        }
    }
}

