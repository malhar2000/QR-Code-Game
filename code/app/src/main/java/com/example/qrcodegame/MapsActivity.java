package com.example.qrcodegame;

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

import com.example.qrcodegame.databinding.ActivityMapsBinding;
import com.example.qrcodegame.models.QRCode;
import com.example.qrcodegame.utils.CurrentUserHelper;
import com.example.qrcodegame.utils.LocationHelper;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationHelper.LocationHelperListener {

    CurrentUserHelper currentUserHelper = CurrentUserHelper.getInstance();
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LocationHelper.listeners.add(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void initUserLocation() {
        if (currentUserHelper.getCurrentLocation().size() > 0) {
            LatLng userLocation = new LatLng(currentUserHelper.getCurrentLocation().get(0), currentUserHelper.getCurrentLocation().get(1));
            //zooming on the user location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 13));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        initUserLocation();
        restoreMarkers();
    }

    public void restoreMarkers() {

        FirebaseFirestore.getInstance()
                .collection("Codes")
                .whereNotEqualTo("coordinates", new ArrayList<>())
                .addSnapshotListener((value, error) -> {
                    mMap.clear();
                    assert value != null;
                    for (DocumentSnapshot ds : value.getDocuments()){
                        QRCode tempCode = ds.toObject(QRCode.class);
                        assert tempCode != null;
                        LatLng latLng = new LatLng(tempCode.getCoordinates().get(0), tempCode.getCoordinates().get(1));
                        mMap.addMarker(new MarkerOptions().position(latLng).title(tempCode.getId()));
                    }
                });
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Intent intent = new Intent(MapsActivity.this, SingleQRActivity.class);
        intent.putExtra("codeID", marker.getTitle());
        startActivity(intent);
        finish();
        return false;
    }

    @Override
    public void onCurrentUserLocationUpdated(Double latitude, Double longitude) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 13));
    }
}

