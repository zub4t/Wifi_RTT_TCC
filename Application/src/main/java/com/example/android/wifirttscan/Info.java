package com.example.android.wifirttscan;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Info extends AppCompatActivity {
    private TextView desc;
    private EditText dscValue;
    private Button saveDsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        desc = findViewById(R.id.description);
        dscValue = findViewById(R.id.description_value);
        saveDsc = findViewById(R.id.save_desc);

        desc.setText("Write a comment about the experiment that will take place");
        dscValue.setText(MainActivity.currentFileDescription);
        saveDsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.currentFileDescription = dscValue.getText().toString();
            }
        });
        /*
        File storageDir = Environment.getExternalStorageDirectory();

        File d = new File(storageDir, "/WiFiRTT/");
        File[] files = d.listFiles();
        List<String> fileNames = new ArrayList<>();
        for (File file : files) {
            fileNames.add(file.getName());
        }
        if(fileNames.isEmpty()){
            fileNames.add("There is no file stored");
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FileAdapter(fileNames));
        */


    }

}
