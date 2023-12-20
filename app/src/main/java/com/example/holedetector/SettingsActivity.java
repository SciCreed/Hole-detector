package com.example.holedetector;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> finish());
    }
}