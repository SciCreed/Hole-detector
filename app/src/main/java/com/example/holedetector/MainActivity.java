package com.example.holedetector;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // UI elements
    private TextView tvJumps;

    // Sensor-related variables
    private SensorManager sensorManager;
    private List<Sensor> sensorList;
    int sensitivity = 50;
    boolean inAir=false;
    boolean freeFall=false;
    boolean rebound=false;
    int jumpCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        tvJumps = findViewById(R.id.tvJumps);
        tvJumps.setText("0");

        // Initialize the SensorManager and get a list of available sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        startAccelerometer();
    }

    // Start the accelerometer sensor
    private void startAccelerometer() {
        if (sensorList.size() > 0) {
            Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            showToast("Error: No accelerometers found.");
        }
    }

    // Stop the accelerometer sensor
    private void stopAccelerometer() {
        sensorManager.unregisterListener(accelerometerListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopAccelerometer();
    }

    // SensorEventListener for accelerometer data
    private final SensorEventListener accelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                handleAccelerometer(sensorEvent);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Handle accuracy changes if needed
        }

        private void handleAccelerometer(SensorEvent event) {
            
            if(event.values[1]>(15-(sensitivity-50)/10)){
                inAir=true;
            }
            if(inAir&&event.values[1]<(5+(sensitivity-50)/10)){
                freeFall=true;
            }
            if(freeFall&&event.values[1]>(13-(sensitivity-50)/10)){
                rebound=true;
            }
            if(rebound&&event.values[1]<(10+(sensitivity-50)/10)&&event.values[1]>(1-(sensitivity-50)/10))
            {
                jumpCount++;
                inAir=false;
                freeFall=false;
                rebound=false;
            }   

            tvJumps.setText(Integer.toString(jumpCount));
        }
    };

    // Display a Toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}