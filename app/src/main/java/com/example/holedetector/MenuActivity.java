package com.example.holedetector;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Find buttons by their IDs
        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);

        // Set click listeners for the buttons
        button1.setOnClickListener(v -> {
            // Create an Intent to open MapActivity
            Intent intent = new Intent(MenuActivity.this, MapActivity.class);
            startActivity(intent);
        });

        button2.setOnClickListener(v -> {
            // Create an Intent to open SettingsActivity
            Intent intent = new Intent(MenuActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // Add more listeners or logic for other UI elements as needed
    }
}
