package com.example.samplecompasstracker;

import android.location.Location;

public interface FusedLocationListener {
    void setFusedLocation(Location location);
    void setUnknowLocation();
    void onNewLocation(Location location);
}
