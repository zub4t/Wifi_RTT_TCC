package com.example.android.wifirttscan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.rtt.RangingRequest;
import android.net.wifi.rtt.WifiRttManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class PerformRangingRequest extends AppCompatActivity {
    private RttRangingResultCallback mRttRangingResultCallback;
    private int mNumberOfRangeRequests;
    private TextView information;
    private ProgressBar progressBar;
    private long startTime;
    private long elapsedTime;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    private boolean canDoRttRequest = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perform_ranging_request);
        information = findViewById(R.id.information);
        progressBar = findViewById(R.id.progressBar);
        startTime = System.currentTimeMillis();
        elapsedTime = 0;


        progressBar.setMax((int) (MainActivity.time * 60));
        progressBar.setMin(0);
        startRangingRequest(MainActivity.mAccessPointsSupporting80211mc);
        information.setText("Requesting...");
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < progressBar.getMax()) {

                    progressStatus += 1;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            if (information.getText().equals("Requesting...")) {
                                information.setText("Requesting");

                            } else if (information.getText().equals("Requesting")) {
                                information.setText("Requesting.");

                            } else if (information.getText().equals("Requesting.")) {
                                information.setText("Requesting..");

                            } else {
                                information.setText("Requesting...");

                            }
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                canDoRttRequest = false;
                handler.post(new Runnable() {
                    public void run() {
                        information.setText("Done!!\nfile:"+MainActivity.currentFileName);

                    }
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                }
            }
        }).start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        canDoRttRequest = true;
    }

    private void startRangingRequest(List<ScanResult> scanResultList) {
       // Log.d("NOW", "INICIANDO RTT REQUEST");
        MainActivity.mWifiRttManager = (WifiRttManager) getSystemService(Context.WIFI_RTT_RANGING_SERVICE);

        mRttRangingResultCallback = new RttRangingResultCallback(new Callable<Void>() {
            public Void call() {
                if (canDoRttRequest)
                    startRangingRequest(MainActivity.mAccessPointsSupporting80211mc);
                return null;
            }
        }, this);
        // Permission for fine location should already be granted via MainActivity (you can't get
        // to this class unless you already have permission. If they get to this class, then disable
        // fine location permission, we kick them back to main activity.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            finish();
        }
   //     Log.d("RTT", "StartingRagingRequest N:" + mNumberOfRangeRequests);
        mNumberOfRangeRequests++;

        RangingRequest rangingRequest = new RangingRequest.Builder().addAccessPoints(scanResultList).build();

        MainActivity.mWifiRttManager.startRanging(rangingRequest, getApplication().getMainExecutor(), mRttRangingResultCallback);
    }


}