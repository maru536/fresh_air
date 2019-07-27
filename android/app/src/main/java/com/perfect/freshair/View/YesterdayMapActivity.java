package com.perfect.freshair.View;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.perfect.freshair.API.DustServerInterface;
import com.perfect.freshair.Callback.ResponseDustMapCallback;
import com.perfect.freshair.Model.RepresentDustWithLocation;
import com.perfect.freshair.R;
import com.perfect.freshair.Utils.JsonUtils;
import com.perfect.freshair.Utils.MeasurementUtils;
import com.perfect.freshair.Utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

public class YesterdayMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    List<RepresentDustWithLocation> mAllRepresentDustWithLocationList;
    private float mZoom = 16.0f;
    DustServerInterface serverInterface = new DustServerInterface();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_map);

        //toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getStringArray(R.array.nav_strings)[3]);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
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


            mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
                @Override
                public void onCircleClick(Circle circle) {
                    Location circleLocation = new Location("temp");
                    circleLocation.setLatitude(circle.getCenter().latitude);
                    circleLocation.setLongitude(circle.getCenter().longitude);
                    int includingIndex = MeasurementUtils.indexOfIncludedInRepresentDustWithLocation(circle.getCenter(), mAllRepresentDustWithLocationList);

                    if (includingIndex > 0) {
                        addLine(mAllRepresentDustWithLocationList.get(includingIndex - 1).getPosition(), mAllRepresentDustWithLocationList.get(includingIndex).getPosition());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mAllRepresentDustWithLocationList.get(includingIndex - 1).getPosition(), mZoom));
                    }
                }
            });

            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(37.5281761, 127.0275929)));

            serverInterface.yesterdayDustMap(PreferencesUtils.getUser(getApplicationContext()), new ResponseDustMapCallback() {
                @Override
                public void onResponse(int code, String message, JsonArray representDustWithLocationList) {
                    if (representDustWithLocationList != null) {
                        mAllRepresentDustWithLocationList = new ArrayList<>();
                        for (JsonElement exploreElem : representDustWithLocationList) {
                            JsonObject representDustWithLocation = JsonUtils.getAsJsonObject(exploreElem);
                            mAllRepresentDustWithLocationList.add(new RepresentDustWithLocation(representDustWithLocation));
                        }

                        LatLng previousPositon = null;
                        for (RepresentDustWithLocation representDustWithLocation : mAllRepresentDustWithLocationList) {
                            addCircle(representDustWithLocation);

                            if (previousPositon != null)
                                addLine(previousPositon, representDustWithLocation.getPosition());
                            previousPositon = representDustWithLocation.getPosition();
                        }

                        if (mAllRepresentDustWithLocationList.size() > 0) {
                            RepresentDustWithLocation latestDustWithLocation = mAllRepresentDustWithLocationList.get(mAllRepresentDustWithLocationList.size() - 1);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latestDustWithLocation.getPosition(), mZoom));
                        }
                    }
                    else
                        Toast.makeText(getApplicationContext(), "미세먼지 데이터가 없습니다.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void addCircle(RepresentDustWithLocation representDustWithLocation) {
        int circleColor = transDustToColor(representDustWithLocation.getDust().getPm100());

        mMap.addCircle(new CircleOptions()
                .center(representDustWithLocation.getPosition())
                .radius(MeasurementUtils.includingArea)
                .strokeColor(circleColor)
                .fillColor(circleColor)
                .clickable(true));
    }

    private void addLine(LatLng start, LatLng end) {
        mMap.addPolyline(new PolylineOptions().add(start, end).color(getResources().getColor(R.color.transparentBlack, null)));
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