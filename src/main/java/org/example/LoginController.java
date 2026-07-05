package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private static final String BACKEND_URL = "http://localhost:8080/api/auth/login";

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        boolean fieldEmpty = false; //false if the username and password datas are entered

        if(username.isEmpty()) {
            displayAlert(Alert.AlertType.ERROR, "Insufficient Information", "Please Enter Username");
            fieldEmpty = true;
        }
        if(password.isEmpty()) {
            displayAlert(Alert.AlertType.ERROR, "Insufficient Information", "Please Enter Password");
            fieldEmpty = true;
        }

        String jsonBody = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BACKEND_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                displayAlert(Alert.AlertType.INFORMATION, "Success!", response.body());
                Main.changeMenu(Main.homeMenu);
            } else {
                displayAlert(Alert.AlertType.ERROR, "Login Failed", response.body());
            }

        } catch (Exception e) {
            e.printStackTrace();
            displayAlert(Alert.AlertType.ERROR, "Connection Error", "Failed to connect to the backend server!");
        }
    }

    public void switchScene(ActionEvent event) {
        Main.changeMenu(Main.registerScreen);
        clearFields();
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
    }
    //displayAlert method's first parameter is the error type the others are title and the message
    //it is used to prompt a read only warning screen!
    private void displayAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}