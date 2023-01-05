package com.example.android.wifirttscan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;

public class Init extends AppCompatActivity {
    private EditText timeValue;
    private EditText timeValueB;
    private TextView config;

    private Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        timeValue = findViewById(R.id.time_value);
        timeValueB = findViewById(R.id.time_between);
        config= findViewById(R.id.configuration_path);

        File file = new File(Environment.getExternalStorageDirectory(), "/WiFiRTT/configuration.json");

        config.setText(file.getAbsolutePath());
        next = findViewById(R.id.next_button);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the external storage directory
                File storageDir = Environment.getExternalStorageDirectory();

                // Create a new directory in the external storage
                File d = new File(storageDir, "/WiFiRTT/");
                // Check if the directory exists
                if (!d.exists()) {
                    d.mkdirs();
                }


                long time = Long.parseLong(timeValue.getText().toString());
                long timeB = Long.parseLong(timeValue.getText().toString());

                MainActivity.time = time;
                MainActivity.timeB = timeB;

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });


    }
}