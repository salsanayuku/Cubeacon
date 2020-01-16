package com.eyro.cubeacon.demos;

import android.os.RemoteException;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.eyro.cubeacon.CBBeacon;
import com.eyro.cubeacon.CBMonitoringListener;
import com.eyro.cubeacon.CBRegion;
import com.eyro.cubeacon.CBServiceListener;
import com.eyro.cubeacon.Cubeacon;
import com.eyro.cubeacon.MonitoringState;
import com.eyro.cubeacon.SystemRequirementManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class MonitoringActivity extends AppCompatActivity implements CBServiceListener, CBMonitoringListener {

    private static final String TAG = MonitoringActivity.class.getSimpleName();
    public static final String INTENT_BEACON = "IntentBeacon";

    private TextView textState;

    private Cubeacon cubeacon;
    private CBBeacon beacon;

    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);

        // get beacon parcelable from intent activity
        beacon = getIntent().getParcelableExtra(INTENT_BEACON);

        // assign view
        textState = findViewById(R.id.state);
        TextView textRegion = findViewById(R.id.region);

        float akurasi = (float) beacon.getAccuracy();
        int pembulatan = Math.round(akurasi);

        int langkah = (pembulatan * 100) / 50; //set to centimeter

        // set default value of region text
        String region = String.format(Locale.getDefault(), "\nUUID: %s\nMajor: %d - Minor : %d\nRange : %d meter\nJalan : %d langkah\nNama : %s",
                beacon.getProximityUUID().toString().toUpperCase(), beacon.getMajor(), beacon.getMinor(), pembulatan, langkah, beacon.getName());
        textRegion.setText(getString(R.string.label_region, region));

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        myRef.child("beacon/range").setValue(pembulatan);

        // assign local instance of Cubeacon manager
        cubeacon = Cubeacon.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // check all requirement like is BLE available, is bluetooth on/off,
        // location service for Android API 23 or later
        if (SystemRequirementManager.checkAllRequirementUsingDefaultDialog(this)) {
            // connecting to Cubeacon service when all requirements completed
            cubeacon.connect(this);
            // disable background mode, because we're going to use full
            // scanning resource in foreground mode
            cubeacon.setBackgroundMode(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // enable background mode when this activity paused
        cubeacon.setBackgroundMode(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // disconnect from Cubeacon service when this activity destroyed
        cubeacon.disconnect(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        // add monitoring listener implementation
        cubeacon.addMonitoringListener(this);
        try {
            // create a new region for monitoring beacon
            CBRegion region = new CBRegion("com.eyro.cubeacon.monitoring_region",
                    beacon.getProximityUUID(), beacon.getMajor(), beacon.getMinor());
            // start monitoring beacon using region
            cubeacon.startMonitoringForRegion(region);
        } catch (RemoteException e) {
            Log.e(TAG, "Error while start monitoring beacon, " + e);
        }
    }

    @Override
    public void didEnterRegion(CBRegion cbRegion) {
        // add code when entering region beacon
    }

    @Override
    public void didExitRegion(CBRegion cbRegion) {
        // add code when exiting region beacon
    }

    @Override
    public void didDetermineStateForRegion(final MonitoringState state, CBRegion cbRegion) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (state) {
                    case INSIDE:
                        textState.setText(getString(R.string.label_state, "ENTER REGION"));
                        break;
                    case OUTSIDE:
                        textState.setText(getString(R.string.label_state, "EXIT REGION"));
                        break;
                }
            }
        });
    }
}
