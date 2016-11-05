package com.ljubo87bg.mylocations.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
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
import com.ljubo87bg.mylocations.adapters.PicturesAdapter;
import com.ljubo87bg.mylocations.model.DBHelper;
import com.ljubo87bg.mylocations.model.UserMarker;

import java.io.File;

public class InfoActivity extends MapsActivity implements OnMapReadyCallback {

    private TextView id, country, address, latitude, longitude;
    private Button add, delete;
    private GoogleMap mMap;
    private Intent intent;
    private double latitudeValue, longitudeValue;
    private UserMarker userMarker;
    private static final int CONTENT_REQUEST=1337;
    private File output=null;
    private PicturesAdapter picturesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_info);
        mapFragment.getMapAsync(this);
        intent = getIntent();
        latitudeValue = intent.getExtras().getDouble("lat");
        longitudeValue = intent.getExtras().getDouble("lgt");
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
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File dir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                output=new File(dir, userMarker.getMarkerID()+"_"+userMarker.getPictures().size()+".jpeg");
                i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
                startActivityForResult(i, CONTENT_REQUEST);
            }
        });

        RecyclerView pictures = (RecyclerView) findViewById(R.id.recyclerList);
        pictures.setLayoutManager(new LinearLayoutManager(this));
        picturesAdapter = new PicturesAdapter(userMarker.getPictures());
        pictures.setAdapter(picturesAdapter);

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
                        setResult(RESULT_CANCELED, data);
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
        LatLng coordinates = new LatLng(latitudeValue, longitudeValue);
        mMap.addMarker(new MarkerOptions().position(coordinates).draggable(true));
        moveToCurrentLocation(coordinates);
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                latitudeValue = marker.getPosition().latitude;
                longitudeValue = marker.getPosition().longitude;
                db.editMarker(userMarker.getMarkerID(), latitudeValue, longitudeValue);
            }
        });
    }

    private void moveToCurrentLocation(LatLng currentLocation) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("lat", latitudeValue);
        intent.putExtra("lgt", longitudeValue);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONTENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                userMarker = DBHelper.getInstance(InfoActivity.this).getMarker(new LatLng(latitudeValue,
                        longitudeValue));
                Uri.fromFile(output);
                final String picURL = output.getAbsolutePath();
                userMarker.addToPictures(picURL);
                picturesAdapter.notifyDataSetChanged();
                new AsyncTask<Void,Void,Void> (){

                    @Override
                    protected Void doInBackground(Void... params) {
                        db.addToPictures(userMarker.getMarkerID(),picURL);
                        return null;
                    }
                }.execute();
            }
        }
    }
}
