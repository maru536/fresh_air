package com.perfect.freshair.View;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.perfect.freshair.Control.DrawerItemClickListener;
import com.perfect.freshair.Control.NavArrayAdapter;
import com.perfect.freshair.DB.MeasurementDBHandler;
import com.perfect.freshair.Model.Measurement;
import com.perfect.freshair.R;

import java.util.List;

public class AppInfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private NavArrayAdapter arrayAdapter;
    private MeasurementDBHandler measurementDBHandler;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mNavigationMenu;
    private GoogleMap mMap;
    private float mZoom = 16.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.round_menu_24);

        measurementDBHandler = new MeasurementDBHandler(getApplicationContext());

        //drawer view
        mDrawerLayout = (DrawerLayout) findViewById(R.id.map_layout);
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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
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

            mMap.clear();
            List<Measurement> allMeasurement = this.measurementDBHandler.searchAll();
            LatLng previousPositon = null;

            for (Measurement currentMeasurement : allMeasurement) {
                LatLng currentPosition = new LatLng(currentMeasurement.getGps().getLatitude(), currentMeasurement.getGps().getLongitude());
                addCircle(currentMeasurement);

                if (previousPositon != null)
                    addLine(previousPositon, currentPosition);
                previousPositon = currentPosition;
            }

            if (allMeasurement.size() > 0) {
                Measurement latestMeasurement = allMeasurement.get(allMeasurement.size()-1);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latestMeasurement.getGps().getLatitude(), latestMeasurement.getGps().getLongitude()), mZoom));
            }
        }
    }

    private void addCircle(Measurement measurement) {
        int circleColor = transDustToColor(measurement.getDust().getPm100());

        mMap.addCircle(new CircleOptions()
            .center(new LatLng(measurement.getGps().getLatitude(), measurement.getGps().getLongitude()))
            .radius(measurement.getGps().getAccuracy())
            .strokeColor(circleColor)
            .fillColor(circleColor)
            .clickable(false));
    }

    private void addLine(LatLng start, LatLng end) {
        mMap.addPolyline(new PolylineOptions().add(start, end).color(Color.BLACK));
    }

    private int transDustToColor(int _dust) {
        if (_dust < 50)
            return getResources().getColor(R.color.transparentBlue, null);
        else if (_dust < 100)
            return getResources().getColor(R.color.transparentGreen, null);
        else if (_dust < 150)
            return getResources().getColor(R.color.transparentYello, null);
        else
            return getResources().getColor(R.color.transparentRed, null);
    }
}
