package com.perfect.freshair.View;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.work.BackoffPolicy;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.perfect.freshair.Common.PermissionEnumeration;
import com.perfect.freshair.Control.DataUpdateWorker;
import com.perfect.freshair.Control.DrawerItemClickListener;
import com.perfect.freshair.Control.NavArrayAdapter;
import com.perfect.freshair.DB.StatusDBHandler;
import com.perfect.freshair.Model.CurrentStatus;
import com.perfect.freshair.Model.TempData;
import com.perfect.freshair.R;
import com.perfect.freshair.Utils.BlueToothUtils;
import com.perfect.freshair.Utils.MicroDustUtils;
import com.perfect.freshair.Utils.MyBLEPacketUtilis;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private TextView textDust;
    private TextView textNoValue;
    private TextView textNoDevice;
    private ListView mDrawerList;
    private NavArrayAdapter arrayAdapter;
    private StatusDBHandler statusDBHandler;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mNavigationMenu;
    LineChart lineChart;
    TempData[] dataList;
    private BlueToothUtils blueToothUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //appcompat toolbar initialization
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.round_menu_24);

        //permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PermissionEnumeration.MY_ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PermissionEnumeration.MY_ACCESS_FINE_LOCATION);
        }

        statusDBHandler = new StatusDBHandler(getApplicationContext());
        blueToothUtils = new BlueToothUtils(getApplicationContext());

        //drawer view
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mNavigationMenu = getResources().getStringArray(R.array.nav_strings);
        arrayAdapter = new NavArrayAdapter(this);
        for(String menu : mNavigationMenu)
        {
            arrayAdapter.addItem(menu);
        }
        mDrawerList.setAdapter(arrayAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener(this));
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.nav_drawer_open,R.string.nav_drawer_close ) {
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
        XAxis xAxis = lineChart.getXAxis(); // x 축 설정
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //x 축 표시에 대한 위치 설정
        xAxis.setLabelCount(24, true); //X축의 데이터를 최대 몇개 까지 나타낼지에 대한 설정 5개 force가 true 이면 반드시 보여줌
        xAxis.setTextColor(Color.BLACK); // X축 텍스트컬러설정
        xAxis.setGridColor(ContextCompat.getColor(this, R.color.colorPrimaryDark)); // X축 줄의 컬러 설정

        YAxis yAxisLeft = lineChart.getAxisLeft(); //Y축의 왼쪽면 설정
        yAxisLeft.setGridLineWidth((float)1);
        yAxisLeft.setGridColor(getColor(R.color.charYGridColor));
        yAxisLeft.setDrawAxisLine(false);
        yAxisLeft.setTextColor(Color.BLACK); //Y축 텍스트 컬러 설정
        yAxisLeft.setLabelCount(5, true);

        YAxis yAxisRight = lineChart.getAxisRight(); //Y축의 오른쪽면 설정
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);

        dataList = new TempData[24];
        Random random = new Random();
        for(int i=0;i<dataList.length;i++)
        {
            dataList[i] = new TempData(i, random.nextInt(100));
        }

        List<Entry> entries = new ArrayList<Entry>();
        for (TempData mydata : dataList) {
            // turn your data into Entry objects
            entries.add(new Entry(mydata.getX(), mydata.getY()));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Label");
        LineData lineData = new LineData(dataSet);
        lineData.setValueTextSize(10);
        lineChart.setData(lineData);
        lineChart.invalidate(); // refresh

        //worker
        PeriodicWorkRequest saveRequest =
                new PeriodicWorkRequest.Builder(DataUpdateWorker.class, 20, TimeUnit.MINUTES, 5, TimeUnit.MINUTES).build();
        WorkManager.getInstance().enqueueUniquePeriodicWork(DataUpdateWorker.class.getName(),ExistingPeriodicWorkPolicy.KEEP,saveRequest);
        //WorkManager.getInstance().cancelUniqueWork(DataUpdateWorker.class.getName());

        //dust information
        int tempMajor = 30; // have to get the value from local database
        textDust = (TextView)findViewById(R.id.ac_main_dust_value);
        textNoDevice = (TextView)findViewById(R.id.ac_main_dust_no_device);
        textNoValue = (TextView)findViewById(R.id.ac_main_dust_no_value);
        checkDustDisplay();


    }

    private String getDustString(int value)
    {
        return String.format("현재 미세먼지 농도는 %d㎍/㎥로 \"%s\"입니다.", value, MicroDustUtils.parseDustValue(value));
    }

    private void checkDustDisplay()
    {
        if(!blueToothUtils.getBLEEnabled())
        {
            textDust.setVisibility(View.INVISIBLE);
            textNoDevice.setVisibility(View.VISIBLE);
            textNoValue.setVisibility(View.INVISIBLE);
        }
        else
        {
            CurrentStatus currentStatus = statusDBHandler.latestRow();
            if(currentStatus!=null)
            {
                textDust.setText(getDustString(currentStatus.getDust().getPm25()));
                textDust.setVisibility(View.VISIBLE);
                textNoDevice.setVisibility(View.INVISIBLE);
                textNoValue.setVisibility(View.INVISIBLE);
            }else
            {
                textDust.setVisibility(View.INVISIBLE);
                textNoDevice.setVisibility(View.INVISIBLE);
                textNoValue.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        mDrawerToggle.syncState();
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionEnumeration.MY_ACCESS_COARSE_LOCATION : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Toast.makeText(this, "sorry, this app requires bluetooth feature.",Toast.LENGTH_SHORT).show();
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case PermissionEnumeration.MY_ACCESS_FINE_LOCATION : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Toast.makeText(this, "sorry, this app requires bluetooth feature.",Toast.LENGTH_SHORT).show();
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
}
