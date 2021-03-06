package com.byteshaft.kidmonitor;

import android.support.v7.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    LocationService mLocationService;
    private VideoRecorder videoRecorder;
    Helpers mHelpers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHelpers = new Helpers(getApplicationContext());
        Button locationButton = (Button) findViewById(R.id.button_location);
        Button soundRec = (Button) findViewById(R.id.button_sound);
        Button videoButton = (Button) findViewById(R.id.button_video);
        soundRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioRecorder audioRecorder =  new AudioRecorder();
                String soundPath = AppGlobals.getDataDirectory("soundrec") + "/" + mHelpers.getTimeStamp()+".mp3";
                audioRecorder.setOutputFile(soundPath);
                audioRecorder.record(5000);

            }
        });
        videoRecorder = new VideoRecorder();
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!VideoRecorder.isRecording()) {
                    videoRecorder.start();
                    Toast.makeText(getApplicationContext(), "Start", Toast.LENGTH_SHORT).show();
                } else if (VideoRecorder.isRecording()){
                    videoRecorder.stopRecording();
                    Toast.makeText(getApplicationContext(), "Stop", Toast.LENGTH_SHORT).show();
                }

            }
        });
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationService = new LocationService(getApplicationContext());
                if (!mHelpers.isAnyLocationServiceAvailable()) {
                    Log.i("Location", "GPS disabled");
                    /* TODO: Implement Response */
                } else {
                mLocationService.connectingGoogleApiClient();
                mLocationService.locationTimer().start();
                }
            }
        });
        startService(new Intent(this, CallListenerService.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationService != null && mLocationService.mGoogleApiClient.isConnected()) {
            mLocationService.stopLocationService();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLocationService != null && mLocationService.mGoogleApiClient.isConnected()) {
            mLocationService.stopLocationService();
        }
    }
}