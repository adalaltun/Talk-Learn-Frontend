package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    private static final String BACKEND_URL = "http://localhost:8080/api/auth/login";

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Main.displayAlert(Alert.AlertType.ERROR, "Insufficient Information", "Please fill all fields");
            return;
        }

        if (username.length() > 16 || password.length() > 20) {
            Main.displayAlert(Alert.AlertType.ERROR, "Security Warning", "Input length exceeded boundaries");
            return;
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
                Main.loggedInUser = username;
                String body = response.body();

                if (body != null && body.contains("selectedAvatar")) {
                    String avatar = body.split("selectedAvatar\":\"")[1].split("\"")[0];
                    Main.selectedAvatar = avatar.isEmpty() ? null : avatar;
                }
                if (body != null && body.contains("selectedWallpaper")) {
                    String wallpaper = body.split("selectedWallpaper\":\"")[1].split("\"")[0];
                    Main.selectedWallpaper = wallpaper.isEmpty() ? null : wallpaper;
                }

                Main.accountCreationDate = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                Main.displayAlert(Alert.AlertType.INFORMATION, "Successful", "Logged In!");

                if (Main.homeController != null) {
                    Main.homeController.refreshPage();
                }
                if (Main.leaderboardController != null) {
                    Main.leaderboardController.refreshData();
                }

                Main.changeMenu(Main.homeMenu);
            } else {
                Main.displayAlert(Alert.AlertType.ERROR, "Error", "Username or password is incorrect!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Main.displayAlert(Alert.AlertType.ERROR, "Connection Error", "Failed to connect to the backend server!");
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

    @FXML
    private void handleLoginKeyEvents(javafx.scene.input.KeyEvent event) {
        if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
            if (event.getSource() == usernameField) {
                passwordField.requestFocus();
            } else if (event.getSource() == passwordField) {
                loginButton.fire();
            }
        }
    }
}