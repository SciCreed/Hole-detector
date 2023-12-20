package com.example.holedetector;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    private static final String DB_NAME = "Bazunia.db";
    private static final String TABLE_MARKERS = "Markers";
    private static final String COLUMN_X = "x";
    private static final String COLUMN_Y = "y";

    private View loadingIndicator;
    private MapView map;
    private MyLocationNewOverlay myLocationOverlay;
    private SQLiteDatabase database;
    private List<Marker> markers = new ArrayList<>();

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initializeLoadingIndicator();
        initializeMap();
        initializeLocationOverlay();
        initializeDatabase();
        loadMarkersOnMap();
        addMarkers();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }

    private void initializeLoadingIndicator() {
        loadingIndicator = getLayoutInflater().inflate(R.layout.loading_indicator, null);
        FrameLayout rootLayout = findViewById(android.R.id.content);
        rootLayout.addView(loadingIndicator);
        loadingIndicator.setVisibility(View.VISIBLE);

        map = findViewById(R.id.map);
        // Wait for the map and location to be fully rendered
        map.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (myLocationOverlay.getMyLocation() != null && map.getBoundingBox().contains(myLocationOverlay.getMyLocation())) {
                // Hide the loading indicator
                loadingIndicator.setVisibility(View.GONE);
            }
        });
    }

    private void initializeMap() {
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(20);
    }

    private void initializeLocationOverlay() {
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
        map.getOverlays().add(myLocationOverlay);
    }

    private void initializeDatabase() {
        try {
            database = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_MARKERS + " (" + COLUMN_X + " FLOAT, " + COLUMN_Y + " FLOAT)");
        } catch (SQLiteException e) {
            Log.e(getClass().getSimpleName(), "Could not create or open the database");
        }
    }

    private void addMarkers() {
        database = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        addMarker(53.2544f, 14.3310f);
        addMarker(53.2544f, 14.3311f);
        addMarker(53.2544f, 14.3312f);
        database.close();
    }

    private void addMarker(float x, float y) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_X, x);
        values.put(COLUMN_Y, y);
        database.insert(TABLE_MARKERS, null, values);
        renderMarker(x, y);
    }

    private void renderMarker(float x, float y) {
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(x, y));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        markers.add(marker);
    }

    private void loadMarkersOnMap() {
        database = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_MARKERS, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                float x = cursor.getFloat(cursor.getColumnIndex(COLUMN_X));
                float y = cursor.getFloat(cursor.getColumnIndex(COLUMN_Y));
                renderMarker(x, y);
            } while (cursor.moveToNext());
            cursor.close();
        }
        database.close();

        for (Marker marker : markers) {
            map.getOverlays().add(marker);
        }
    }
}