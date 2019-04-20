package com.example.samplecompasstracker.listener;

import android.location.Location;

public interface FusedLocationListener {
    void setFusedLocation(Location location);
    void setUnknownLocation();
    void onNewLocation(Location location);
}
