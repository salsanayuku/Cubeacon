package com.eyro.cubeacon.demos;

import android.content.Context;
import android.os.Build;
import android.os.RemoteException;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.eyro.cubeacon.CBBeacon;
import com.eyro.cubeacon.CBMonitoringListener;
import com.eyro.cubeacon.CBRegion;
import com.eyro.cubeacon.CBServiceListener;
import com.eyro.cubeacon.Cubeacon;
import com.eyro.cubeacon.MonitoringState;
import com.eyro.cubeacon.SystemRequirementManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MonitoringActivity extends AppCompatActivity implements CBServiceListener, CBMonitoringListener {

    private static final String TAG = MonitoringActivity.class.getSimpleName();
    public static final String INTENT_BEACON = "IntentBeacon";

    private TextView textState;

    private Cubeacon cubeacon;
    private CBBeacon beacon;

    FirebaseDatabase database;
    DatabaseReference myRef;

    TextToSpeech textVoice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);

        //Set Vibrator And Speech
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        textVoice = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = textVoice.setLanguage(new Locale("id", "ID"));

                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");
                } else {
                    Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String sensorL = dataSnapshot.child("sensor_left").getValue(String.class);
                String sensorR = dataSnapshot.child("sensor_right").getValue(String.class);

                Integer sensor_Lint = Integer.parseInt(sensorL);
                Integer sensor_Rint = Integer.parseInt(sensorR);

                if (sensor_Lint <= 50){
                    String textLeft = "Ada Benda, Geser Ke Kanan Sedikit";

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        textVoice.speak(textLeft, TextToSpeech.QUEUE_FLUSH, null, null);
                    }else{
                        textVoice.speak(textLeft, TextToSpeech.QUEUE_FLUSH, null);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(4000, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        vibrator.vibrate(4000);
                    }

                }
                if(sensor_Rint <= 50){
                    String textRight = "Ada Benda, Geser Ke Kiri Sedikit";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        textVoice.speak(textRight, TextToSpeech.QUEUE_FLUSH, null, null);
                    }else{
                        textVoice.speak(textRight, TextToSpeech.QUEUE_FLUSH, null);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(4000, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        vibrator.vibrate(4000);
                    }
                }
                if(sensor_Lint <= 50 && sensor_Rint < 50) {
                    String textAll = "Ada Benda Di Depan Anda";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        textVoice.speak(textAll, TextToSpeech.QUEUE_FLUSH, null, null);
                    }else{
                        textVoice.speak(textAll, TextToSpeech.QUEUE_FLUSH, null);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(4000, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        vibrator.vibrate(4000);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // get beacon parcelable from intent activity
        beacon = getIntent().getParcelableExtra(INTENT_BEACON);

        // assign view
        textState = (TextView) findViewById(R.id.state);
        TextView textRegion = (TextView) findViewById(R.id.region);

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
        if (textVoice != null) {
            textVoice.stop();
            textVoice.shutdown();
        }
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
