package com.perfect.freshair.View;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
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
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.perfect.freshair.API.GPSServerInterface;
import com.perfect.freshair.Callback.ResponseCallback;
import com.perfect.freshair.Callback.ResponseDustCallback;
import com.perfect.freshair.Common.CommonEnumeration;
import com.perfect.freshair.Common.PermissionEnumeration;
import com.perfect.freshair.Control.DataUpdateWorker;
import com.perfect.freshair.Control.DrawerItemClickListener;
import com.perfect.freshair.Control.NavArrayAdapter;
import com.perfect.freshair.DB.StatusDBHandler;
import com.perfect.freshair.Model.CurrentStatus;
import com.perfect.freshair.Model.Dust;
import com.perfect.freshair.Model.Gps;
import com.perfect.freshair.Model.GpsProvider;
import com.perfect.freshair.Model.LatestDust;
import com.perfect.freshair.Model.Position;
import com.perfect.freshair.Model.PositionStatus;
import com.perfect.freshair.Model.Satellite;
import com.perfect.freshair.Model.TempData;
import com.perfect.freshair.R;
import com.perfect.freshair.Utils.BlueToothUtils;
import com.perfect.freshair.Utils.MicroDustUtils;
import com.perfect.freshair.Utils.MyBLEPacketUtilis;
import com.perfect.freshair.Utils.PreferencesUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
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
    private StatusDBHandler statusDBHandler;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mNavigationMenu;
    private GPSServerInterface serverInterface;
    PeriodicWorkRequest saveRequest;
    Thread updateThread;
    final String myUniqueWorkName = "com.perfect.freshair.ViewoneTimeRequest";
    private boolean tRunning = false;
    LineChart lineChart;
    TempData[] dataList;
    Dust receivedDust;
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

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PermissionEnumeration.MY_ACCESS_COARSE_LOCATION);
        }



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

        updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                tRunning = true;
                while(tRunning)
                {
                    try{
                        Log.i(toString(), "thread is running");
                        Thread.sleep(5000);
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

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }

            if(fusedLocationClient == null)
            {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
            }

            fusedLocationClient.removeLocationUpdates(locationCallback);

            for (Location location : locationResult.getLocations()) {
                // Update UI with location data
                // ...
                if (location != null) {
                    // Logic to handle location object
                    String res = "latitude : " + location.getLatitude() + " attitude : " + location.getLongitude();
                    final List<Address> list = getCurrentAddress(location.getLatitude(), location.getLongitude());
                    if(list != null && list.size() > 0)
                    {
                        Log.i(getApplicationContext().toString(), list.get(0).getAdminArea()+" ,"+ list.get(0).getLocality() + " ," + list.get(0).getAddressLine(0).toString());
                        if (serverInterface == null)
                            serverInterface = new GPSServerInterface();

                        final ArrayList<String> sidogun = new ArrayList<String>();

                        if(list.get(0).getAdminArea() != null)
                        {
                            sidogun.add(list.get(0).getAdminArea());
                        }else
                        {
                            sidogun.add(list.get(0).getSubAdminArea());
                        }

                        if(list.get(0).getLocality() != null)
                        {
                            sidogun.add(list.get(0).getLocality());
                        }else
                        {
                            sidogun.add(list.get(0).getSubLocality());
                        }

                        serverInterface.publicDust(sidogun.get(0), sidogun.get(1), new ResponseDustCallback() {
                            @Override
                            public void responseDustCallback(int code, Dust dust) {
                                String addr = sidogun.get(0) + " " +sidogun.get(1);
                                if(code == 404)
                                {
                                    Toast.makeText(getApplicationContext(),code + " 서버에서 "+addr+" 미세먼지 값을 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                                }
                                if(dust != null)
                                {

                                    Log.i("publicDustApi", "PM10: " +dust.getPm100()+ " / PM2.5: " +dust.getPm25());
                                    strPublicDustValue = getPublicDustString(addr, dust.getPm100(), dust.getPm25());
                                    checkDustDisplay();
                                }

                            }
                        });
                    }
                }else
                {
                    Log.e(toString(), "loaction is null");
                }
            }
        };
    };

    public void getPublicData()
    {
        Log.i(toString(), "refresh");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PermissionEnumeration.MY_ACCESS_COARSE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PermissionEnumeration.MY_ACCESS_FINE_LOCATION);
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(fusedLocationClient == null)
        {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

        /*fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            String res = "latitude : " + location.getLatitude() + " attitude : " + location.getLongitude();
                            final List<Address> list = getCurrentAddress(location.getLatitude(), location.getLongitude());
                            if(list != null && list.size() > 0)
                            {
                                Log.i(getApplicationContext().toString(), list.get(0).getAdminArea()+" ,"+ list.get(0).getLocality() + " ," + list.get(0).getAddressLine(0).toString());
                                if (serverInterface == null)
                                    serverInterface = new GPSServerInterface();

                                final ArrayList<String> sidogun = new ArrayList<String>();

                                if(list.get(0).getAdminArea() != null)
                                {
                                    sidogun.add(list.get(0).getAdminArea());
                                }else
                                {
                                    sidogun.add(list.get(0).getSubAdminArea());
                                }

                                if(list.get(0).getLocality() != null)
                                {
                                    sidogun.add(list.get(0).getLocality());
                                }else
                                {
                                    sidogun.add(list.get(0).getSubLocality());
                                }

                                serverInterface.publicDust(sidogun.get(0), sidogun.get(1), new ResponseDustCallback() {
                                    @Override
                                    public void responseDustCallback(int code, Dust dust) {
                                        String addr = sidogun.get(0) + " " +sidogun.get(1);
                                        if(code == 404)
                                        {
                                            Toast.makeText(getApplicationContext(),code + " 서버에서 "+addr+" 미세먼지 값을 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                        if(dust != null)
                                        {

                                            Log.i("publicDustApi", "PM10: " +dust.getPm100()+ " / PM2.5: " +dust.getPm25());
                                            strPublicDustValue = getPublicDustString(addr, dust.getPm100(), dust.getPm25());
                                            checkDustDisplay();
                                        }

                                    }
                                });
                            }
                        }else
                        {
                            Log.e(toString(), "loaction is null");
                        }
                    }
                });*/
    }

    private String getDustString(int pm2dot5, int pm10) {
        return String.format("현재 미세먼지 농도는 %d㎍/㎥" + "\r\n" +"초미세먼지 농도는 %d㎍/㎥"+"\r\n"+" \"%s\"입니다.", pm10, pm2dot5,  MicroDustUtils.parseDustValue(pm10));
    }

    private String getPublicDustString(String addr, int pm2dot5, int pm10) {
        return String.format("현재 %s\r\n미세먼지 농도는 %d㎍/㎥" + "\r\n" +"초미세먼지 농도는 %d㎍/㎥"+"\r\n"+" \"%s\"입니다.", addr, pm10, pm2dot5,  MicroDustUtils.parseDustValue(pm10));
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
            int limitCount = 30;

            for (int i = data.size() - 1; i >= 0 && limitCount-- >= 0; i--) {
                CurrentStatus mydata = data.get(i);
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

        if (!blueToothUtils.getBLEEnabled() || deviceAddr.equals(defaultValue) && strPublicDustValue.equals("none")) {
            textDust.setVisibility(View.INVISIBLE);
            textNoDevice.setVisibility(View.VISIBLE);
            textNoValue.setVisibility(View.INVISIBLE);
            textCoach.setVisibility(View.INVISIBLE);
            textPublicValue.setVisibility(View.INVISIBLE);
        } else {
            CurrentStatus currentStatus = statusDBHandler.latestRow();
            if (currentStatus != null) {
                textDust.setText(getDustString(currentStatus.getDust().getPm25(), currentStatus.getDust().getPm100()));
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
        //worker
        WorkManager.getInstance().cancelUniqueWork(myUniqueWorkName);
        tRunning = false;
        saveRequest =
                new PeriodicWorkRequest.Builder(DataUpdateWorker.class, 10, TimeUnit.SECONDS, 10, TimeUnit.SECONDS).build();
        WorkManager.getInstance().enqueueUniquePeriodicWork(getString(R.string.APP_BACKGROUND_WORKER_TAG), ExistingPeriodicWorkPolicy.KEEP, saveRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionEnumeration.MY_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
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
                } else {
                    Toast.makeText(this, "sorry, this app requires bluetooth feature.", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    public List<Address> getCurrentAddress( double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        boolean res = true;
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            return null;
        } catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }

        if (addresses == null || addresses.size() == 0) {
            return null;
        }

        return addresses;
    }
}
