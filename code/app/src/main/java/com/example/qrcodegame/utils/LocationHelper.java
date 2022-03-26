package com.example.qrcodegame.utils;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

import android.location.LocationListener;

/**
 * Class to handle Location services within the app
 * no issues
 */
public class LocationHelper implements LocationListener {

    static public ArrayList<LocationHelperListener> listeners = new ArrayList<>();

    private CurrentUserHelper currentUserHelper = CurrentUserHelper.getInstance();
    final private Context context;
    private LocationManager locationManager;

    public interface LocationHelperListener {
        void onCurrentUserLocationUpdated(Double latitude, Double longitude);
    }

    public LocationHelper(Context context) {
        this.context = context;
    }

    /**
     * Gets the updated location and puts results in CurrentUserHelper
     * @param location new location
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {

        Double lat = location.getLatitude();
        Double lng = location.getLongitude();

        ArrayList<Double> coordinates = new ArrayList<>();
        coordinates.add(lat);
        coordinates.add(lng);
        currentUserHelper.setCurrentLocation(coordinates);
        System.out.println("LOCATION UPDATED!");
        for (LocationHelperListener listener: listeners) {
            listener.onCurrentUserLocationUpdated(lat, lng);
        }
    }

    /**
     * Stops location updates.
     */
    public void stopLocationUpdates() {
        locationManager.removeUpdates(this);
    }

    /**
     * Starts the location updates. The results are stored within the CurrentUserHelper
     */
    public void startLocationUpdates() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2500, 10, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
