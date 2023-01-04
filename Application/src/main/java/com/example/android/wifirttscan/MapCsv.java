package com.example.android.wifirttscan;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MapCsv {
    private Map<String, Object> map;

    public MapCsv(Map<String, Object> map) {
        this.map = map;
    }

    public String toCsv() {


        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Log.d("DATA",entry.getKey());

            sb.append(entry.getKey()).append(",").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }


    public static MapCsv fromCsv(String csv) {
        Map<String, Object> map = new HashMap<>();
        String[] lines = csv.split("\n");
        for (String line : lines) {
            String[] parts = line.split(",");
            map.put(parts[0], parts[1]);
        }
        return new MapCsv(map);
    }

    public String readFileFromExternalStorage(Context ctx, String name) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "file.csv");

        if (file.exists()) {
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            try {
                FileInputStream fis = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                fis.read(data);
                fis.close();
                String csvData = new String(data, StandardCharsets.UTF_8);
                return csvData;
            } catch (Exception e) {
                e.printStackTrace();
            }

            // do something with the CSV data
        } else {
            Log.e("MapCsv", "File do not exist, file name: " + name);
        }
        return "";

    }

    public void writeFileExternalStorage(Context ctx) {


        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy");
        String formattedDateTime = now.format(formatter);
        //Checking the availability state of the External Storage.
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {

            //If it isn't mounted - we can't write into it.
            return;
        }
        //Create a new file that points to the root directory, with the given name:
        File file = new File(Environment.getExternalStorageDirectory(), String.format("/WiFiRTT/data_%s_%s.csv", formattedDateTime, MainActivity.nextIDFile));
        MainActivity.currentFileName = file.getAbsolutePath();
        //This point and below is responsible for the write operation
        FileOutputStream outputStream = null;
        try {

            if(!file.exists()) {
                if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                   return;
                }
                file.createNewFile();
            }
            //second argument of FileOutputStream constructor indicates whether
            //to append or create new file if one exists
            outputStream = new FileOutputStream(file, true);

            outputStream.write(toCsv().getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}

