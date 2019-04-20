package com.example.samplecompasstracker.util;

import android.app.Activity;
import android.support.design.widget.TextInputLayout;

import com.example.samplecompasstracker.model.Coordinates;
import com.example.samplecompasstracker.permission.LocationPermissionManager;

public class ValidUtils {

    private static boolean validCoordinates(TextInputLayout inputLatLayout, TextInputLayout inputLngLayout) {
        boolean isCoordsCorrect = true;
        String lat = inputLatLayout.getEditText().getText().toString();
        String lng = inputLngLayout.getEditText().getText().toString();


        if (!lat.isEmpty()) {
            double doubleLat = Double.parseDouble(lat);
            if ((doubleLat < -90.0 || doubleLat > 90.0)) {
                inputLatLayout.setError("Choose from -90 to 90");
                isCoordsCorrect = false;
            } else {
                inputLatLayout.setError(null);
            }
        } else {
            isCoordsCorrect = false;
            inputLatLayout.setError("Latitude cannot be empty");
        }
        if (!lng.isEmpty()) {
            double doubleLng = Double.parseDouble(lng);
            if ((doubleLng < -180.0 || doubleLng > 180.0)) {
                inputLngLayout.setError("Choose from -180 to 180");
                isCoordsCorrect = false;
            } else {
                inputLngLayout.setError(null);
            }
        } else {
            isCoordsCorrect = false;
            inputLngLayout.setError("Longitude cannot be empty");
        }

        return isCoordsCorrect;
    }

    public static boolean startValid(Activity activity, TextInputLayout latitudeTextLayout, TextInputLayout longitudeTextLayout, Coordinates userLocation, Coordinates destinationLocation) {
        if (!LocationPermissionManager.checkLocationPermissions(activity)) {
            LocationPermissionManager.checkRequestLocationPermissions(activity);
            return false;
        }
        if (!ValidUtils.validCoordinates(latitudeTextLayout, longitudeTextLayout)) {
            return false;
        }
        if (DestinationPoint.checkIfGeoAreTheSame(userLocation, destinationLocation)) {
            DestinationPoint.sameCoordinatesDialog(activity);
            return false;
        }
        return true;
    }



}
