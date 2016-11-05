package com.ljubo87bg.mylocations.activities;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ljubo87bg.mylocations.R;
import com.ljubo87bg.mylocations.model.DBHelper;
import com.ljubo87bg.mylocations.model.UserMarker;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DBHelper db=DBHelper.getInstance(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        db.init();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final ArrayList<UserMarker> markers = db.getMarkers();
        for(UserMarker marker:markers){
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(marker.getLat(),
                    marker.getLng()));
            mMap.addMarker(markerOptions);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d("latitude",String.valueOf(marker.getPosition().latitude));
                startActivity(new Intent(MapsActivity.this,InfoActivity.class)
                        .putExtra("lat",marker.getPosition().latitude)
                        .putExtra("lgt",marker.getPosition().longitude));
                return true;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng));
                db.addMarker(latLng);
            }
        });
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
