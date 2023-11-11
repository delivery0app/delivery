package com.factglobal.delivery.util.common;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@Component
public class DistanceCalculator {
    private static final double EARTH_RADIUS = 6371000; // Радиус Земли в метрах

    public int getDistance(String city1, String city2) throws IOException{
        HashMap<String, Double> coordinateCity1 = getCoordinate(city1);
        double city1Lat = coordinateCity1 != null ? coordinateCity1.get("cityLat") : null;
        double city1Lon = coordinateCity1 != null ? coordinateCity1.get("cityLon") : null;

        HashMap<String, Double> coordinateCity2 = getCoordinate(city2);
        double city2Lat = coordinateCity2 != null ? coordinateCity2.get("cityLat") : null;
        double city2Lon = coordinateCity2 != null ? coordinateCity2.get("cityLon") : null;

        return (int) Math.round(calculateDistance(city1Lat, city1Lon, city2Lat, city2Lon));
    }

    //Получение координат города
    private HashMap<String, Double> getCoordinate(String city) throws IOException{
        String query = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String url = "https://nominatim.openstreetmap.org/search?q=" + query + "&format=json";
        URL obj1 = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj1.openConnection();
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();

        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Gson gson = new Gson();
            JsonArray jsonArray = gson.fromJson(response.toString(), JsonArray.class);
            JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
            Double cityLat = jsonObject.get("lat").getAsDouble();
            Double cityLon = jsonObject.get("lon").getAsDouble();

            HashMap<String, Double> coordinate = new HashMap<>();
            coordinate.put("cityLat", cityLat);
            coordinate.put("cityLon", cityLon);

            return coordinate;
        }
        return null;
    }


    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Переводим координаты городов из градусов в радианы
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Разница между долготами и широтами городов
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Формула гаверсинусов
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Вычисляем расстояние в метрах
        double distance = EARTH_RADIUS * c;
        // Отправляем расстояние в киллометрах
        return distance / 1000;
    }
}

