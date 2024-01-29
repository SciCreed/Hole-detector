package com.example.holedetector;

import static android.location.LocationManager.GPS_PROVIDER;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.ShakeDetector;
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
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;
    private MapView map;
    private MyLocationNewOverlay myLocationOverlay;
    private SQLiteDatabase database;

    private List<Marker> markerList = new ArrayList<Marker>();

    private final String DB_NAME = "dbbbbd.db";
    private final String TABLE_MARKERS = "Markers";
    private final String COLUMN_X = "x";
    private final String COLUMN_Y = "y";

    @SuppressLint({"Range", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        map = findViewById(R.id.map);

        initializeLoadingIndicator();
        initializeMap();
        initializeLocationOverlay();
        initializeDatabase();
        loadMarkers();
        addMarkers();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> finish());

        Sensey.getInstance().init(this,Sensey.SAMPLING_PERIOD_GAME);
        ShakeDetector.ShakeListener shakeListener=new ShakeDetector.ShakeListener() {
            @Override public void onShakeDetected() {
                //
            }

            @Override public void onShakeStopped() {
                addMarker((float) myLocationOverlay.getMyLocation().getLatitude(), (float) myLocationOverlay.getMyLocation().getLongitude());
            }
        };
        Sensey.getInstance().startShakeDetection(5,50,shakeListener);


        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Marker m = findNearestMarker();
                double distance = myLocationOverlay.getMyLocation().distanceToAsDouble(m.getPosition());
                if(distance < 10){
                    printAlert();
                }
            }
        };
        locationManager.requestLocationUpdates(GPS_PROVIDER, 10, 5, listener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null) {
            database.close();
        }
        Sensey.getInstance().stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }

    private void initializeLoadingIndicator() {
        View loadingIndicator = getLayoutInflater().inflate(R.layout.loading_indicator, null);
        FrameLayout rootLayout = findViewById(android.R.id.content);
        rootLayout.addView(loadingIndicator);
        loadingIndicator.setVisibility(View.VISIBLE);

        // Wait for the map and location to be fully rendered
        globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (myLocationOverlay.getMyLocation() != null && map.getBoundingBox().contains(myLocationOverlay.getMyLocation())) {
                    // Hide the loading indicator
                    loadingIndicator.setVisibility(View.GONE);
                    // Unregister the listener
                    map.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        };
        map.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }

    private void initializeMap() {
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

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

    private void loadMarkers() {
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_MARKERS, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndexX = cursor.getColumnIndex(COLUMN_X);
            int columnIndexY = cursor.getColumnIndex(COLUMN_Y);
            if (columnIndexX >= 0 && columnIndexY >= 0) {
                do {
                    float x = cursor.getFloat(columnIndexX);
                    float y = cursor.getFloat(columnIndexY);
                    loadMarker(x, y);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    private void loadMarker(float x, float y) {
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(x, y));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(marker);
        markerList.add(marker);
    }

    private void addMarkers() {
        addMarker(53.2544f, 14.3310f);
        addMarker(53.2544f, 14.3311f);
        addMarker(53.2544f, 14.3312f);
    }

    private void addMarker(float x, float y) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_X, x);
        values.put(COLUMN_Y, y);
        database.insert(TABLE_MARKERS, null, values);
        loadMarker(x, y);
    }

    private Marker findNearestMarker(){
        if(!markerList.isEmpty()){
            GeoPoint p = myLocationOverlay.getMyLocation();
            double d = 0;
            Marker sel = null;
            for(Marker m : markerList) {
                if (sel == null) {
                    sel = m;
                    d = p.distanceToAsDouble(m.getPosition());
                } else {
                    double dist = p.distanceToAsDouble(m.getPosition());
                    if (d > dist){
                        sel = m;
                        d = dist;
                    }
                }
            }
            return sel;
        }
        return null;
    }


    private void printAlert(){
        AlertDialog alertDialog = new AlertDialog.Builder(this)
            //set icon
            .setIcon(android.R.drawable.ic_dialog_alert)
            //set title
            .setTitle("HOLE NEARBY")
            //set message
            .setMessage("Get ur penis ready")
            //set positive button
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //set what would happen when positive button is clicked

                }
            })
        .show();
    }

}