package com.example.holedetector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class GravityActivity extends AppCompatActivity {

    // UI elements
    private TextView vx, vy, vz, GravityData;

    // Sensor-related variables
    private SensorManager sensorManager;
    private List<Sensor> sensorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gravity);

        // Initialize UI elements
        vx = findViewById(R.id.vx);
        vy = findViewById(R.id.vy);
        vz = findViewById(R.id.vz);
        GravityData = findViewById(R.id.gravityData);
        Button startButton = findViewById(R.id.button);
        Button nextActivityButton = findViewById(R.id.button2);

        // Initialize the SensorManager and get a list of available sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        // Display Gravity info
        displayGravityInfo();

        // Set click listeners for buttons
        startButton.setOnClickListener(v -> startGravity());

        nextActivityButton.setOnClickListener(v -> {
            stopGravity();
            //startActivity(new Intent(GravityActivity.this, GravityActivity.class));
        });
    }

    // Start the Gravity sensor
    private void startGravity() {
        if (sensorList.size() > 0) {
            Sensor gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            sensorManager.registerListener(GravityListener, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            showToast("Error: No Gravitys found.");
        }
    }

    // Stop the Gravity sensor
    private void stopGravity() {
        sensorManager.unregisterListener(GravityListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopGravity();
    }

    // SensorEventListener for Gravity data
    private final SensorEventListener GravityListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_GRAVITY) {
                handleGravity(sensorEvent);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Handle accuracy changes if needed
        }

        private void handleGravity(SensorEvent event) {
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

    // Display Gravity information
    private void displayGravityInfo() {
        if (sensorList.size() > 0) {
            Sensor gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            if (gravitySensor != null) {
                String info = "Gravity Name: " + gravitySensor.getName() + "\n"
                        + "Vendor: " + gravitySensor.getVendor() + "\n"
                        + "Version: " + gravitySensor.getVersion() + "\n"
                        + "Type: " + gravitySensor.getType() + "\n"
                        + "Resolution: " + gravitySensor.getResolution() + "\n"
                        + "Power: " + gravitySensor.getPower() + " mA\n"
                        + "Maximum Range: " + gravitySensor.getMaximumRange() + "\n";
                GravityData.setText(info);
            } else {
                showToast("Error: No Gravity sensor found.");
            }
        } else {
            showToast("Error: No sensors found.");
        }
    }

}
