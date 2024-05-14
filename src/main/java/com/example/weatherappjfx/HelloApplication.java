package com.example.weatherappjfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.scene.input.KeyCode;
import javafx.geometry.Insets;
import javafx.scene.Cursor;

public class HelloApplication extends Application {

    private static final String API_KEY = "99f1f0cb2b36daaaaec4053e2cd0be88";
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s";

    private TextField cityInput;
    private Text errorDisplay;
    private VBox card;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Weather App");

        cityInput = new TextField();
        cityInput.setPromptText("Enter city");
        cityInput.setStyle("-fx-padding: 10px; -fx-font-size: 14px; -fx-border-radius: 4px; -fx-border-color: #ced4da; -fx-border-width: 1px;");

        cityInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                getWeather();
            }
        });

        Button getWeatherButton = new Button("Get Weather");
        getWeatherButton.setOnAction(e -> getWeather());
        getWeatherButton.setStyle("-fx-padding: 10px; -fx-font-size: 14px; -fx-background-color: #007bff; -fx-text-fill: white; -fx-border-radius: 4px; -fx-border-width: 0px;");

        card = new VBox();

        VBox root = new VBox();
        root.setStyle("-fx-padding: 20px; -fx-alignment: center; -fx-spacing: 10px;"); // VBox styling
        root.getChildren().addAll(cityInput, getWeatherButton, card);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    private void getWeather() {
        String city = cityInput.getText();
        if (city.isEmpty()) {
            displayError("Please enter a city");
            return;
        }

        try {
            String apiUrl = String.format(API_URL, city, API_KEY);
            String weatherData = fetchData(apiUrl);
            displayWeatherInfo(weatherData);
        } catch (IOException e) {
            displayError("Could not fetch weather data");
        }
    }

    private String fetchData(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        connection.disconnect();
        return response.toString();
    }

    private void displayWeatherInfo(String weatherData) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(weatherData);

            String city = node.get("name").asText();
            double temp = node.get("main").get("temp").asDouble() - 273.15;
            int humidity = node.get("main").get("humidity").asInt();
            String description = node.get("weather").get(0).get("description").asText();
            int weatherId = node.get("weather").get(0).get("id").asInt();

            card.getChildren().clear();

            Text cityDisplay = new Text("City: " + city);
            cityDisplay.setStyle("-fx-font-weight: bold;");
            Text tempDisplay = new Text("Temperature: " + String.format("%.1f°C", temp));
            tempDisplay.setStyle("-fx-font-weight: bold;");
            Text humidityDisplay = new Text("Humidity: " + humidity + "%");
            humidityDisplay.setStyle("-fx-font-weight: bold;");
            Text descDisplay = new Text("Description: " + description);
            descDisplay.setStyle("-fx-font-weight: bold;");
            Text weatherEmoji = new Text("Weather: " + getWeatherEmoji(weatherId));
            weatherEmoji.setStyle("-fx-font-weight: bold;");

            VBox cardContent = new VBox();
            cardContent.getChildren().addAll(cityDisplay, tempDisplay, humidityDisplay, descDisplay, weatherEmoji);
            cardContent.setStyle("-fx-padding: 10px; -fx-spacing: 5px;");

            card.getChildren().add(cardContent);
            card.setStyle("-fx-background-color: #f8f9fa; -fx-border-radius: 4px; -fx-padding: 20px;");

            // Close button
            Button closeButton = new Button("Close");
            closeButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-border-radius: 4px; -fx-padding: 5px 10px;");
            closeButton.setOnAction(e -> System.exit(0)); // Close the application

            VBox.setMargin(closeButton, new Insets(10, 0, 0, 0));
            card.getChildren().add(closeButton);

        } catch (IOException e) {
            displayError("Error parsing weather data");
        }
    }


    private String getWeatherEmoji(int weatherId) {
        switch (weatherId / 100) {
            case 2:
                return "⛈"; // Thunderstorm
            case 3:
                return "🌧"; // Drizzle
            case 5:
                return "🌧"; // Rain
            case 6:
                return "❄"; // Snow
            case 7:
                return "🌫"; // Atmosphere
            case 8:
                return weatherId == 800 ? "☀" : "☁"; // Clear or Clouds
            default:
                return "❓";
        }
    }

    private void displayError(String message) {
        if (errorDisplay == null) {
            errorDisplay = new Text();
            card.getChildren().add(errorDisplay);
        }
        errorDisplay.setText(message);
    }
}
