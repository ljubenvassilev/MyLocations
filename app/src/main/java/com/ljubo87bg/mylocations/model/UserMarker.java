package com.ljubo87bg.mylocations.model;

import java.util.ArrayList;

/**
 * Created by ljubo on 11/4/2016.
 */
public class UserMarker{

    private int markerID;
    private String address;
    private String country;
    private double lat;
    private double lng;
    private ArrayList<String> pictures;

    public UserMarker(String address, String country, double lat, double lng) {
        this.address = address;
        this.country = country;
        this.lat = lat;
        this.lng = lng;
        this.pictures = new ArrayList<>();
    }

    public ArrayList<String> getPictures() {
        return pictures;
    }

    public void addToPictures(String picture) {
        this.pictures.add(picture);
    }

    public void removeFromPictures(String picture){
        this.pictures.remove(picture);
    }

    public int getMarkerID() {
        return markerID;
    }

    public String getAddress() {
        return address;
    }

    public String getCountry() {
        return country;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setMarkerID(int markerID) {
        this.markerID = markerID;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }
}
