package com.example.aplicatierunn;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SaveData extends AppCompatActivity {

    private SQLiteDatabase database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_data);

        // Inițializați baza de date
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();


        SharedPreferences sharedPreferences = getSharedPreferences("RunData", MODE_PRIVATE);

        String runDataJson = sharedPreferences.getString("runData", "");

        Gson gson = new Gson();
        RunData runData = gson.fromJson(runDataJson, RunData.class);

        if (runData != null) {
            // Afișare traseu
            List<LatLng> route = runData.getRoute();
            TextView routeTextView = findViewById(R.id.routeTextView);
            StringBuilder routeText = new StringBuilder("Traseul:\n");

            // Filtrăm coordonatele duplicate
            Set<LatLng> uniqueLocations = new HashSet<>(route);

            for (LatLng location : uniqueLocations) {
                routeText.append("Latitudine: ").append(location.latitude).append(", Longitudine: ").append(location.longitude).append("\n");
            }
            routeTextView.setText(routeText.toString());

            // Afisare timp
            List<Long> times = runData.getTimes();
            TextView timeTextView = findViewById(R.id.timeTextView);
            StringBuilder timeText = new StringBuilder("Timpi:\n");
            for (Long time : times) {
                long minutes = time / 60000;
                long seconds = (time % 60000) / 1000;
                if (minutes == 1) {
                    timeText.append(minutes).append(" minut și ").append(seconds).append(" secunde\n");
                } else {
                    timeText.append(minutes).append(" minute și ").append(seconds).append(" secunde\n");
                }
            }
            timeTextView.setText(timeText.toString());
        }
    }
}