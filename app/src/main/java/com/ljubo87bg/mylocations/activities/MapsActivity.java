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
    private SupportMapFragment mapFragment;
    Marker clicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        db.init();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        final ArrayList<UserMarker> markers = db.getMarkers();
        for(UserMarker marker:markers){
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(marker.getLat(),
                    marker.getLng()));
            mMap.addMarker(markerOptions);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                clicked=marker;
                startActivityForResult(new Intent(MapsActivity.this,InfoActivity.class)
                        .putExtra("lat",marker.getPosition().latitude)
                        .putExtra("lgt",marker.getPosition().longitude),1);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            Log.d("moving","go");
            clicked.setPosition(new LatLng(data.getExtras().getDouble("lat"),data.getExtras().getDouble("lgt")));
        }else
        if(resultCode==RESULT_CANCELED) {
            clicked.remove();
        }
    }
}
