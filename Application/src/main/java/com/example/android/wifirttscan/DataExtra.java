package com.example.android.wifirttscan;

public class DataExtra {
    float xCoordinateV;
    float yCoordinateV;
    float zCoordinateV;
    String SSID;

    public DataExtra(float xCoordinateV, float yCoordinateV, float zCoordinateV,String SSID) {
        this.xCoordinateV = xCoordinateV;
        this.yCoordinateV = yCoordinateV;
        this.zCoordinateV = zCoordinateV;
        this.SSID = SSID;
    }
}
