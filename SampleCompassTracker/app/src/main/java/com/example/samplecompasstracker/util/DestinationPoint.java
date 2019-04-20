package com.example.samplecompasstracker.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.constraint.ConstraintLayout;

import com.example.samplecompasstracker.R;
import com.example.samplecompasstracker.model.Coordinates;

public class DestinationPoint {

    public static boolean checkIfGeoAreTheSame(Coordinates user, Coordinates destination) {
        return  ((user.getLatitude() == destination.getLatitude())
                && (user.getLongitude() == destination.getLongitude()));
    }

    public static float getBearing(Coordinates user, Coordinates destination) {
        double userLatRadians = Math.toRadians(user.getLatitude());
        double userLngRadians = Math.toRadians(user.getLongitude());
        double destinationLatRadians = Math.toRadians(destination.getLatitude());
        double destinationLngRadians = Math.toRadians(destination.getLongitude());

        double lng = destinationLngRadians - userLngRadians;

        double y = Math.sin(lng) * Math.cos(destinationLatRadians);
        double x = Math.cos(userLatRadians) * Math.sin(destinationLatRadians)
                - Math.sin(userLatRadians) * Math.cos(destinationLatRadians) * Math.cos(lng);

        double bearing = Math.atan2(y, x);

        return mod((float) Math.toDegrees(bearing), 360.0f);
    }

    private static float mod(float a, float b) {
        return (a % b + b) % b;
    }

    public static void sameCoordinatesDialog(Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.coordinates_dialog_title))
                .setMessage(activity.getString(R.string.coordinates_dialog_message))
                .setPositiveButton(activity.getString(R.string.coordinates_dialog_ok_bt),
                        (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}
