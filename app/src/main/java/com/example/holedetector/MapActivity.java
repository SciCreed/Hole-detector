package com.example.holedetector;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;


public class MapActivity extends AppCompatActivity {

    private final String DBNAME = "Bazunia.db"; //Nazwa dla nowej bazy
    SQLiteDatabase baza = null;

    private List<Marker> markers = new ArrayList<Marker>();


    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_map);


        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(9);
        GeoPoint startPoint = new GeoPoint(53.2544, 14.3310);
        mapController.setCenter(startPoint);

        // Enable the location overlay
map.getOverlays().add(new MyLocationNewOverlay(new GpsMyLocationProvider(this), map));

// Set the map controller to your current location
MyLocationNewOverlay myLocationOverlay = (MyLocationNewOverlay) map.getOverlays().get(0);
myLocationOverlay.enableMyLocation();
myLocationOverlay.enableFollowLocation();


        // add markers
        try{
            baza = this.openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
            baza.execSQL("CREATE TABLE IF NOT EXISTS Markers (x FLOAT, y FLOAT)");

            dodaj(53.2544f, 14.3310f);
            dodaj(22.78f, 44.32f);
            dodaj(55.225f, 64.21f);

            String sx = null, sy = null;

            Cursor cursor = baza.rawQuery("SELECT * FROM Markers",null);
            if(cursor.moveToFirst()) { //Metoda zwraca FALSE jesli cursor jest pusty
                do {
                    //getString Returns the value of the requested column as a String.
                    //getColumnIndex Returns the zero-based index for the given column name,
                    //	or -1 if the column doesn't exist.

                    sx = cursor.getString(cursor.getColumnIndex("x"));
                    sy = cursor.getString(cursor.getColumnIndex("y"));
                    float x = Float.valueOf(sx), y = Float.valueOf(sy);

                    GeoPoint pkt = new GeoPoint(x, y);
                    Marker m = new Marker(map);
                    m.setPosition(pkt);
                    m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    markers.add(m);

                } while (cursor.moveToNext()); //Metoda zwraca FALSE wówczas gdy cursor przejdzie ostatni wpis
                cursor.close();
            }

            baza.close();
        }
        catch(SQLiteException e) {
            Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        }

        for (Marker m : markers) {
            map.getOverlays().add(m);
        }


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            //Close this activity
            finish();
        });
    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }


    private void dodaj(float x, float y){
        //baza.execSQL("INSERT INTO Markers Values(53.2544, 14.3310);");
        ContentValues vals = new ContentValues();
        vals.put("x", x);
        vals.put("y", y);
        baza.insert("Markers", null, vals);
    }

}