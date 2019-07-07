package com.perfect.freshair.Utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.perfect.freshair.Model.Measurement;
import com.perfect.freshair.Model.RepresentMeasurement;

import java.util.ArrayList;
import java.util.List;

public class MeasurementUtils {
    public static final float includingArea = 100.0f;

    public static List<RepresentMeasurement> representMeasurement(List<Measurement> allMeasurement) {
        List<RepresentMeasurement> representMeasurementList = new ArrayList<>();
        if (allMeasurement != null) {
            for (Measurement exploreMeasurement : allMeasurement) {
                if (representMeasurementList.size() == 0)
                    representMeasurementList.add(new RepresentMeasurement());
                else {
                    if (!isIncludedInRepresentMeasurement(exploreMeasurement.getLocation(), representMeasurementList.get(representMeasurementList.size()-1))) {
                        int includingRepresentMeasurementIndex = indexOfIncludedInRepresentMeasurement(exploreMeasurement.getLocation(), representMeasurementList);
                        if (includingRepresentMeasurementIndex >= 0) {
                            RepresentMeasurement includingRepresentMeasurement = representMeasurementList.get(includingRepresentMeasurementIndex);
                            representMeasurementList.remove(includingRepresentMeasurement);
                            representMeasurementList.add(includingRepresentMeasurement);
                        }
                        else
                            representMeasurementList.add(new RepresentMeasurement());
                    }
                }

                representMeasurementList.get(representMeasurementList.size()-1).addMeasurement(exploreMeasurement);
            }
        }

        return representMeasurementList;
    }

    private static boolean isIncludedInRepresentMeasurement(Location exploreLocation, RepresentMeasurement representMeasurement) {
        Location representLocation = new Location("Temp");
        representLocation.setLatitude(representMeasurement.getCenterPosition().latitude);
        representLocation.setLongitude(representMeasurement.getCenterPosition().longitude);
        float distance = representLocation.distanceTo(exploreLocation);
        return distance < includingArea;
    }

    public static int indexOfIncludedInRepresentMeasurement(Location exploreLocation, List<RepresentMeasurement> representMeasurementList) {
        for (int index = 0; index < representMeasurementList.size(); index++) {
            RepresentMeasurement exploreRepresentMeasurement = representMeasurementList.get(index);
            if (isIncludedInRepresentMeasurement(exploreLocation, exploreRepresentMeasurement))
                return index;
        }

        return -1;
    }
}
