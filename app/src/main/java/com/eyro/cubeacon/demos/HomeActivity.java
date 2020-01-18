package com.eyro.cubeacon.demos;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 100;
    TextView textOutput;
    ImageButton imageButton;

    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        textOutput = findViewById(R.id.txtVoice);
        imageButton = findViewById(R.id.image_button);

        textOutput.setVisibility(View.GONE);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
                try {
                    startActivityForResult(intent, REQUEST_CODE);
                } catch (ActivityNotFoundException a){
                    Toast.makeText(HomeActivity.this, "Sorry your device not supported", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE:{
                if (resultCode == RESULT_OK && null != data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textOutput.setVisibility(View.VISIBLE);

                    if (result != null) {
                        String hasil = String.valueOf(result.get(0));
                        textOutput.setText(hasil);

                        database = FirebaseDatabase.getInstance();
                        myRef = database.getReference();

                        myRef.child("tujuan").setValue(hasil);
                    }

                    Intent abc = new Intent(HomeActivity.this, RangingActivity.class);
                    startActivity(abc);
                }
                break;
            }
        }
    }
}
