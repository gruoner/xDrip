package com.eveningoutpost.dexdrip.eassist;

import static java.lang.Math.max;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.eveningoutpost.dexdrip.Home;
import com.eveningoutpost.dexdrip.models.JoH;
import com.eveningoutpost.dexdrip.models.UserError;
import com.eveningoutpost.dexdrip.xdrip;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

// jamorham

// Do our best to get the most accurate location and geocode we can for emergency message feature

// Using old (9.x) apis of play services due to legacy compatibility

public class GetLocationByLM {

    private static final String TAG = GetLocationByLM.class.getSimpleName();


    private static LocationManager locationManager;
    private static volatile Location lastGPSLocation;
    private static volatile Location lastNetworkLocation;
    private static volatile Location lastPassiveLocation;
    private static volatile Location lastBestLocation;
    private static long GPSAddressUpdated = 0;
    private static long NetworkAddressUpdated = 0;
    private static long PassiveAddressUpdated = 0;

    // TODO this can be centralized
    public final static int MY_PERMISSIONS_REQUEST_GPS = 204;

    private static boolean checkLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(xdrip.getAppContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(xdrip.getAppContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                UserError.Log.d(TAG, "i don't have got permission for location");
                return false;
            }
        }
        return true;
    }
    @SuppressLint("MissingPermission")
    public static void prepareForLocation() {
        // turn on wifi? gps? bluetooth?
        UserError.Log.d(TAG, "prepareing location");
        JoH.setBluetoothEnabled(xdrip.getAppContext(), true);
        locationManager = (LocationManager) xdrip.getAppContext().getSystemService(Context.LOCATION_SERVICE);
        if (!checkLocationPermissions()) {
            UserError.Log.wtf(TAG, "No permission to obtain location");
            return;
        }
        UserError.Log.d(TAG, "Requesting live location updates");
        if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 60 * 1000, 1,
                    GPSlocationListener);
        }
        if (locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 60 * 1000, 1,
                    NetworkLocationListener);
        }
        if (locationManager.isProviderEnabled(locationManager.PASSIVE_PROVIDER)) {
            locationManager.requestLocationUpdates(locationManager.PASSIVE_PROVIDER, 60 * 1000, 1,
                    PassiveLocationListener);
        }
    }


    public static String getBestLocation() {

        if (lastBestLocation == null) {
            return "Location unknown!";
        }

        return "GPS: " + lastBestLocation.getLatitude() + "," + lastBestLocation.getLongitude() + accuracyAddendum();
    }

    private static String accuracyAddendum() {
        return lastBestLocation.hasAccuracy() ? " (+/- " + JoH.qs(lastBestLocation.getAccuracy(), 0) + "m)" : "";
    }

    public static String getMapUrl() {
        if (lastBestLocation == null) return "";
        return "https://maps.google.de/?q=" + lastBestLocation.getLatitude() + "," + lastBestLocation.getLongitude();
    }

    public synchronized static void getLocation() {
        UserError.Log.d(TAG, "fetching location for device status");
        if (!checkLocationPermissions()) {
            UserError.Log.wtf(TAG, "No permission to obtain location");
            return;
        }

        if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
            @SuppressLint("MissingPermission") final Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            if (location == null)
                UserError.Log.d(TAG, "GPS location is null!!");
            else {
                UserError.Log.d(TAG, "Got GPS location " + location2String(location));
                if ((lastGPSLocation != null) && (location.distanceTo(lastGPSLocation) < max(lastGPSLocation.getAccuracy(), location.getAccuracy())))
                    UserError.Log.d(TAG, "GPS location is within accuracy of last location (" + location2String(lastGPSLocation) + ") | " + location.distanceTo(lastGPSLocation) + "m -- no update!! ");
                else {
                    UserError.Log.d(TAG, "Got GPS location update!! " + location2String(location));
                    lastGPSLocation = location;
                    GPSAddressUpdated = JoH.tsl();
                }
            }
        }
        if (locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)) {
            @SuppressLint("MissingPermission") final Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            if (location == null)
                UserError.Log.d(TAG, "network location is null!!");
            else {
                UserError.Log.d(TAG, "Got network location " + location2String(location));
                if ((lastNetworkLocation != null) && (location.distanceTo(lastNetworkLocation) < max(lastNetworkLocation.getAccuracy(), location.getAccuracy())))
                    UserError.Log.d(TAG, "network location is within accuracy of last location (" + location2String(lastNetworkLocation) + ") | " + location.distanceTo(lastNetworkLocation) + "m -- no update!! ");
                else {
                    UserError.Log.d(TAG, "Got network location update!! " + location2String(location));
                    lastNetworkLocation = location;
                    NetworkAddressUpdated = JoH.tsl();
                }
            }
        }
        if (locationManager.isProviderEnabled(locationManager.PASSIVE_PROVIDER)) {
            @SuppressLint("MissingPermission") final Location location = locationManager.getLastKnownLocation(locationManager.PASSIVE_PROVIDER);
            if (location == null)
                UserError.Log.d(TAG, "passive location is null!!");
            else {
                UserError.Log.d(TAG, "Got passive location " + location2String(location));
                if ((lastPassiveLocation != null) && (location.distanceTo(lastPassiveLocation) < max(lastPassiveLocation.getAccuracy(), location.getAccuracy())))
                    UserError.Log.d(TAG, "passive location is within accuracy of last location (" + location2String(lastPassiveLocation) + ") | " + location.distanceTo(lastPassiveLocation) + "m -- no update!! ");
                else {
                    UserError.Log.d(TAG, "Got passive location update!! " + location2String(location));
                    lastPassiveLocation = location;
                    PassiveAddressUpdated = JoH.tsl();
                }
            }
        }
        lastBestLocation = lastGPSLocation;
    }

    private static final LocationListener GPSlocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            UserError.Log.d(TAG, "Got GPS location update!! " + location2String(location));
            if ((lastGPSLocation != null) && (location.distanceTo(lastGPSLocation) < max(lastGPSLocation.getAccuracy(), location.getAccuracy())))
                UserError.Log.d(TAG, "GPS location is within accuracy of last location (" + location2String(lastGPSLocation) + ") | " + location.distanceTo(lastGPSLocation) + "m -- no update!! ");
            else {
                lastGPSLocation = location;
                GPSAddressUpdated = JoH.tsl();
            }
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) { }
        @Override
        public void onProviderEnabled(String s) { }
        @Override
        public void onProviderDisabled(String s) { }
    };
    private static final LocationListener NetworkLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            UserError.Log.d(TAG, "Got Network location update!! " + location2String(location));
            if ((lastNetworkLocation != null) && (location.distanceTo(lastNetworkLocation) < max(lastNetworkLocation.getAccuracy(), location.getAccuracy())))
                UserError.Log.d(TAG, "Network location is within accuracy of last location (" + location2String(lastNetworkLocation) + ") | " + location.distanceTo(lastNetworkLocation) + "m -- no update!! ");
            else {
                lastNetworkLocation = location;
                GPSAddressUpdated = JoH.tsl();
            }
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) { }
        @Override
        public void onProviderEnabled(String s) { }
        @Override
        public void onProviderDisabled(String s) { }
    };
    private static final LocationListener PassiveLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            UserError.Log.d(TAG, "Got Passive location update!! " + location2String(location));
            if ((lastPassiveLocation != null) && (location.distanceTo(lastPassiveLocation) < max(lastPassiveLocation.getAccuracy(), location.getAccuracy())))
                UserError.Log.d(TAG, "Passive location is within accuracy of last location (" + location2String(lastPassiveLocation) + ") | " + location.distanceTo(lastPassiveLocation) + "m -- no update!! ");
            else {
                lastPassiveLocation = location;
                GPSAddressUpdated = JoH.tsl();
            }
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) { }
        @Override
        public void onProviderEnabled(String s) { }
        @Override
        public void onProviderDisabled(String s) { }
    };

    private static String location2String(Location l) {
        return l.getLatitude() + "," + l.getLongitude() + (l.hasAccuracy() ? " (+/- " + JoH.qs(l.getAccuracy(), 0) + "m)" : "");
    }
}
