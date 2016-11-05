package com.ljubo87bg.mylocations.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ljubo87bg.mylocations.R;
import com.ljubo87bg.mylocations.model.DBHelper;
import com.ljubo87bg.mylocations.model.UserMarker;

public class InfoActivity extends MapsActivity implements OnMapReadyCallback {

    private TextView id, country, address, latitude, longitude;
    private Button add,delete;
    private GoogleMap mMap;
    private Intent intent;
    private double latitudeValue, longitudeValue;
    private UserMarker userMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_info);
        mapFragment.getMapAsync(this);
        intent = getIntent();
        latitudeValue=intent.getExtras().getDouble("lat");
        longitudeValue=intent.getExtras().getDouble("lgt");
        userMarker = DBHelper.getInstance(InfoActivity.this).getMarker(new LatLng(latitudeValue,
                longitudeValue));
        id = (TextView) findViewById(R.id.idTV);
        id.setText(String.valueOf(userMarker.getMarkerID()));
        country = (TextView) findViewById(R.id.countryTV);
        country.setText(userMarker.getCountry());
        address = (TextView) findViewById(R.id.addressTV);
        address.setText(userMarker.getAddress());
        latitude = (TextView) findViewById(R.id.latitudeTV);
        latitude.setText(String.valueOf(latitudeValue));
        longitude = (TextView) findViewById(R.id.longitudeTV);
        longitude.setText(String.valueOf(longitudeValue));
        add = (Button) findViewById(R.id.button_add);
        delete = (Button) findViewById(R.id.button_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        db.deleteMarker(userMarker.getMarkerID());
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        Intent data = new Intent();
                        setResult(RESULT_CANCELED,data);
                    }
                }.execute();
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        LatLng coordinates = new LatLng(latitudeValue,longitudeValue);
        mMap.addMarker(new MarkerOptions().position(coordinates).draggable(true));
        moveToCurrentLocation(coordinates);
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {}

            @Override
            public void onMarkerDrag(Marker marker) {}

            @Override
            public void onMarkerDragEnd(Marker marker) {
                latitudeValue = marker.getPosition().latitude;
                longitudeValue = marker.getPosition().longitude;
                db.editMarker(userMarker.getMarkerID(),latitudeValue,longitudeValue);
            }
        });
    }

    private void moveToCurrentLocation(LatLng currentLocation) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("lat", latitudeValue);
        intent.putExtra("lgt", longitudeValue);
        setResult(RESULT_OK,intent);
        super.onBackPressed();
    }
}
