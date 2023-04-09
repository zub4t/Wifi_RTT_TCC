package de.plinzen.android.rttmanager.Log;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil {
    public static  String TAG = "";
    public FileUtil(){
        TAG = getNextExpFileName();
    }
    public static void writeStringToFile(String fileName, String content) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.e(TAG, "External storage not available for writing.");
            return;
        }

        File publicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(publicDir, fileName);

        try (FileWriter fileWriter = new FileWriter(file, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            bufferedWriter.write(content);
            bufferedWriter.newLine();

        } catch (IOException e) {
            Log.e(TAG, "Error writing to file", e);
        }
    }

    public static String getNextExpFileName() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.e(TAG, "External storage not available for reading.");
            return null;
        }

        File publicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

        Pattern expFilePattern = Pattern.compile("^EXP_(\\d+)");
        int maxNumber = -1;

        for (File file : publicDir.listFiles()) {
            Matcher matcher = expFilePattern.matcher(file.getName());
            if (matcher.find()) {
                int currentNumber = Integer.parseInt(matcher.group(1));
                maxNumber = Math.max(maxNumber, currentNumber);
            }
        }

        return "EXP_" + (maxNumber + 1);
    }
}
