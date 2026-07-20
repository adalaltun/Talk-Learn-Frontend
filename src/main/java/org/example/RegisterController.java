package org.example;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RegisterController {
    @FXML
    public Button signUpButton;

    public Button loginButton;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField, repeatedPasswordField;

    private static final String BACKEND_URL = "http://localhost:8080/api/auth/register";

    @FXML
    public void handleRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String repeatedPassword = repeatedPasswordField.getText().trim();
        boolean fieldEmpty = false;

        if (username.isEmpty()) {
            Main.displayAlert(Alert.AlertType.ERROR, "Insufficient Information", "Please Enter Username");
            fieldEmpty = true;
        }
        if (password.isEmpty()) {
            Main.displayAlert(Alert.AlertType.ERROR, "Insufficient Information", "Please Enter Password");
            fieldEmpty = true;
        } else if (repeatedPassword.isEmpty()) {
            Main.displayAlert(Alert.AlertType.ERROR, "Insufficient Information", "Please Repeat Your Password");
            fieldEmpty = true;
        }
        if (!fieldEmpty) {
            if (password.equals(repeatedPassword)) {

                boolean hasDigit = false;
                for (char c : password.toCharArray()) {
                    if (Character.isDigit(c)) {
                        hasDigit = true;
                        break;
                    }
                }

                if(password.length() < 8) {
                    Main.displayAlert(Alert.AlertType.ERROR, "Security Warning", "Please Enter A Password That Contains Minimum 8 Characters");
                }
                else if(password.toLowerCase().equals(password)){
                    Main.displayAlert(Alert.AlertType.ERROR, "Security Warning", "Please Enter A Password That Contains At Least 1 Uppercase Letter");
                }
                else if(password.toUpperCase().equals(password)){
                    Main.displayAlert(Alert.AlertType.ERROR, "Security Warning", "Please Enter A Password That Contains At Least 1 Lowercase Letter");
                }
                else if(!hasDigit) {
                    Main.displayAlert(Alert.AlertType.ERROR, "Security Warning", "Please Enter A Password That Contains At Least 1 Digit");
                }
                else if (password.contains(" ")) {
                    Main.displayAlert(Alert.AlertType.ERROR, "Security Warning", "Your password cannot contain any spaces!");
                }
                else if (password.indexOf(username) != -1) {
                    Main.displayAlert(Alert.AlertType.ERROR, "Security Warning", "Please Do Not Include Your Username In Your Password");
                } else {

                    String jsonBody = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BACKEND_URL))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 201 || response.statusCode() == 200) {
                            Main.loggedInUser = username;
                            String body = response.body();
                            if (body != null && body.contains("creationDate")) {
                                String[] parts = body.split("creationDate\":\"");
                                if (parts.length > 1) {
                                    Main.accountCreationDate = parts[1].split("\"")[0];
                                }
                            } else {
                                Main.accountCreationDate = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                            }
                            Platform.runLater(() -> {
                                Main.displayAlert(Alert.AlertType.INFORMATION, "Successful", "Your account has been registered successfully!");
                                if (Main.homeController != null) {
                                    Main.homeController.refreshPage();
                                }
                                if (Main.leaderboardController != null) {
                                    Main.leaderboardController.refreshData();
                                }
                                Main.changeMenu(Main.homeMenu);
                            });
                            clearFields();
                        } else {
                            Platform.runLater(() -> {
                                Main.displayAlert(Alert.AlertType.ERROR, "Registration Failed", "Username is already taken");
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Main.displayAlert(Alert.AlertType.ERROR, "Connection Error", "Could not connect to the backend server. Make sure it is running!");
                    }
                }
            } else {
                Main.displayAlert(Alert.AlertType.ERROR, "Information Mismatch", "Passwords do not match");
            }

        }

    }

    public void switchScene(ActionEvent event) {
        Main.changeMenu(Main.loginScreen);
        clearFields();
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        repeatedPasswordField.clear();
    }

    @FXML
    private void handleRegisterKeyEvents(javafx.scene.input.KeyEvent event) {
        if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
            if (usernameField.isFocused()) {
                passwordField.requestFocus();
            } else if (passwordField.isFocused()) {
                repeatedPasswordField.requestFocus();
            } else if (repeatedPasswordField.isFocused()) {
                signUpButton.fire();
            }
        }
    }
}