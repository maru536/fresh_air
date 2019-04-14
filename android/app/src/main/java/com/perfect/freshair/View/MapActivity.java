package com.perfect.freshair.View;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.perfect.freshair.DB.DustGPSDBHandler;
import com.perfect.freshair.Model.DustGPS;
import com.perfect.freshair.R;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MapActivity extends NavActivity implements OnMapReadyCallback {
    public static final long MAX_TIME = 4133948399999L;
    private static final int LOCATION_REQUEST_CODE = 101;
    private static final int LOCATION_COARSE_REQUEST_CODE = 102;
    public static final int MIN_LOCATION_UPDATE_TIME = 1000;
    public static final int MIN_LOCATION_UPDATE_DISTANCE = 10;
    private LocationManager mLocationManager;
    private Spinner mSpinnerGPSType;
    private Button mBtnStartDate;
    private Button mBtnStartTime;
    private Button mBtnEndDate;
    private Button mBtnEndTime;
    private Timestamp mStartTime;
    private Timestamp mEndTime;
    private EditText mEditMinAcc;
    private EditText mEditMaxAcc;
    private Button mBtnSearch;
    private DustGPSDBHandler mLocDB;
    private String TAG = "MapActivity";
    private GoogleMap mMap;
    private float mZoom = 16.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mSpinnerGPSType = findViewById(R.id.select_type);
        mBtnStartDate = findViewById(R.id.start_date);
        mBtnStartTime = findViewById(R.id.start_time);
        mBtnEndDate = findViewById(R.id.end_date);
        mBtnEndTime = findViewById(R.id.end_time);
        mEditMinAcc = findViewById(R.id.min_acc);
        mEditMaxAcc = findViewById(R.id.max_acc);
        mBtnSearch = findViewById(R.id.search);

        mStartTime = new Timestamp(0);
        mEndTime = new Timestamp(MAX_TIME);
        mLocDB = new DustGPSDBHandler(this);

        ArrayList<String> typeList = new ArrayList<>();
        typeList.add(DustGPS.ProviderType.ALL.name());
        typeList.add(DustGPS.ProviderType.GPS.name());
        typeList.add(DustGPS.ProviderType.NETWORK.name());
        typeList.add(DustGPS.ProviderType.FUSED.name());

        ArrayAdapter spinnerAdapter;
        spinnerAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, typeList);
        mSpinnerGPSType.setAdapter(spinnerAdapter);

        mSpinnerGPSType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mBtnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timestamp curTime = new Timestamp(System.currentTimeMillis());
                new DatePickerDialog(
                        MapActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                                mStartTime.setYear(year - 1900);
                                mStartTime.setMonth(month);
                                mStartTime.setDate(date);
                                mBtnStartDate.setText(new SimpleDateFormat("yyyy년 MM월 dd일").format(mStartTime));
                            }
                        },
                        1900 + curTime.getYear(),
                        curTime.getMonth(),
                        curTime.getDate()
                ).show();
            }
        });

        mBtnStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timestamp curTime = new Timestamp(System.currentTimeMillis());
                new TimePickerDialog(
                        MapActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                                mStartTime.setHours(hour);
                                mStartTime.setMinutes(min);
                                mBtnStartTime.setText(new SimpleDateFormat("HH시 mm분").format(mStartTime));
                            }
                        },
                        curTime.getHours(),
                        curTime.getMinutes(),
                        true
                ).show();
            }
        });

        mBtnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timestamp curTime = new Timestamp(System.currentTimeMillis());
                new DatePickerDialog(
                        MapActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                                mEndTime.setYear(year - 1900);
                                mEndTime.setMonth(month);
                                mEndTime.setDate(date);
                                mBtnEndDate.setText(new SimpleDateFormat("yyyy년 MM월 dd일").format(mEndTime));
                            }
                        },
                        1900 + curTime.getYear(),
                        curTime.getMonth(),
                        curTime.getDate()
                ).show();
            }
        });

        mBtnEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timestamp curTime = new Timestamp(System.currentTimeMillis());
                new TimePickerDialog(
                        MapActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                                mEndTime.setHours(hour);
                                mEndTime.setMinutes(min);
                                mBtnEndTime.setText(new SimpleDateFormat("HH시 mm분").format(mEndTime));
                            }
                        },
                        curTime.getHours(),
                        curTime.getMinutes(),
                        true
                ).show();
            }
        });

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int locationColor;
                DustGPS prevData = null;
                String selectedItem = (String) mSpinnerGPSType.getSelectedItem();
                String strMinAcc = mEditMinAcc.getText().toString();
                String strMaxAcc = mEditMaxAcc.getText().toString();
                int minAcc = strMinAcc.length() > 0 ? Integer.valueOf(strMinAcc) : 0;
                int maxAcc = strMaxAcc.length() > 0 ? Integer.valueOf(strMaxAcc) : Integer.MAX_VALUE;


                mMap.clear();
                ArrayList<DustGPS> searchedData = mLocDB.search((String)mSpinnerGPSType.getSelectedItem(),
                                                                    mStartTime, mEndTime, minAcc, maxAcc);

                for (DustGPS currentData : searchedData) {
                    Log.i(TAG, currentData.toString());

                    locationColor = transDustToColor(currentData.getDust());

                    mMap.addCircle(new CircleOptions()
                            .center(currentData.getPosition())
                            .radius(currentData.getCurrentLocation().getAccuracy())
                            .strokeColor(locationColor)
                            .fillColor(locationColor)
                            .clickable(false));

                    if (prevData != null) {
                        mMap.addPolyline(new PolylineOptions()
                                .add(prevData.getPosition(), currentData.getPosition())
                                .color(Color.BLACK)
                                .clickable(false));
                    }

                    prevData = currentData;
                }

                if (searchedData.size() > 0) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(searchedData.get(searchedData.size()-1).getPosition()));
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private int transDustToColor(int dust) {
        if (dust < 50)
            return getResources().getColor(R.color.transparentBlue, null);
        else if (dust < 100)
            return getResources().getColor(R.color.transparentGreen, null);
        else if (dust < 150)
            return getResources().getColor(R.color.transparentYello, null);
        else
            return getResources().getColor(R.color.transparentRed, null);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /*
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */

        if (mMap != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(false);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(mZoom));
        }
    }

    protected void requestPermission(String permissionType, int requestCode) {
        int permission = ContextCompat.checkSelfPermission(this, permissionType);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permissionType}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length == 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Unable to show location - permission required", Toast.LENGTH_LONG).show();
                }
                return;
            }

            case LOCATION_COARSE_REQUEST_CODE: {
                if (grantResults.length == 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Unable to show  coarse location - permission required", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
