package com.eveningoutpost.dexdrip.eassist;

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
import android.support.v4.content.ContextCompat;

import com.eveningoutpost.dexdrip.models.JoH;
import com.eveningoutpost.dexdrip.models.UserError;
import com.eveningoutpost.dexdrip.xdrip;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import lombok.Getter;

import static com.eveningoutpost.dexdrip.xdrip.gs;

// jamorham

// Do our best to get the most accurate location and geocode we can for emergency message feature

// Using old (9.x) apis of play services due to legacy compatibility

public class GetLocationByLM {

    private static final String TAG = GetLocationByLM.class.getSimpleName();


    private static LocationManager locationManager;
    private static volatile Location lastLocation;
    private static volatile String lastAddress;
    private static long addressUpdated = 0;

    // TODO this can be centralized
    public final static int MY_PERMISSIONS_REQUEST_GPS = 104;

    private static boolean checkLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(xdrip.getAppContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(xdrip.getAppContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED))
                return false;
        }
        return true;
    }
    @SuppressLint("MissingPermission")
    public static void prepareForLocation() {
        // turn on wifi? gps? bluetooth?
        JoH.setBluetoothEnabled(xdrip.getAppContext(), true);
        locationManager = (LocationManager) xdrip.getAppContext().getSystemService(Context.LOCATION_SERVICE);
        UserError.Log.d(TAG, "Requesting live GPS updates");
        if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
            if (!checkLocationPermissions()) {
                return;
            }
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 2 * 60 * 1000, 10,
                    locationListener);
        }
    }


    public static String getBestLocation() {

        if (lastLocation == null) {
            return "Location unknown!";
        }

        if (lastAddress == null) {
            return "GPS: " + lastLocation.getLatitude() + "," + lastLocation.getLongitude() + accuracyAddendum();
        }

        return lastAddress + " (" + lastLocation.getLatitude() + "," + lastLocation.getLongitude() + ")" + accuracyAddendum();
    }

    private static String accuracyAddendum() {
        return lastLocation.hasAccuracy() ? " (+/- " + JoH.qs(lastLocation.getAccuracy(), 0) + "m)" : "";
    }

    public static String getMapUrl() {
        if (lastLocation == null) return "";
        return "https://maps.google.de/?q=" + lastLocation.getLatitude() + "," + lastLocation.getLongitude();
    }


    @Getter// TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
    private final static long GPS_ACTIVE_TIME = 60000;

    public synchronized static void getLocation() {

        final Context context = xdrip.getAppContext();

        if (!checkLocationPermissions()) {
            UserError.Log.wtf(TAG, "No permission to obtain location");
            return;
        }

        if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
            @SuppressLint("MissingPermission") final Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            if (location != null) {
                lastLocation = location;
                UserError.Log.d(TAG, location.toString());
                lastAddress = getStreetLocation(location.getLatitude(), location.getLongitude());
                UserError.Log.d(TAG, "Address: " + lastAddress);
                addressUpdated = JoH.tsl();
            }
        } else {
            UserError.Log.e(TAG, "GPS provider not enabled");
        }
    }

    private static final float SKIP_DISTANCE = 100;
    private static final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                UserError.Log.d(TAG, "Got location update!! " + location);
                if ((lastLocation == null)
                        || location.getAccuracy() < lastLocation.getAccuracy()
                        || ((location.getAccuracy() < SKIP_DISTANCE) && (location.distanceTo(lastLocation) > SKIP_DISTANCE))) {

                    lastLocation = location;
                    UserError.Log.d(TAG, "Got location UPDATED element: " + lastLocation);
                    lastAddress = getStreetLocation(lastLocation.getLatitude(), lastLocation.getLongitude());
                }
            }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };


    public static String getStreetLocation(double latitude, double longitude) {
        try {
            final Geocoder geocoder = new Geocoder(xdrip.getAppContext(), Locale.getDefault());
            final List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            UserError.Log.d(TAG, addresses.toString());
            final String address = addresses.get(0).getAddressLine(0);
            UserError.Log.d(TAG, "Street address: " + address);
            return address;

        } catch (IndexOutOfBoundsException | NullPointerException e) {
            UserError.Log.e(TAG, "Couldn't isolate street address");
        } catch (IOException e) {
            UserError.Log.e(TAG, "Location error (reboot sometimes helps fix geocoding): " + e);

        }
        return null;

    }
}
