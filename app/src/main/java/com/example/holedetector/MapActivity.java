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
import android.util.Log;

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

    private MapView map;
    private SQLiteDatabase database;
    private List<Marker> markers = new ArrayList<>();

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initializeMap();
        initializeLocationOverlay();
        initializeDatabase();
        addMarkers();
        loadMarkersOnMap();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
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
        MyLocationNewOverlay myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
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
    }

    private void loadMarkersOnMap() {
        database = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_MARKERS, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                float x = cursor.getFloat(cursor.getColumnIndex(COLUMN_X));
                float y = cursor.getFloat(cursor.getColumnIndex(COLUMN_Y));

                GeoPoint point = new GeoPoint(x, y);
                Marker marker = new Marker(map);
                marker.setPosition(point);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                markers.add(marker);

            } while (cursor.moveToNext());
            cursor.close();
        }
        database.close();

        for (Marker marker : markers) {
            map.getOverlays().add(marker);
        }
    }
}