package com.example.samplecompasstracker;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class Utils {

    public static String FormatCoordinateText(double coordinate) {
        return new DecimalFormat("#.####").format(coordinate);
    }

    public static Coordinates getCoordinatesFromPoints(String latString, String lngString) {
        double lat = Double.parseDouble(latString);
        double lng = Double.parseDouble(lngString);
        return new Coordinates(lat, lng);
    }

    public static boolean validLatCoordinate(String lat) {
        double latCoordinate = Double.parseDouble(lat);
        return (latCoordinate < -90.0 || latCoordinate > 90.0);
    }
    public static boolean validLngCoordinate(String lng) {
        double lngCoordinate = Double.parseDouble(lng);
        return (lngCoordinate < -180.0 || lngCoordinate > 180.0);
    }

}
