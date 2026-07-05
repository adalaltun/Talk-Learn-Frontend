package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RegisterController {
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
        boolean fieldEmpty = false; //false if the username, password, repeatedPassword datas are entered

        if (username.isEmpty()) {
            displayAlert(Alert.AlertType.ERROR, "Insufficient Information", "Please Enter Username");
            fieldEmpty = true;
        }
        if (password.isEmpty()) {
            displayAlert(Alert.AlertType.ERROR, "Insufficient Information", "Please Enter Password");
            fieldEmpty = true;
        } else if (repeatedPassword.isEmpty()) {
            displayAlert(Alert.AlertType.ERROR, "Insufficient Information", "Please Repeat Your Password");
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

                //This if statements check: if the password is at least 8 characters, if the password contains both lower and
                //uppercase letters, if the password contains at least one digit, if the password has gap in it,
                //if the password matches with the username
                if(password.length() < 8) {
                    displayAlert(Alert.AlertType.ERROR, "Security Warning", "Please Enter A Password That Contains Minimum 8 Characters");
                }
                else if(password.toLowerCase().equals(password)){
                    displayAlert(Alert.AlertType.ERROR, "Security Warning", "Please Enter A Password That Contains At Least 1 Uppercase Letter");
                }
                else if(password.toUpperCase().equals(password)){
                    displayAlert(Alert.AlertType.ERROR, "Security Warning", "Please Enter A Password That Contains At Least 1 Lowercase Letter");
                }
                else if(!hasDigit) {
                    displayAlert(Alert.AlertType.ERROR, "Security Warning", "Please Enter A Password That Contains At Least 1 Digit");
                }
                else if (password.contains(" ")) {
                    displayAlert(Alert.AlertType.ERROR, "Security Warning", "Your password cannot contain any spaces!");
                }
                else if (password.indexOf(username) != -1) {
                    displayAlert(Alert.AlertType.ERROR, "Security Warning", "Please Do Not Include Your Username In Your Password");
                } else {
                    //the program enters this section if everything is fine with the chosen username and password and repeated password
                    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

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
                            displayAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful! Redirecting to login.");
                            //homeMenu can be changed into any menu that will be shown after a successful login
                            Main.changeMenu(Main.homeMenu);
                            //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
                            System.out.println("Successfully redirected to " + BACKEND_URL);
                        } else {
                            displayAlert(Alert.AlertType.ERROR, "Registration Failed", response.body());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        displayAlert(Alert.AlertType.ERROR, "Connection Error", "Could not connect to the backend server. Make sure it is running!");
                    }
                }
            } else {
                displayAlert(Alert.AlertType.ERROR, "Information Mismatch", "Passwords do not match");
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

    //displayAlert method's first parameter is the error type the others are title and the message
    //it is used to prompt a read only warning screen!
    private static void displayAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}