package com.mist.it.pod_nk;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Tunyaporn on 7/24/2017.
 */

public class GPSManager {
    private LocationManager locationManager;
    private Context context;
    private String latString, longString;

    public GPSManager(Context context) {
        this.context = context;
    }

    private Location requestLocation(String strProvider, String strError) {

        Location location = null;

        if (locationManager.isProviderEnabled(strProvider)) {


            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            locationManager.requestLocationUpdates(strProvider, 1000, 10, locationListener);
            location = locationManager.getLastKnownLocation(strProvider);

        } else {
            Log.d("GPS", strError);
        }


        return location;
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
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

    private void setupLocation() {

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);


    }   // setupLocation

    public boolean setLatLong(int rev) {
        boolean b = true;
        boolean result = false;

        do {
            Log.d("ServiceTag", "Do " + rev);
            String strLat = "Unknown";
            String strLng = "Unknown";
            setupLocation();
            Location networkLocation = requestLocation(LocationManager.NETWORK_PROVIDER, "No Internet");
            if (networkLocation != null) {
                strLat = String.format(new Locale("th"), "%.7f", networkLocation.getLatitude());
                strLng = String.format(new Locale("th"), "%.7f", networkLocation.getLongitude());
            }

            Location gpsLocation = requestLocation(LocationManager.GPS_PROVIDER, "No GPS card");
            if (gpsLocation != null) {
                strLat = String.format(new Locale("th"), "%.7f", gpsLocation.getLatitude());
                strLng = String.format(new Locale("th"), "%.7f", gpsLocation.getLongitude());
            }

            if (strLat.equals("Unknown") && strLng.equals("Unknown") && rev < 10) {

                rev++;
//                setLatLong(rev);
                Log.d("ServiceTag", "Repeat");
            } else if (strLat.equals("Unknown") && strLng.equals("Unknown") && rev >= 10) {
                //Can't get lat/long
                Log.d("ServiceTag", "Can't get lat/long");
                rev++;
                b = false;
            } else {
                latString = strLat;
                longString = strLng;
                b = false;
                result = true;
            }
        } while (b);


        return result;

    }

    public String getLatString() {
        return latString;
    }

    public String getLongString() {
        return longString;
    }

    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
