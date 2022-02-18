package com.example.qrcodegame.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.example.qrcodegame.MainActivity;
import com.example.qrcodegame.QRCodeCommentActivity;
import com.example.qrcodegame.R;
import com.example.qrcodegame.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    LocationListener locationListener;
    LocationManager locationManager;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    public void centerMapOnLocation(Location location, String title) {

        if (location != null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            //zooming on the user location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerMapOnLocation(lastKnownLocation, "Your Location");
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);

        restoreMarkers();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //this avoid the app from crashing if the user has not given permission
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

            }
        };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            centerMapOnLocation(lastKnownLocation, "Your Location");

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    public void restoreMarkers() {
        DocumentReference reference = FirebaseFirestore.getInstance().collection("Locations")
                .document("places");
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                List<String> lc = (List<String>) value.get("location");
                if (lc == null) {
                    return;
                }
                for (String i : lc) {
                    if (!i.equals("")) {
                        String[] locate = i.split(",");
                        LatLng latLng = new LatLng(Double.parseDouble(locate[0]), Double.parseDouble(locate[1]));
                        mMap.addMarker(new MarkerOptions().position(latLng).title("qrcode"));
                    }
                }
            }
        });

    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        startActivity(new Intent(MapsActivity.this, QRCodeCommentActivity.class));

        return false;
    }
}

