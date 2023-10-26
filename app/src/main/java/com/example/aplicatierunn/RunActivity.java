package com.example.aplicatierunn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class RunActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private List<LatLng> locatii = new ArrayList<>();
    LocationListener m_oLocationListener;
    LocationManager m_oLocationManager;
    private PolylineOptions m_oLineOptions;
    Handler handler = new Handler(); // Handler googleMaps
    private TextView timerTextView;

    private boolean locationInfoDialogShown = false;
    private boolean firstLocationUpdate = true;
    private boolean isTrackingStarted = false;
    boolean isTrackingPaused = false;

    private boolean isTimerRunning = false;
    private long totalTime = 60000; // Timpul total în milisecunde (60 de secunde)
    private long elapsedTime = 0;

    private SharedPreferences sharedPreferences;
    private RunData runData;

    //Pentru screenShot
    private View mapView; // Obiect pentru vizualizarea hărții
    private boolean capturingScreen = false;
    private long initialTotalTime = 60000; // Timpul total inițial în milisecunde (60 de secunde)


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        sharedPreferences = getSharedPreferences("RunData", MODE_PRIVATE);

        //Map location

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        // assert mapFragment !=null;

        m_oLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        m_oLineOptions = new PolylineOptions().width(15).color(Color.RED);
//
//        //ScreenShot
//        mapView = mapFragment.getView();

        initialTotalTime = totalTime;


        RunData runData = new RunData(new ArrayList<>(), new ArrayList<>());

        Button startLocationButton = findViewById(R.id.startButton);
        startLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTrackingStarted) {
                    startLocationUpdates();
//                    startTimer();
                    isTrackingStarted = true; // Tracking-ul este activat
                    startLocationButton.setText("Stop Session");
                } else {
                    isTrackingStarted = false;
                    startLocationButton.setText("Start Session");

                    // Adaugarea traseului si a timpului in obiectul RunData
                    runData.getRoute().addAll(locatii);
                    runData.getTimes().add(elapsedTime);
                    stopLocationUpdates(); //Oprirea urmaririi
                    stopTimer(); // Oprirea timerului

                    // Salvați traseul ca screenshot
//                    saveRouteScreenshot();

                    // Tramsformarea obiectuilui RunData in Gson
                    Gson gson = new Gson();
                    String runDataJson = gson.toJson(runData);

                    // Salvarea obiectul RunData în SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("runData", runDataJson);
                    editor.apply();


                }
            }
        });

        Button pauseResumeButton = findViewById(R.id.pauseResumeButton);
        pauseResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTrackingPaused) {
                    // Dacă trasarea este în pauză, reluați-o
                    startLocationUpdates();
//                    startTimer();
                    isTrackingPaused = false;
                    pauseResumeButton.setText("Pause");
                } else {
                    // Dacă trasarea este în curs de desfășurare, opriți temporar
                    stopLocationUpdatesP();
                    stopTimer();
                    isTrackingPaused = true;
                    pauseResumeButton.setText("Resume");
                }
            }
        });

        timerTextView = findViewById(R.id.chronometer);

    }


//    // Metoda pentru a crea și salva un screenshot al hărții
//    private void saveRouteScreenshot() {
//        if (mapView != null) {
//            mapView.setDrawingCacheEnabled(true);
//            Bitmap bitmap = Bitmap.createBitmap(mapView.getDrawingCache());
//            mapView.setDrawingCacheEnabled(false);
//
//            String screenshotFileName = "route_screenshot.png";
//            boolean success = Utils.saveBitmapToStorage(bitmap, screenshotFileName, this);
//
//            if (success) {
//                Toast.makeText(this, "Screenshot salvat cu succes!", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Eroare la salvarea screenshot-ului!", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
    // Clasa Utils pentru salvarea imaginilor
//    public class Utils {
//        public static boolean saveBitmapToStorage(Bitmap bitmap, String fileName, Context context) {
//            try {
//                File directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//                File file = new File(directory, fileName);
//                FileOutputStream fos = new FileOutputStream(file);
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                fos.close();
//                return true;
//            } catch (IOException e) {
//                e.printStackTrace();
//                return false;
//            }
//        }
//    }




    private void updateTimer() {

        elapsedTime += 1000;

        int totalMilliseconds = (int) elapsedTime;
        int milliseconds = (totalMilliseconds / 1000) % 1000;
        int seconds = (totalMilliseconds / 1000) % 60;
        int minutes = (totalMilliseconds / (1000 * 60)) % 60;

        String timerText = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        timerTextView.setText(timerText);
        handler.postDelayed(timerRunnable, 1000); // Actualizăm la fiecare milisecundă pentru precizie maximă
    }
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            updateTimer();
        }
    };
    long startT = SystemClock.elapsedRealtime();
    private void startTimer() {
        if (!isTimerRunning) {
            handler.postDelayed(timerRunnable, 100);
            isTimerRunning = true;
        }
    }
    // Adaugăm metoda pentru oprirea timerului
    private void stopTimer() {
        if (isTimerRunning) {
            handler.removeCallbacks(timerRunnable);
            isTimerRunning = false;
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RunActivity.this, new
                            String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
        mMap.setMyLocationEnabled(true);

        m_oLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Location location = new Location(LocationManager.GPS_PROVIDER);
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                showLocationInfoDialog(location);
                locationInfoDialogShown = true; // Setăm flag-ul pentru a indica că dialogul a fost afișat
            }
        });



        m_oLocationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                if (isTrackingStarted) {
                    startTimer();
                    LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                    locatii.add(loc); // Adăugăm locația în lista de locații
                    updateHarta(loc); // Actualizăm harta cu noua locație
                }

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }


        };

        m_oLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10L, 1F, m_oLocationListener);
    }
    private void showLocationInfoDialog(Location location) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Informații GPS");

        long locationTime = location.getTime();
        String formattedTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(locationTime));

        DecimalFormat speedFormat = new DecimalFormat("0.00");

        //Vreme
        String cityName = "Pitesti";
        String apiKey = "b2bfdbfb37921ca960945fd78eda1448";
        String TemperatureInfo = Temperature.getTemperature(cityName, apiKey);

        String dialogMessage = "Longitudine: " + location.getLongitude() + "\n"
                + "Latitudine: " + location.getLatitude() + "\n"
                + "Ora: " + formattedTime + "\n"
                + "Viteză: " + speedFormat.format(location.getSpeed()) + " m/s"+ "\n"
                + "Vremea: " + TemperatureInfo;

        dialogBuilder.setMessage(dialogMessage);

        dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
            locationInfoDialogShown = false; // Resetează flag-ul la apasarea butonului "OK"
            dialog.dismiss();
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }



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

    private void updateHarta(LatLng locatieNoua) {
        if (mMap == null) return;
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(locatieNoua).title("Locatia meaa"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locatieNoua, 16.0f));

        // Desenăm polilinia cu toate locațiile din lista
        PolylineOptions polylineOptions = new PolylineOptions()
                .width(15)
                .color(Color.RED)
                .addAll(locatii);
        mMap.addPolyline(polylineOptions);
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            m_oLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0F, m_oLocationListener);
        } else {
            // Solicită din nou permisiunea de acces la locație
            ActivityCompat.requestPermissions(RunActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
    }

    private void stopLocationUpdatesP() {

        m_oLocationManager.removeUpdates(m_oLocationListener);
    }

    private void stopLocationUpdates(){
        m_oLocationManager.removeUpdates(m_oLocationListener);
       if (isTimerRunning) {
            handler.removeCallbacks(timerRunnable);
            isTimerRunning = false;
            timerTextView.setText("00:00"); // Setăm textul timerului la "00:00"
        }
        totalTime = initialTotalTime; // Resetăm timpul total
        elapsedTime = 0; // Resetăm timpul scurs
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates(); // Începe urmărirea locației dacă permisiunea este acordată
            }
        }
    }

}