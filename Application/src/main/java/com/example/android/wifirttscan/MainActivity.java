/*
 * Copyright (C) 2018 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.wifirttscan;


import static android.app.PendingIntent.getActivity;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.rtt.RangingRequest;
import android.net.wifi.rtt.WifiRttManager;
import android.os.Bundle;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.wifirttscan.MyAdapter.ScanResultClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.utilities.Tree;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

/**
 * Displays list of Access Points enabled with WifiRTT (to check distance). Requests location
 * permissions if they are not approved via secondary splash screen explaining why they are needed.
 */
public class MainActivity extends AppCompatActivity implements ScanResultClickListener {


    static ArrayList<ScanResult> mAccessPointsSupporting80211mc = new ArrayList<>();
    static Map<String, DataExtra> mapExtraInformation = new TreeMap<>();
    static long nextID = 0;
    static boolean canSave = true;

    private static final String TAG = "MainActivity";
    private boolean mLocationPermissionApproved = false;
    private WifiManager mWifiManager;
    private WifiScanReceiver mWifiScanReceiver;

    private TextView mOutputTextView;
    private RecyclerView mRecyclerView;
    private int mNumberOfRangeRequests;

    private MyAdapter mAdapter;
    private WifiRttManager mWifiRttManager;
    private RttRangingResultCallback mRttRangingResultCallback;
    private Button firebaseStart;
    private Button firebaseStop;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        boolean rttCapable = (getApplication().getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_RTT));
        String messageFormat = String.format("This device is RTT capable ? %s.", rttCapable);
        Toast.makeText(getApplicationContext(), messageFormat, Toast.LENGTH_LONG).show();

        mOutputTextView = findViewById(R.id.access_point_summary_text_view);
        mRecyclerView = findViewById(R.id.recycler_view);
        firebaseStart = findViewById(R.id.firebase_start);
        firebaseStop = findViewById(R.id.firebase_stop);
        firebaseStop.setEnabled(false);
        // Improve performance if you know that changes in content do not change the layout size
        // of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);


        if (mAccessPointsSupporting80211mc.size() == 0) {
            Log.d("FTM", " extras == null");
            mAccessPointsSupporting80211mc = new ArrayList<>();
            mAdapter = new MyAdapter(mAccessPointsSupporting80211mc, this);
            mRecyclerView.setAdapter(mAdapter);
            mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            mWifiScanReceiver = new WifiScanReceiver();


        } else {
            Log.d("FTM", " extras != null");

            mAdapter = new MyAdapter(mAccessPointsSupporting80211mc, this);
            mRecyclerView.setAdapter(mAdapter);

        }

        firebaseStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("RTT", "Initiating RTT");

                startRangingRequest(mAccessPointsSupporting80211mc);
            }
        });
        firebaseStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canSave=false;
            }
        });
        firebaseStart.setEnabled(mAccessPointsSupporting80211mc.size() > 0 && mapExtraInformation.size() == mAccessPointsSupporting80211mc.size());
        mDatabaseReference.getRoot().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    if (task.getResult().getValue() != null) try {
                        List<Map<String, Object>> listMap = (List<Map<String, Object>>) task.getResult().getValue();
                        Log.d("firebase", listMap.size() + "");
                        nextID = listMap.size();
                        listMap = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop()");

        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();

        mLocationPermissionApproved = ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
        //unregisterReceiver(mWifiScanReceiver);

    }

    private void logToUi(final String message) {
        if (!message.isEmpty()) {
            Log.d(TAG, message);
            mOutputTextView.setText(message);
        }
    }

    private void startRangingRequest(List<ScanResult> scanResultList) {

        mWifiRttManager = (WifiRttManager) getSystemService(Context.WIFI_RTT_RANGING_SERVICE);

        mRttRangingResultCallback = new RttRangingResultCallback(new Callable<Void>() {
            public Void call() {
                startRangingRequest(mAccessPointsSupporting80211mc);
                return null;
            }
        });
        canSave=true;
        firebaseStop.setEnabled(true);
        // Permission for fine location should already be granted via MainActivity (you can't get
        // to this class unless you already have permission. If they get to this class, then disable
        // fine location permission, we kick them back to main activity.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            finish();
        }
        Log.d("RTT", "StartingRagingRequest N:" + mNumberOfRangeRequests);
        mNumberOfRangeRequests++;

        RangingRequest rangingRequest = new RangingRequest.Builder().addAccessPoints(scanResultList).build();

        mWifiRttManager.startRanging(rangingRequest, getApplication().getMainExecutor(), mRttRangingResultCallback);
    }

    @Override
    public void onScanResultItemClick(ScanResult scanResult) {
        Log.d(TAG, "onScanResultItmeClick(): ssid: " + scanResult.SSID);

        Intent intent = new Intent(this, SetDataForASoftAP.class);
        intent.putExtra("RESULT", scanResult);
        startActivity(intent);

    }

    public void onClickFindDistancesToAccessPoints(View view) {
        if (mLocationPermissionApproved) {
            logToUi(getString(R.string.retrieving_access_points));
            mWifiManager.startScan();

        } else {
            // On 23+ (M+) devices, fine location permission not granted. Request permission.
            Intent startIntent = new Intent(this, LocationPermissionRequestActivity.class);
            startActivity(startIntent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("mAccessPointsSupporting80211mc", mAccessPointsSupporting80211mc);
        Log.d(TAG, "onSaveInstanceState");

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState");

        Log.d(TAG, "savedInstanceState:" + savedInstanceState);

    }

    private class WifiScanReceiver extends BroadcastReceiver {

        private ArrayList<ScanResult> find80211mcSupportedAccessPoints(@NonNull List<ScanResult> originalList) {
            ArrayList<ScanResult> newList = new ArrayList<>();

            for (ScanResult scanResult : originalList) {

                if (scanResult.is80211mcResponder()) {
                    newList.add(scanResult);
                }

                if (newList.size() >= RangingRequest.getMaxPeers()) {
                    break;
                }
            }
            return newList;
        }

        // This is checked via mLocationPermissionApproved boolean
        @SuppressLint("MissingPermission")
        public void onReceive(Context context, Intent intent) {
            Log.d("RTT", "onReceive Called from broadCast");

            Toast.makeText(context, "started search for FTM responders", Toast.LENGTH_LONG).show();
            List<ScanResult> scanResults = mWifiManager.getScanResults();

            if (scanResults != null) {

                if (mLocationPermissionApproved) {
                    mAccessPointsSupporting80211mc = find80211mcSupportedAccessPoints(scanResults);


                    mAdapter.swapData(mAccessPointsSupporting80211mc);

                    logToUi(scanResults.size() + " APs discovered, " + mAccessPointsSupporting80211mc.size() + " RTT capable.");

                } else {
                    // TODO (jewalker): Add Snackbar regarding permissions
                    Log.d(TAG, "Permissions not allowed.");
                }


            }

        }
    }
}
