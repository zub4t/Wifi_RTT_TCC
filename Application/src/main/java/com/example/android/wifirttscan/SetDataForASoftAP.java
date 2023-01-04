package com.example.android.wifirttscan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.wifi.ScanResult;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.wifirttscan.databinding.ActivitySetDataForAsoftApBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class SetDataForASoftAP extends AppCompatActivity {

    private ActivitySetDataForAsoftApBinding binding;
    private EditText trueDistance;
    private EditText xCoordinate;
    private EditText yCoordinate;
    private EditText zCoordinate;
    private TextView softAP;
    private Button save;


    private ScanResult result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.activity_set_data_for_asoft_ap);
        result = (ScanResult) extras.get("RESULT");

        trueDistance = findViewById(R.id.true_distance);
        xCoordinate = findViewById(R.id.x_coordinate);
        yCoordinate = findViewById(R.id.y_coordinate);
        zCoordinate = findViewById(R.id.z_coordinate);
        softAP = findViewById(R.id.soft_ap_mac);
        save = findViewById(R.id.save_button);
        softAP.setText(result.BSSID);


        DataExtra dataExtraTargetAP = MainActivity.mapExtraInformation.get(result.BSSID);
        if (dataExtraTargetAP != null) {
            trueDistance.setText(dataExtraTargetAP.distance + "");
            xCoordinate.setText(dataExtraTargetAP.xCoordinateV + "");
            yCoordinate.setText(dataExtraTargetAP.yCoordinateV + "");
            zCoordinate.setText(dataExtraTargetAP.zCoordinateV + "");


        }



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    float distance = Float.parseFloat(trueDistance.getText().toString());
                    float xCoordinateV = Float.parseFloat(xCoordinate.getText().toString());
                    float yCoordinateV = Float.parseFloat(yCoordinate.getText().toString());
                    float zCoordinateV = Float.parseFloat(zCoordinate.getText().toString());

                    DataExtra dataExtra = new DataExtra(distance, xCoordinateV, yCoordinateV, zCoordinateV,result.SSID);

                    MainActivity.mapExtraInformation.put(result.BSSID, dataExtra);
                    Toast.makeText(getApplicationContext(), "SoftAP information defined", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }


}