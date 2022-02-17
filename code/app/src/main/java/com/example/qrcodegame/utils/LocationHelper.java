package com.example.qrcodegame.utils;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.qrcodegame.models.QRCode;


import java.util.ArrayList;

import android.location.LocationListener;
import android.location.LocationRequest;
import android.util.Log;

public class LocationHelper implements LocationListener {

    private CurrentUserHelper currentUserHelper = CurrentUserHelper.getInstance();
    final private Context context;
    private LocationManager locationManager;

    public LocationHelper(Context context) {
        this.context = context;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        ArrayList<Double> coordinates = new ArrayList<>();
        coordinates.add(location.getLatitude());
        coordinates.add(location.getLongitude());
        currentUserHelper.setCurrentLocation(coordinates);
        System.out.println("LOCATION UPDATED!");
    }

    public void stopLocationUpdates() {
        locationManager.removeUpdates(this);
    }

    public void startLocationUpdates() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
