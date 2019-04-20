package com.example.samplecompasstracker.util;

import com.example.samplecompasstracker.model.Coordinates;

import java.text.DecimalFormat;

public class Utils {

    public static String FormatCoordinateText(double coordinate) {
        return new DecimalFormat("#.######").format(coordinate);
    }

    public static Coordinates getCoordinatesFromPoints(String latString, String lngString) {
        double lat = Double.parseDouble(latString);
        double lng = Double.parseDouble(lngString);
        return new Coordinates(lat, lng);
    }
}
