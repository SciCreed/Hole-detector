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

    private final String DBNAME = "Bazunia.db"; // Name for the new database
    private SQLiteDatabase baza = null;
    private List<Marker> markers = new ArrayList<>();

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Context ctx = getApplicationContext();
        // Important! Set your user agent to prevent getting banned from the OSM servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        MapView map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(20);
        GeoPoint startPoint = new GeoPoint(53.2544, 14.3310);
        mapController.setCenter(startPoint);

        // Enable the location overlay
        MyLocationNewOverlay myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
        map.getOverlays().add(myLocationOverlay);

        // Add markers
        try {
            baza = this.openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
            baza.execSQL("CREATE TABLE IF NOT EXISTS Markers (x FLOAT, y FLOAT)");

            dodaj(53.2544f, 14.3310f);
            dodaj(22.78f, 44.32f);
            dodaj(55.225f, 64.21f);

            String sx, sy;

            Cursor cursor = baza.rawQuery("SELECT * FROM Markers", null);
            if (cursor.moveToFirst()) {
                do {
                    sx = cursor.getString(cursor.getColumnIndex("x"));
                    sy = cursor.getString(cursor.getColumnIndex("y"));
                    float x = Float.parseFloat(sx);
                    float y = Float.parseFloat(sy);

                    GeoPoint pkt = new GeoPoint(x, y);
                    Marker m = new Marker(map);
                    m.setPosition(pkt);
                    m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    markers.add(m);

                } while (cursor.moveToNext());
                cursor.close();
            }

            baza.close();
        } catch (SQLiteException e) {
            Log.e(getClass().getSimpleName(), "Could not create or open the database");
        }

        for (Marker m : markers) {
            map.getOverlays().add(m);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }

    private void dodaj(float x, float y) {
        ContentValues vals = new ContentValues();
        vals.put("x", x);
        vals.put("y", y);
        baza.insert("Markers", null, vals);
    }
}