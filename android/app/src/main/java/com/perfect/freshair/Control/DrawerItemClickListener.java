package com.perfect.freshair.Control;


import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.perfect.freshair.Utils.PreferencesUtils;
import com.perfect.freshair.View.TodayMapActivity;
import com.perfect.freshair.View.DeviceRegisterActivity;
import com.perfect.freshair.View.SignInActivity;
import com.perfect.freshair.View.YesterdayMapActivity;

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
            case 1://로그아웃
                PreferencesUtils.clearUser(activity.getApplicationContext());
                activity.finish();
                activity.startActivity(new Intent(activity.getApplicationContext(), SignInActivity.class));
                break;
            case 2://오늘 미세지도
                activity.startActivity(new Intent(activity.getApplicationContext(), TodayMapActivity.class));
                break;
            case 3://어제 미세지도
                activity.startActivity(new Intent(activity.getApplicationContext(), YesterdayMapActivity.class));
                break;
        }
    }
}

