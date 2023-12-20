package com.example.holedetector;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button1;
    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if (v.getId() == R.id.button1) {
            intent = new Intent(MenuActivity.this, MapActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.button2) {
            intent = new Intent(MenuActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
    }
}