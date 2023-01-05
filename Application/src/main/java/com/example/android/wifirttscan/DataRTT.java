package com.example.android.wifirttscan;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DataRTT  implements Serializable {
    private String BSSID;
    private String SSID;
    private float distance;
    private String RSSI;
    private long time;
    private float xCoordinate;
    private float yCoordinate;
    private float zCoordinate;
    private float distanceStdDevM;
    private int numAttemptedMeasurements;
    private int numSuccessfulMeasurements;
    private long rangingTimestampMillis;

    public DataRTT(String BSSID, String SSID, float distance,String RSSI, long time, float xCoordinate, float yCoordinate, float zCoordinate, float distanceStdDevM, int numAttemptedMeasurements, int numSuccessfulMeasurements, long rangingTimestampMillis) {
        this.BSSID = BSSID;
        this.SSID = SSID;
        this.distance = distance;
        this.RSSI = RSSI;
        this.time = time;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.zCoordinate = zCoordinate;
        this.distanceStdDevM = distanceStdDevM;
        this.numAttemptedMeasurements = numAttemptedMeasurements;
        this.numSuccessfulMeasurements = numSuccessfulMeasurements;
        this.rangingTimestampMillis = rangingTimestampMillis;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("BSSID", BSSID);
        result.put("SSID", SSID);
        result.put("distance", distance);
        result.put("RSSI", RSSI);
        result.put("time", time);
        result.put("xCoordinate", xCoordinate);
        result.put("yCoordinate", yCoordinate);
        result.put("zCoordinate", zCoordinate);
        result.put("distanceStdDevM", distanceStdDevM);
        result.put("numAttemptedMeasurements", numAttemptedMeasurements);
        result.put("numSuccessfulMeasurements", numSuccessfulMeasurements);
        result.put("rangingTimestampMillis", rangingTimestampMillis);

        return result;
    }
}
