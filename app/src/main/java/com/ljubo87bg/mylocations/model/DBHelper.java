package com.ljubo87bg.mylocations.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by ljubo on 11/4/2016.
 */

public class DBHelper extends SQLiteOpenHelper {

    private HashMap<String,UserMarker> markers;
    private HashMap<UserMarker, ArrayList<String>> pictures;
    private Context context;
    private static DBHelper ourInstance;
    private DBHelper (Context context) {
        super(context, "MyPlaces.db", null, 1);
        this.context=context;
        markers = new HashMap<>();
        pictures = new HashMap<>();
    }
    public static DBHelper getInstance(Context context){
        if (ourInstance==null){ourInstance = new DBHelper(context);}
        return ourInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table locations (_id INTEGER PRIMARY KEY AUTOINCREMENT, Address TEXT," +
                "Country TEXT, Lat REAL, Long REAL)" );
        db.execSQL("create table pictures (File TEXT PRIMARY KEY NOT NULL, " +
                "location_id INTEGER)" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    public void init (){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM locations",null);
        if(cursor.moveToFirst()){
            do{
                markers.put(String.valueOf(cursor.getInt(0)),new UserMarker(cursor.getInt(0),
                        cursor.getString(1),cursor.getString(2),cursor.getDouble(3),
                        cursor.getDouble(4)));
            }while (cursor.moveToNext());
        }
        cursor = db.rawQuery("SELECT * FROM pictures",null);
        if(cursor.moveToFirst()){
            do{
                markers.get(String.valueOf(cursor.getInt(1))).addToPictures(cursor.getString(0));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    public ArrayList<UserMarker> getMarkers() {
        ArrayList<UserMarker> markerArraylist = new ArrayList<>();
        markerArraylist.addAll(markers.values());
        return markerArraylist;
    }

    public UserMarker getMarker (LatLng latLng) {
        Log.d("latitude",String.valueOf(latLng.latitude));
        Cursor cursor = this.getReadableDatabase().rawQuery( "SELECT * FROM locations WHERE Lat=?",
                new String[] {String.valueOf(latLng.latitude)} );
        Log.d("cursor2",String.valueOf(cursor.moveToFirst()));
        cursor.moveToFirst();
        int id=0;
        do{
            if(cursor.getDouble(4)==latLng.longitude)
                id=cursor.getInt(0);
        }while(cursor.moveToNext());
        cursor.close();

        Cursor cursor2 = this.getReadableDatabase().rawQuery( "SELECT * FROM locations WHERE _id=?",
                new String[] { String.valueOf(id) } );
        cursor2.moveToFirst();
        String address = cursor2.getString(1);
        String country = cursor2.getString(2);
        return new UserMarker(id,address,country,latLng.latitude,latLng.longitude);
    }

    public void editMarker (int markerID, double lat, double lng){
        deleteMarker(markerID);
        addMarker(new LatLng(lat,lng));
    }

    public void deleteMarker (int markerID){
        SQLiteDatabase db = this.getWritableDatabase();
        try{db.delete("locations", "_id = ?", new String[] { String.valueOf(markerID) }); }
        catch(Exception e){e.printStackTrace(); }
        finally{ db.close(); }
    }

    public void addMarker (LatLng latLng){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses= null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address = addresses.get(0).getAddressLine(0);
        String country = addresses.get(0).getCountryName();

        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Address",address);
        contentValues.put("Country",country);

        Log.d("latitudeadd",String.valueOf(latLng.latitude));
        contentValues.put("Lat",latLng.latitude);
        contentValues.put("Long",latLng.longitude);
        db.insert("locations", null, contentValues);
        Cursor cursor = this.getReadableDatabase().rawQuery( "SELECT * FROM locations WHERE Lat=?",
                new String[] {String.valueOf(latLng.latitude)} );
        cursor.moveToFirst();
        int id=0;
        do{if(cursor.getDouble(4)==latLng.longitude) id=cursor.getInt(0);}while(cursor.moveToNext());
        markers.put(String.valueOf(id),new UserMarker(id,address,country,latLng.latitude,latLng.longitude));
    }
}
