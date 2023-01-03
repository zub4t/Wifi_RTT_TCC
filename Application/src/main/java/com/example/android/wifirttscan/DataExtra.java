package com.example.android.wifirttscan;

public class DataExtra {
    float distance;
    float xCoordinateV;
    float yCoordinateV;
    float zCoordinateV;
    String SSID;

    public DataExtra(float distance, float xCoordinateV, float yCoordinateV, float zCoordinateV,String SSID) {
        this.distance = distance;
        this.xCoordinateV = xCoordinateV;
        this.yCoordinateV = yCoordinateV;
        this.zCoordinateV = zCoordinateV;
        this.SSID = SSID;
    }
}
