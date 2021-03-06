package com.perfect.freshair.View;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.perfect.freshair.API.DustServerInterface;
import com.perfect.freshair.Callback.ResponseCallback;
import com.perfect.freshair.Callback.ResponsePublicDustCallback;
import com.perfect.freshair.Common.CommonEnumeration;
import com.perfect.freshair.Common.PermissionEnumeration;
import com.perfect.freshair.Control.DataUpdateWorker;
import com.perfect.freshair.Control.DrawerItemClickListener;
import com.perfect.freshair.Control.NavArrayAdapter;
import com.perfect.freshair.Controller.GpsController;
import com.perfect.freshair.DB.MeasurementDBHandler;
import com.perfect.freshair.Model.Address;
import com.perfect.freshair.Model.Measurement;
import com.perfect.freshair.Model.Dust;
import com.perfect.freshair.Model.PublicDust;
import com.perfect.freshair.R;
import com.perfect.freshair.Utils.BlueToothUtils;
import com.perfect.freshair.Utils.MicroDustUtils;
import com.perfect.freshair.Utils.PreferencesUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private TextView textDust;
    private TextView textNoValue;
    private TextView textNoDevice;
    private TextView textCoach;
    private TextView textPublicValue;
    private Button btnRefresh;
    private String strPublicDustValue = "none";

    private ListView mDrawerList;
    private NavArrayAdapter arrayAdapter;
    private MeasurementDBHandler measurementDBHandler;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mNavigationMenu;
    private DustServerInterface serverInterface;
    private GpsController gpsController;
    Thread updateThread;
    PeriodicWorkRequest saveRequest;
    final String myUniqueWorkName = "com.perfect.freshair.ViewoneTimeRequest";
    private boolean tRunning = false;
    LineChart lineChart;
    private BlueToothUtils blueToothUtils;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(MainActivity.this.toString(), "onReceive");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    checkDustDisplay();
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PermissionEnumeration.MY_ACCESS_COARSE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PermissionEnumeration.MY_ACCESS_FINE_LOCATION);
        }

        gpsController = new GpsController(this.getApplicationContext());

        //appcompat toolbar initialization
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.round_menu_24);

        intentFilter = new IntentFilter();
        intentFilter.addAction(CommonEnumeration.dataUpdateAction);

        measurementDBHandler = new MeasurementDBHandler(getApplicationContext());
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
        lineChart.getLegend().setEnabled(true);
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
        //xAxis.setGridColor(ContextCompat.getColor(this, R.color.colorPrimaryDark)); // X축 줄의 컬러 설정

        YAxis yAxisLeft = lineChart.getAxisLeft(); //Y축의 왼쪽면 설정
        yAxisLeft.setGridLineWidth((float) 1);
        yAxisLeft.setGridColor(getColor(R.color.charYGridColor));
        yAxisLeft.setDrawAxisLine(false);
        yAxisLeft.setTextColor(Color.RED); //Y축 텍스트 컬러 설정
        yAxisLeft.setLabelCount(5, true);


        YAxis yAxisRight = lineChart.getAxisRight(); //Y축의 오른쪽면 설정
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setTextColor(Color.BLUE); //Y축 텍스트 컬러 설정
        yAxisRight.setGridLineWidth((float) 1);

        Legend l = lineChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        /*
        updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                tRunning = true;
                while(tRunning)
                {
                    try{
                        Log.i(toString(), "thread is running");
                        Thread.sleep(5000);
                        WorkManager.getInstance().cancelAllWork();
                        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(DataUpdateWorker.class).build();
                        WorkManager.getInstance().enqueueUniqueWork(myUniqueWorkName, ExistingWorkPolicy.KEEP, oneTimeWorkRequest);
                    }catch (Exception e)
                    {
                        Log.e(toString(),e.toString());
                    }
                }
            }
        });

        updateThread.start();
        */

        saveRequest =
                new PeriodicWorkRequest.Builder(DataUpdateWorker.class, 15, TimeUnit.MINUTES, 5, TimeUnit.MINUTES).addTag(myUniqueWorkName).build();
        WorkManager.getInstance().enqueueUniquePeriodicWork(getString(R.string.APP_BACKGROUND_WORKER_TAG), ExistingPeriodicWorkPolicy.KEEP, saveRequest);

        //dust information
        int tempMajor = 30; // have to get the value from local database
        textDust = (TextView) findViewById(R.id.ac_main_dust_value);
        textNoDevice = (TextView) findViewById(R.id.ac_main_dust_no_device);
        textNoValue = (TextView) findViewById(R.id.ac_main_dust_no_value);
        textCoach = (TextView)findViewById(R.id.ac_main_dust_coach);
        textPublicValue = (TextView)findViewById(R.id.ac_main_dust_public_value);
        btnRefresh = (Button)findViewById(R.id.ac_main_dust_refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });

        getPublicData();
        checkDustDisplay();
        updateChartData();
    }

    public void getPublicData()
    {
        Log.i("GPSUtils", "getPublicData");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PermissionEnumeration.MY_ACCESS_COARSE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PermissionEnumeration.MY_ACCESS_FINE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.i("GPSUtils", "requestGPS");
                        gpsController.requestGPS(new com.perfect.freshair.Callback.LocationCallback() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.i("GPSUtils", "onGpsChanged");
                        String res = "latitude : " + location.getLatitude() + " attitude : " + location.getLongitude();

                        if (serverInterface == null)
                            serverInterface = new DustServerInterface();

                        serverInterface.postDust(PreferencesUtils.getUser(getApplicationContext()), new Measurement(System.currentTimeMillis(),
                                new Dust(-1, -1), location), new ResponseCallback() {
                            @Override
                            public void responseCallback(int _resultCode) {

                            }
                        });

                        serverInterface.publicDust(location, new ResponsePublicDustCallback() {
                            @Override
                            public void responsePublicDustCallback(int code, PublicDust publicDust) {
                                if (code == 200) {
                                    strPublicDustValue = getPublicDustString(publicDust);
                                    checkDustDisplay();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),code + " 서버에서 "+publicDust.getAddress().toString()+" 미세먼지 값을 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    private String getDustString(int pm2dot5, int pm10) {
        return String.format("현재 미세먼지 농도는 %d㎍/㎥" + "\r\n" +"초미세먼지 농도는 %d㎍/㎥"+"\r\n"+" \"%s\"입니다.", pm10, pm2dot5,  MicroDustUtils.parsePM10Value(pm10));
    }

    private String getPublicDustString(PublicDust publicDust) {
        int pm100 = publicDust.getDust().getPm100();
        int pm25 = publicDust.getDust().getPm25();

        if (pm25 < 0 && pm100 < 0)
            return String.format("현재 %s\r\n미세먼지 및 초미세먼지 농도 측정값이 없습니다.", publicDust.getAddress().toString());
        else {
            String message = "현재 " +publicDust.getAddress().toString()+ "\n";
            if (pm100 >= 0)
                message += "미세먼지 농도는 " + pm100 + "㎍/㎥으로 " +MicroDustUtils.parsePM10Value(pm100)+ "입니다.\n";
            if (pm25 >= 0)
                message += "초미세먼지 농도는 " + pm25 + "㎍/㎥으로 " +MicroDustUtils.parsePM25Value(pm25)+ "입니다.\n";

            return message;
        }
    }

    private void updateChartData()
    {
        long eightHouresInMilis = 8*60*1000*60;
        long currentTime = System.currentTimeMillis();
        List<Measurement> data = measurementDBHandler.search(currentTime-eightHouresInMilis, currentTime);

        if(data != null)
        {
            List<Entry> pm2Dot5Entries = new ArrayList<Entry>();
            List<Entry> pm10Entries = new ArrayList<Entry>();
            int limitCount = 30;

            for (int i = data.size() - 1; i >= 0 && limitCount-- >= 0; i--) {
                Measurement mydata = data.get(i);
                long oldTime = mydata.getTimestamp();
                float xValue = (float)((currentTime - oldTime) / 1000);
                pm10Entries.add(new Entry(xValue,mydata.getDust().getPm100()));
                pm2Dot5Entries.add(new Entry(xValue, mydata.getDust().getPm25()));
            }

            LineDataSet dataSet1 = new LineDataSet(pm2Dot5Entries, "초미세먼지");
            dataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
            dataSet1.setColor(Color.RED);
            dataSet1.setCircleColor(Color.RED);
            dataSet1.setValueTextColor(Color.RED);
            LineDataSet dataSet2 = new LineDataSet(pm10Entries, "미세먼지");
            dataSet2.setAxisDependency(YAxis.AxisDependency.RIGHT);
            dataSet2.setColor(Color.BLUE);
            dataSet2.setCircleColor(Color.BLUE);
            dataSet2.setValueTextColor(Color.BLUE);
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

    private void refreshData()
    {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.my_preference_ble_file_key), Context.MODE_PRIVATE);
        String defaultValue = "none";
        String deviceAddr = sharedPreferences.getString(getApplicationContext().getString(R.string.my_preference_ble_addr_key), defaultValue);


        //if (true)
        if(blueToothUtils.getBLEEnabled() && !deviceAddr.equals(defaultValue))
        {
            OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(DataUpdateWorker.class).build();
            WorkManager.getInstance().enqueueUniqueWork(myUniqueWorkName, ExistingWorkPolicy.KEEP, oneTimeWorkRequest);
        }else
        {
            getPublicData();
        }
    }

    private void checkDustDisplay() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.my_preference_ble_file_key), Context.MODE_PRIVATE);
        String defaultValue = "none";
        String deviceAddr = sharedPreferences.getString(getApplicationContext().getString(R.string.my_preference_ble_addr_key), defaultValue);

        //if (false) {
        if (!blueToothUtils.getBLEEnabled() || deviceAddr.equals(defaultValue) && strPublicDustValue.equals("none")) {
            textDust.setVisibility(View.INVISIBLE);
            textNoDevice.setVisibility(View.VISIBLE);
            textNoValue.setVisibility(View.INVISIBLE);
            textCoach.setVisibility(View.INVISIBLE);
            textPublicValue.setVisibility(View.INVISIBLE);
        } else {
            Measurement measurement = measurementDBHandler.latestRow();
            if (measurement != null) {
                textDust.setText(getDustString(measurement.getDust().getPm25(), measurement.getDust().getPm100()));
                textDust.setVisibility(View.VISIBLE);
                textCoach.setVisibility(View.VISIBLE);
                textNoDevice.setVisibility(View.INVISIBLE);
                textNoValue.setVisibility(View.INVISIBLE);
                textPublicValue.setVisibility(View.INVISIBLE);
            } else if(!strPublicDustValue.equals("none")){
                textDust.setVisibility(View.INVISIBLE);
                textNoDevice.setVisibility(View.INVISIBLE);
                textNoValue.setVisibility(View.INVISIBLE);
                textCoach.setVisibility(View.INVISIBLE);
                textPublicValue.setVisibility(View.VISIBLE);
                textPublicValue.setText(strPublicDustValue);
            }else
            {
                textDust.setVisibility(View.INVISIBLE);
                textNoDevice.setVisibility(View.INVISIBLE);
                textNoValue.setVisibility(View.VISIBLE);
                textCoach.setVisibility(View.INVISIBLE);
                textPublicValue.setVisibility(View.INVISIBLE);
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
        //WorkManager.getInstance().enqueueUniquePeriodicWork(getString(R.string.APP_BACKGROUND_WORKER_TAG), ExistingPeriodicWorkPolicy.REPLACE, saveRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionEnumeration.MY_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getPublicData();

                } else {
                    Toast.makeText(this, "sorry, this app requires bluetooth feature.", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case PermissionEnumeration.MY_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getPublicData();
                } else {
                    Toast.makeText(this, "sorry, this app requires bluetooth feature.", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
}
