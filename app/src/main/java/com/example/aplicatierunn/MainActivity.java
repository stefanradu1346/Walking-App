package com.example.aplicatierunn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.os.AsyncTask;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity{


    private GoogleMap mMap;
    private List<LatLng> locatii = new ArrayList<>();
    LocationListener m_oLocationListener;
    LocationManager m_oLocationManager;
    private PolylineOptions m_oLineOptions;
    Handler handler = new Handler(); // Handler googleMaps

    private Random m_oRandom;

    private TextView weatherTextView;
    private Button startButton;
    private Button previousSessionsButton; // Butonul pentru sesiunile anterioare
    private Location lastLocation;
    private long lastLocationTime;
    private TextView speedTextView;
    private boolean locationInfoDialogShown = false;
    private boolean firstLocationUpdate = true;
    private boolean isTrackingStarted = false;
    boolean isTrackingPaused = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Weather
        //weatherTextView = findViewById(R.id.weatherTextView);

//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);

//        String cityName = "Pitesti";
//        String apiKey = "b2bfdbfb37921ca960945fd78eda1448";
//
//        String TemperatureInfo = Temperature.getTemperature(cityName, apiKey);
//        //weatherTextView.setText(TemperatureInfo);


        Button startLocationButton = findViewById(R.id.startButton);
        startLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTrackingStarted) {
                    Intent intent = new Intent(MainActivity.this, RunActivity.class);
                    startActivity(intent);
                }
            }
        });

        Button previousSessionsButton = findViewById(R.id.previousSessionsButton);
        previousSessionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SaveData.class);
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}



