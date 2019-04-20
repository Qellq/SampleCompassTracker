package com.example.samplecompasstracker;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class LocationPermissionManager {

    public static final int PERMISSIONS_REQUEST = 9128;

    public static boolean checkLocationPermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void checkRequestLocationPermissions(Activity activity) {
        boolean shouldShowRequestRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (shouldShowRequestRationale) {
            showPermissionDialog(activity);
        } else {
            requestPermissions(activity);
        }
    }

    private static void requestPermissions(Activity activity) {
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(activity, permissions, PERMISSIONS_REQUEST);
    }

    private static void showPermissionDialog(final Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("Required Location Permission")
                .setMessage("You have to grant location permission to set your target")
                .setPositiveButton("Allow",
                        (dialog, which) -> requestPermissions(activity))
                .setNegativeButton("No, Thanks",
                        (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}
