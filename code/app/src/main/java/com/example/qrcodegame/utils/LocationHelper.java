package com.example.qrcodegame.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.qrcodegame.MainActivity;
import com.example.qrcodegame.models.QRCode;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class LocationHelper {

    public static int saveLocationInCode(QRCode currentQRCode, Context context) {

        AtomicInteger saveLocationSuccessful = new AtomicInteger();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled(context)) {
                    FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
                    fusedLocationClient.getCurrentLocation(LocationRequest.QUALITY_BALANCED_POWER_ACCURACY, new CancellationToken() {
                        @NonNull
                        @Override
                        public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                            return null;
                        }

                        @Override
                        public boolean isCancellationRequested() {
                            return false;
                        }
                    }).addOnSuccessListener(location -> {
                        currentQRCode.getCoordinates().add(location.getLatitude());
                        currentQRCode.getCoordinates().add(location.getLongitude());
                        saveLocationSuccessful.set(1);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(context, "Something went wrong with location!", Toast.LENGTH_SHORT).show();
                        System.err.println(e.getMessage());
                    });
                } else {
                    Toast.makeText(context, "Turn on Location Services!", Toast.LENGTH_SHORT).show();
                }
            } else {
                System.out.println("HEREEREERE LOCATION THINGYU");
            }
        } else {
            System.err.println("CANT GET LOCATION");
        }

        if (saveLocationSuccessful.equals(1)) {
            return 1;
        }
        return 0;
    }


    private static boolean isGPSEnabled(Context context) {
        LocationManager locationManager = null;
        boolean isEnabled = false;
        if (locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    };

}
