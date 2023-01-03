package com.example.android.wifirttscan;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.rtt.RangingRequest;
import android.net.wifi.rtt.RangingResult;
import android.net.wifi.rtt.RangingResultCallback;
import android.net.wifi.rtt.WifiRttManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class RttRangingResultCallback extends RangingResultCallback {
    Callable<Void> methodParam;
    Context ctx;
    RttRangingResultCallback(Callable<Void> methodParam,Context ctx) {
        this.methodParam = methodParam;
        this.ctx = ctx;
    }

    // Triggers additional RangingRequests with delay (mMillisecondsDelayBeforeNewRangingRequest).
    final Handler mRangeRequestDelayHandler = new Handler();
    private ScanResult mScanResult;
    private String mMAC;

    private int mNumberOfSuccessfulRangeRequests;
    private WifiRttManager mWifiRttManager;

    private int mMillisecondsDelayBeforeNewRangingRequest=0;

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mDatabase.getReference();


    private void queueNextRangingRequest() {
        mRangeRequestDelayHandler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (MainActivity.canSave)
                                methodParam.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                mMillisecondsDelayBeforeNewRangingRequest);
    }

    @Override
    public void onRangingFailure(int code) {
        Log.d("FTM", "onRangingFailure() code: " + code);
        queueNextRangingRequest();
    }

    @Override
    public void onRangingResults(@NonNull List<RangingResult> list) {
        Log.d("FTM", "onRangingResults(): " + list);
        // Because we are only requesting RangingResult for one access point (not multiple
        // access points), this will only ever be one. (Use loops when requesting RangingResults
        // for multiple access points.)
        for (RangingResult rangingResult : list) {
            if (!MainActivity.canSave) {
                break;
            }

           if (rangingResult.getStatus() == RangingResult.STATUS_SUCCESS) {
           // if (true) {
                mNumberOfSuccessfulRangeRequests++;
                float distance = (float) rangingResult.getDistanceMm() / 1000.0f;
                String BSSID = rangingResult.getMacAddress().toString();
                String RSSI = String.valueOf(rangingResult.getRssi());
                long timestamp = System.currentTimeMillis();
                DataExtra dataExtra = MainActivity.mapExtraInformation.get(BSSID);
                String SSID = dataExtra.SSID;
                float distanceStdDevM = (float) rangingResult.getDistanceStdDevMm() / 1000.0f;

                DataRTT dataRTT = new DataRTT(BSSID, SSID, distance, RSSI, timestamp,
                        dataExtra.distance, dataExtra.xCoordinateV, dataExtra.yCoordinateV,
                        dataExtra.zCoordinateV, distanceStdDevM, rangingResult.getNumAttemptedMeasurements(),
                        rangingResult.getNumSuccessfulMeasurements(), rangingResult.getRangingTimestampMillis());

               // mDatabaseReference = mDatabase.getReference().child(String.valueOf(MainActivity.nextID));
                final Map<String, Object> dataMap = new HashMap<String, Object>();
               // mDatabaseReference.setValue(dataRTT.toMap());
                MapCsv mapCsv = new MapCsv(dataMap);
                Log.d("DATA","SALVANDO");
                mapCsv.writeFileExternalStorage(ctx);
                MainActivity.nextID++;

            }

            queueNextRangingRequest();
        }
    }
}