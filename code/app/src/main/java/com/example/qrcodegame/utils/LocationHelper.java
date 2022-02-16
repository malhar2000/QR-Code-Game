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

    private Context context;
    private QRCode currentQRCode;
    private LocationManager locationManager;
    private handleLocationChanged listener;

    public LocationHelper (Context context, QRCode currentQRCode, handleLocationChanged listener) {
        this.context = context;
        this.currentQRCode = currentQRCode;
        this.listener = listener;
    }

    public interface handleLocationChanged {
        void onLocationReady();
    }

    public QRCode getCurrentQRCode() {
        return currentQRCode;
    }

    public void setCurrentQRCode(QRCode currentQRCode) {
        this.currentQRCode = currentQRCode;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        ArrayList<Double> coordinates = new ArrayList<>();
        coordinates.add(location.getLatitude());
        coordinates.add(location.getLongitude());
        Log.d("CurrentUserUpdated", currentQRCode.toString());
        if (currentQRCode != null) {
            currentQRCode.setCoordinates(coordinates);
            locationManager.removeUpdates(this);
            listener.onLocationReady();
        };
    }

    public void getCurrentLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
