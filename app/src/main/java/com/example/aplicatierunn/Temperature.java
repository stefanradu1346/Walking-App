package com.example.aplicatierunn;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Temperature {

    public static String getTemperature(String cityName, String apiKey) {
        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder data = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                data.append(line);
            }

            reader.close();

            JSONObject jsonObject = new JSONObject(data.toString());
            JSONObject main = jsonObject.getJSONObject("main");
            double temperatureKelvin = main.getDouble("temp");

            // Conversia din Kelvin în Celsius cu o singură zecimală
            double temperatureCelsius = temperatureKelvin - 273.15;
            String formattedTemperature = String.format("%.1f", temperatureCelsius);

            return  formattedTemperature + "°C";
        } catch (Exception e) {
            e.printStackTrace();
            return "Eroare la obținerea temperaturii";
        }
    }
}

