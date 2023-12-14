package com.example.holedetector;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find buttons by their IDs
        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);

        // Set click listeners for the buttons
        button1.setOnClickListener(v -> {
            // Handle button1 click
            // For example: launch a new activity, perform an action, etc.
        });

        button2.setOnClickListener(v -> {
            // Handle button2 click
            // For example: launch a new activity, perform an action, etc.
        });

        // Add more listeners or logic for other UI elements as needed
    }
}
