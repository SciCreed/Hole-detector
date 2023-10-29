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

public class AccelerometerActivity extends AppCompatActivity {

    // UI elements
    private TextView vx, vy, vz;

    // Sensor-related variables
    private SensorManager sensorManager;
    private List<Sensor> sensorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        vx = findViewById(R.id.vx);
        vy = findViewById(R.id.vy);
        vz = findViewById(R.id.vz);
        Button startButton = findViewById(R.id.button);
        Button nextActivityButton = findViewById(R.id.button2);

        // Initialize the SensorManager and get a list of available sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        // Set click listeners for buttons
        startButton.setOnClickListener(v -> startAccelerometer());

        nextActivityButton.setOnClickListener(v -> {
            stopAccelerometer();
            startActivity(new Intent(AccelerometerActivity.this, GyroscopesActivity.class));
        });
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
            float[] values = event.values;
            vx.setText(String.valueOf(values[0]));
            vy.setText(String.valueOf(values[1]));
            vz.setText(String.valueOf(values[2]));
        }
    };

    // Display a Toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
