package com.this25.safeteria.MapActivity;

public class MarkerItem {
    double lat;
    public MarkerItem(double lat, double lon, int price) {
        this.lat = lat;
    }
    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
}
