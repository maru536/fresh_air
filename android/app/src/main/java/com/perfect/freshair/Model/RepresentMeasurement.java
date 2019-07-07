package com.perfect.freshair.Model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class RepresentMeasurement {
    List<Measurement> measurementList;
    LatLng centerPosition;
    int sumPm100 = 0;
    int sumPm25 = 0;
    long lastTime = -1L;

    public RepresentMeasurement() {
        measurementList = new ArrayList<>();
        centerPosition = new LatLng(0.0, 0.0);
    }

    public void addMeasurement(Measurement newMeasurement) {
        double sumLatitude = centerPosition.latitude*measurementList.size() + newMeasurement.getLocation().getLatitude();
        double sumLongitude = centerPosition.longitude*measurementList.size() + newMeasurement.getLocation().getLongitude();
        measurementList.add(newMeasurement);
        centerPosition = new LatLng(sumLatitude / measurementList.size(), sumLongitude / measurementList.size());
        lastTime = newMeasurement.getTimestamp();
        sumPm100 += newMeasurement.getDust().getPm100();
        sumPm25 += newMeasurement.getDust().getPm25();
    }

    public List<Measurement> getMeasurementList() {
        return measurementList;
    }

    public LatLng getCenterPosition() {
        return centerPosition;
    }

    public int getAveragePm100() {
        return sumPm100 / measurementList.size();
    }

    public int getAveragePm25() {
        return sumPm25 / measurementList.size();
    }
}
