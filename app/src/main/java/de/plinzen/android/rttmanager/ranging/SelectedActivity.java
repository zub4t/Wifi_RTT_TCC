package de.plinzen.android.rttmanager.ranging;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.rtt.RangingResult;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.plinzen.android.rttmanager.Log.FileUtil;
import de.plinzen.android.rttmanager.R;
import de.plinzen.android.rttmanager.wifi.MainActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SelectedActivity extends AppCompatActivity {

    private static final String EXTRA_WIFI_NETWORK = "WIFI_NETWORK";

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 200;
    @BindView(R.id.logView)
    TextView logView;
    @BindView(R.id.startButton)
    Button startButton;
    @BindView(R.id.stopButton)
    Button stopButton;
    @BindView(R.id.checkpoint)
    FloatingActionButton checkpoint;
    private Set<Disposable> rangingDisposables = new HashSet<>();
    private RttRangingManager rangingManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected);
        ButterKnife.bind(this);
        rangingManager = new RttRangingManager(getApplicationContext());
        initUI();
        FileUtil fileUtil = new FileUtil();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopRanging();
    }

    private String buildLogString(final RangingResult result) {
        String resultString = getString(R.string.log, result.getMacAddress(), result.getRangingTimestampMillis(), result.getRssi(), result
                .getDistanceMm(), logView.getText()
                .toString());
        if (resultString.length() > 5000) {
            return resultString.substring(0, 5000);
        }
        return resultString;
    }


    private void initStartButtonListener() {
        startButton.setOnClickListener(view -> onStartButtonClicked());

    }

    private void initStopButtonListener() {
        stopButton.setOnClickListener(view -> stopRanging());

    }
    private void initCheckPointButtonListener() {
        checkpoint.setOnClickListener(view -> logCheckPoint());

    }

    private void initUI() {
        setTitle("Log Start");
        initStartButtonListener();
        initStopButtonListener();
        initCheckPointButtonListener();
    }

    private void onStartButtonClicked() {
        FileUtil.TAG = FileUtil.getNextExpFileName();
        logView.setText("");
        for (ScanResult scanResult : MainActivity.set.values()) {
            rangingDisposables.add(rangingManager.startRanging(scanResult)
                    .repeat()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::writeOutput,
                            throwable -> {
                                Timber.e(throwable, "An unexpected error occurred while start ranging.");
                                Snackbar.make(logView, throwable.getMessage(), Snackbar.LENGTH_LONG).show();
                            }));
        }
    }


    private void stopRanging() {
        if (rangingDisposables == null) {
            return;
        }
        for(Disposable rangingDisposable : rangingDisposables){
            if (rangingDisposable != null)
                rangingDisposable.dispose();

        }
    }
    private void logCheckPoint() {

        long currentTimeStamp = System.currentTimeMillis();
        String aux = String.format("%s",currentTimeStamp);
        FileUtil.writeStringToFile("CHECKPOINT_"+FileUtil.TAG,aux);

    }
    private void writeOutput(@NonNull final List<RangingResult> result) {
        if (result.isEmpty()) {
            Timber.d("EMPTY ranging result received.");
            return;
        }
        for (RangingResult res : result) {
            if (res.getStatus() == RangingResult.STATUS_SUCCESS) {
                logView.setText(buildLogString(res));
                double d = res.getDistanceMm()/1000.0;
                double dd = res.getDistanceStdDevMm()/1000.0;
                long currentTimeStamp = System.currentTimeMillis();

                String aux = String.format("%s,%s,%s,%s,%s",currentTimeStamp,res.getMacAddress(),res.getRssi(),d,dd);
                FileUtil.writeStringToFile(FileUtil.TAG+".csv",aux);
                Timber.d("MAC: %s Result: %d RSSI: %d Distance: %d mm", res.getMacAddress(), res.getRangingTimestampMillis(), res.getRssi(), res
                        .getDistanceMm());
            }
        }
    }

}
