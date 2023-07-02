package com.weather;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherApp {

    private static final String API_KEY = "0e357fdf8d7d276042e63c09622b53dd";

    public static void main(String[] args) {
        try {
            String location = getUserInput("Enter a location: ");
            JSONObject coordinates = getCoordinates(location);
            double latitude = coordinates.getDouble("lat");
            double longitude = coordinates.getDouble("lon");

            // Fetch current weather data
            String currentWeatherData = getWeatherData(latitude, longitude);
            JSONObject currentWeatherJson = new JSONObject(currentWeatherData);
            displayCurrentWeather(currentWeatherJson);

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private static String getUserInput(String message) throws Exception {
        System.out.print(message);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }

    private static JSONObject getCoordinates(String location) throws Exception {
        String apiUrl = "https://api.openweathermap.org/geo/1.0/direct?q=" + location + "&limit=1&appid=" + API_KEY;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        connection.disconnect();

        JSONArray resultArray = new JSONArray(response.toString());
        if (resultArray.length() > 0) {
            JSONObject locationObject = resultArray.getJSONObject(0);
            double latitude = locationObject.getDouble("lat");
            double longitude = locationObject.getDouble("lon");
            JSONObject coordinates = new JSONObject();
            coordinates.put("lat", latitude);
            coordinates.put("lon", longitude);
            return coordinates;
        }

        throw new Exception("Unable to fetch coordinates for the location: " + location);
    }

    private static String getWeatherData(double latitude, double longitude) throws Exception {
        String apiUrl = "http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude
                + "&appid=" + API_KEY + "&units=metric";
        @SuppressWarnings("deprecation")
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        connection.disconnect();

        return response.toString();
    }

    private static void displayCurrentWeather(JSONObject currentWeatherJson) {
        String cityName = currentWeatherJson.getString("name");
        JSONObject main = currentWeatherJson.getJSONObject("main");
        double temperature = main.getDouble("temp");
        int humidity = main.getInt("humidity");
        JSONObject wind = currentWeatherJson.getJSONObject("wind");
        double windSpeed = wind.getDouble("speed");
        JSONArray weatherArray = currentWeatherJson.getJSONArray("weather");
        String weatherDescription = weatherArray.getJSONObject(0).getString("description");

        System.out.println("Current weather in " + cityName + ":");
        System.out.println("Temperature: " + temperature + "°C");
        System.out.println("Humidity: " + humidity + "%");
        System.out.println("Wind Speed: " + windSpeed + " m/s");
        System.out.println("Description: " + weatherDescription);
    }
}
