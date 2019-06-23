package com.perfect.freshair.View;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.work.BackoffPolicy;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.perfect.freshair.Common.CommonEnumeration;
import com.perfect.freshair.Common.PermissionEnumeration;
import com.perfect.freshair.Control.DataUpdateWorker;
import com.perfect.freshair.Control.DrawerItemClickListener;
import com.perfect.freshair.Control.NavArrayAdapter;
import com.perfect.freshair.DB.StatusDBHandler;
import com.perfect.freshair.Model.CurrentStatus;
import com.perfect.freshair.Model.Dust;
import com.perfect.freshair.Model.TempData;
import com.perfect.freshair.R;
import com.perfect.freshair.Utils.BlueToothUtils;
import com.perfect.freshair.Utils.MicroDustUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private TextView textDust;
    private TextView textNoValue;
    private TextView textNoDevice;
    private TextView textCoach;
    private ListView mDrawerList;
    private NavArrayAdapter arrayAdapter;
    private StatusDBHandler statusDBHandler;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mNavigationMenu;
    PeriodicWorkRequest saveRequest;
    OneTimeWorkRequest oneTimeWorkRequest;
    LineChart lineChart;
    TempData[] dataList;
    Dust receivedDust;
    private BlueToothUtils blueToothUtils;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(MainActivity.this.toString(), "onReceive");
            receivedDust = new Dust(intent.getIntExtra("pm25", -1), intent.getIntExtra("pm100", -1));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    checkDustDisplay(receivedDust);
                    updateChartData();
                }
            });
        }
    };
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //appcompat toolbar initialization
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.round_menu_24);

        intentFilter = new IntentFilter();
        intentFilter.addAction(CommonEnumeration.dataUpdateAction);

        statusDBHandler = new StatusDBHandler(getApplicationContext());
        blueToothUtils = new BlueToothUtils(getApplicationContext());

        //drawer view
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mNavigationMenu = getResources().getStringArray(R.array.nav_strings);
        arrayAdapter = new NavArrayAdapter(this);
        for (String menu : mNavigationMenu) {
            arrayAdapter.addItem(menu);
        }
        mDrawerList.setAdapter(arrayAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener(this));
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.nav_drawer_open, R.string.nav_drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        //chart view
        lineChart = (LineChart) findViewById(R.id.main_chart);
        lineChart.getLegend().setEnabled(false);
        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);
        lineChart.setDrawGridBackground(false);
        lineChart.setNoDataText("기록이 없습니다.");
        lineChart.setAutoScaleMinMaxEnabled(true);
        XAxis xAxis = lineChart.getXAxis(); // x 축 설정
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //x 축 표시에 대한 위치 설정
        xAxis.setTextColor(Color.BLACK); // X축 텍스트컬러설정
        xAxis.setGridColor(ContextCompat.getColor(this, R.color.colorPrimaryDark)); // X축 줄의 컬러 설정

        YAxis yAxisLeft = lineChart.getAxisLeft(); //Y축의 왼쪽면 설정
        yAxisLeft.setGridLineWidth((float) 1);
        yAxisLeft.setGridColor(getColor(R.color.charYGridColor));
        yAxisLeft.setDrawAxisLine(false);
        yAxisLeft.setTextColor(Color.BLACK); //Y축 텍스트 컬러 설정
        yAxisLeft.setLabelCount(5, true);


        YAxis yAxisRight = lineChart.getAxisRight(); //Y축의 오른쪽면 설정
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setTextColor(Color.BLACK); //Y축 텍스트 컬러 설정
        yAxisRight.setGridLineWidth((float) 1);





        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(DataUpdateWorker.class).setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS).addTag("mywork").build();
        Log.i(toString(), "enqueue");
        WorkManager.getInstance().cancelAllWorkByTag("mywork");
        WorkManager.getInstance().enqueue(oneTimeWorkRequest);


        //dust information
        int tempMajor = 30; // have to get the value from local database
        textDust = (TextView) findViewById(R.id.ac_main_dust_value);
        textNoDevice = (TextView) findViewById(R.id.ac_main_dust_no_device);
        textNoValue = (TextView) findViewById(R.id.ac_main_dust_no_value);
        textCoach = (TextView)findViewById(R.id.ac_main_dust_coach);
        checkDustDisplay();
        updateChartData();
    }

    private String getDustString(int pm2dot5, int pm10) {
        return String.format("현재 초미세먼지 농도는 %d㎍/㎥, 미세먼지 농도는 %d㎍/㎥로 \"%s\"입니다.", pm2dot5, pm10,  MicroDustUtils.parseDustValue(pm2dot5));
    }

    private void updateChartData()
    {
        long eightHouresInMilis = 8*60*1000*60;
        long currentTime = System.currentTimeMillis();
        List<CurrentStatus> data = statusDBHandler.search(currentTime-eightHouresInMilis, currentTime);

        if(data != null)
        {
            List<Entry> pm2Dot5Entries = new ArrayList<Entry>();
            List<Entry> pm10Entries = new ArrayList<Entry>();
            for (CurrentStatus mydata : data) {
                // turn your data into Entry objects
                long oldTime = mydata.getTimestamp();
                float xValue = (float)((oldTime - currentTime)/60/1000);
                Log.i("timestamp", mydata.toString());
                Log.i("timestamp", oldTime-currentTime+"");
                pm10Entries.add(new Entry(xValue,mydata.getDust().getPm100()));
                pm2Dot5Entries.add(new Entry(xValue, mydata.getDust().getPm25()));
            }
            LineDataSet dataSet1 = new LineDataSet(pm2Dot5Entries, "PM2.5");
            dataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
            LineDataSet dataSet2 = new LineDataSet(pm10Entries, "PM10");
            dataSet2.setAxisDependency(YAxis.AxisDependency.RIGHT);
            LineData lineData = new LineData(dataSet1);
            lineData.addDataSet(dataSet2);
            lineData.setValueTextSize(10);
            lineData.setDrawValues(false);
            lineChart.setData(lineData);
            lineChart.invalidate(); // refresh
            textCoach.setText(MicroDustUtils.getCoach(MicroDustUtils.getDustAverage(data)));
        }else
        {
            lineChart.setData(null);
            lineChart.invalidate(); // refresh
        }
    }

    private void checkDustDisplay(Dust dust) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.my_preference_ble_file_key), Context.MODE_PRIVATE);
        String defaultValue = "none";
        String deviceAddr = sharedPreferences.getString(getApplicationContext().getString(R.string.my_preference_ble_addr_key), defaultValue);

        if (!blueToothUtils.getBLEEnabled() || deviceAddr.equals(defaultValue) ) {
            textDust.setVisibility(View.INVISIBLE);
            textNoDevice.setVisibility(View.VISIBLE);
            textNoValue.setVisibility(View.INVISIBLE);
            textCoach.setVisibility(View.INVISIBLE);
        } else {
            textDust.setText(getDustString(dust.getPm25(), dust.getPm100()));
            textDust.setVisibility(View.VISIBLE);
            textCoach.setVisibility(View.VISIBLE);
            textNoDevice.setVisibility(View.INVISIBLE);
            textNoValue.setVisibility(View.INVISIBLE);
        }
    }

    private void checkDustDisplay() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.my_preference_ble_file_key), Context.MODE_PRIVATE);
        String defaultValue = "none";
        String deviceAddr = sharedPreferences.getString(getApplicationContext().getString(R.string.my_preference_ble_addr_key), defaultValue);

        if (!blueToothUtils.getBLEEnabled() || deviceAddr.equals(defaultValue) ) {
            textDust.setVisibility(View.INVISIBLE);
            textNoDevice.setVisibility(View.VISIBLE);
            textNoValue.setVisibility(View.INVISIBLE);
            textCoach.setVisibility(View.INVISIBLE);
        } else {
            CurrentStatus currentStatus = statusDBHandler.latestRow();
            if (currentStatus != null) {
                textDust.setText(getDustString(currentStatus.getDust().getPm25(), currentStatus.getDust().getPm100()));
                textDust.setVisibility(View.VISIBLE);
                textCoach.setVisibility(View.VISIBLE);
                textNoDevice.setVisibility(View.INVISIBLE);
                textNoValue.setVisibility(View.INVISIBLE);
            } else {
                textDust.setVisibility(View.INVISIBLE);
                textNoDevice.setVisibility(View.INVISIBLE);
                textNoValue.setVisibility(View.VISIBLE);
                textCoach.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        mDrawerToggle.syncState();
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //worker
        WorkManager.getInstance().cancelAllWorkByTag("mywork");
        saveRequest =
                new PeriodicWorkRequest.Builder(DataUpdateWorker.class, 20, TimeUnit.MINUTES, 5, TimeUnit.MINUTES).build();
        WorkManager.getInstance().enqueueUniquePeriodicWork(getString(R.string.APP_BACKGROUND_WORKER_TAG), ExistingPeriodicWorkPolicy.KEEP, saveRequest);
    }
}
