package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

public class AccountInfoController implements Initializable, ProfileUpdatable {
    @FXML private SplitPane rootPane;
    @FXML private Label profileName;
    @FXML private Label profileDate;
    @FXML private Circle profileCircle;
    @FXML private Label profileLetterLabel;

    @FXML private TextField newUsernameInput;
    @FXML private PasswordField currentPasswordInput;
    @FXML private PasswordField newPasswordInput;

    private static final String BASE_AUTH_URL = "http://localhost:8080/api/auth";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshWallpaperAndProfile();
    }

    @Override
    public void refreshWallpaperAndProfile() {
        if (profileName != null) {
            profileName.setText(Main.loggedInUser);
            profileName.setStyle("-fx-font-family: 'System'; -fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: white; -fx-effect: dropshadow(one-pass-box, black, 2.0, 1.0, 0, 0);");
        }
        if (profileDate != null) {
            profileDate.setText(Main.accountCreationDate);
            profileDate.setStyle("-fx-font-family: 'System'; -fx-font-size: 14px; -fx-text-fill: white; -fx-effect: dropshadow(one-pass-box, black, 1.5, 1.0, 0, 0);");
        }

        if (Main.selectedWallpaper != null && !Main.selectedWallpaper.isEmpty() && rootPane != null) {
            rootPane.setStyle("-fx-background-image: url('" + Main.selectedWallpaper + "'); -fx-background-size: cover; -fx-background-position: center;");
        }

        if (Main.selectedAvatar != null && !Main.selectedAvatar.isEmpty() && profileCircle != null) {
            try {
                javafx.scene.image.Image img = new javafx.scene.image.Image(Main.selectedAvatar);
                profileCircle.setFill(new javafx.scene.paint.ImagePattern(img));
                if (profileLetterLabel != null) {
                    profileLetterLabel.setVisible(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void handleUpdateUsername(ActionEvent actionEvent) {
        String newUsername = newUsernameInput.getText().trim();
        if (newUsername.isEmpty()) {
            Main.displayAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid username");
            return;
        }

        if (newUsername.length() > 16) {
            Main.displayAlert(Alert.AlertType.ERROR, "Security Warning", "Username cannot be longer than 16 characters");
            return;
        }

        String jsonBody = String.format("{\"currentUsername\":\"%s\", \"newUsername\":\"%s\"}", Main.loggedInUser, newUsername);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_AUTH_URL + "/update-username"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Main.loggedInUser = newUsername;

                refreshWallpaperAndProfile();

                if (Main.settingsController != null) {
                    Main.settingsController.refreshWallpaperAndProfile();
                }
                if (Main.homeController != null) {
                    Main.homeController.refreshPage();
                }
                if (Main.customizationController != null) {
                    Main.customizationController.refreshWallpaperAndProfile();
                }

                newUsernameInput.clear();
                Main.displayAlert(Alert.AlertType.INFORMATION, "Success", "Username updated successfully!");
            } else {
                Main.displayAlert(Alert.AlertType.ERROR, "Error", "Failed to update username or username already taken.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Main.displayAlert(Alert.AlertType.ERROR, "Connection Error", "Could not connect to the backend server!");
        }
    }

    public void handleUpdatePassword(ActionEvent actionEvent) {
        String currentPassword = currentPasswordInput.getText().trim();
        String newPassword = newPasswordInput.getText().trim();

        if (currentPassword.isEmpty() || newPassword.isEmpty()) {
            Main.displayAlert(Alert.AlertType.ERROR, "Error", "Please fill in all password fields");
            return;
        }

        if (newPassword.length() > 20) {
            Main.displayAlert(Alert.AlertType.ERROR, "Security Warning", "Password cannot be longer than 20 characters");
            return;
        }

        if (currentPassword.equals(newPassword)) {
            Main.displayAlert(Alert.AlertType.ERROR, "Security Warning", "Your new password cannot be the same as your current password");
            return;
        }

        boolean hasDigit = false;
        for (char c : newPassword.toCharArray()) {
            if (Character.isDigit(c)) {
                hasDigit = true;
                break;
            }
        }

        if (newPassword.length() < 8) {
            Main.displayAlert(Alert.AlertType.ERROR, "Security Warning", "Please Enter A Password That Contains Minimum 8 Characters");
            return;
        }
        if (newPassword.toLowerCase().equals(newPassword)) {
            Main.displayAlert(Alert.AlertType.ERROR, "Security Warning", "Please Enter A Password That Contains At Least 1 Uppercase Letter");
            return;
        }
        if (newPassword.toUpperCase().equals(newPassword)) {
            Main.displayAlert(Alert.AlertType.ERROR, "Security Warning", "Please Enter A Password That Contains At Least 1 Lowercase Letter");
            return;
        }
        if (!hasDigit) {
            Main.displayAlert(Alert.AlertType.ERROR, "Security Warning", "Please Enter A Password That Contains At Least 1 Digit");
            return;
        }
        if (newPassword.contains(" ")) {
            Main.displayAlert(Alert.AlertType.ERROR, "Security Warning", "Your password cannot contain any spaces!");
            return;
        }
        if (newPassword.indexOf(Main.loggedInUser) != -1) {
            Main.displayAlert(Alert.AlertType.ERROR, "Security Warning", "Please Do Not Include Your Username In Your Password");
            return;
        }

        String jsonBody = String.format("{\"username\":\"%s\", \"currentPassword\":\"%s\", \"newPassword\":\"%s\"}", Main.loggedInUser, currentPassword, newPassword);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_AUTH_URL + "/update-password"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                currentPasswordInput.clear();
                newPasswordInput.clear();
                Main.displayAlert(Alert.AlertType.INFORMATION, "Success", "Password changed successfully!");
            } else {
                Main.displayAlert(Alert.AlertType.ERROR, "Error", "Incorrect current password or invalid new password.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Main.displayAlert(Alert.AlertType.ERROR, "Connection Error", "Could not connect to the backend server!");
        }
    }

    public void handleLogout(ActionEvent actionEvent) {
        Main.loggedInUser = "NoAccount";
        Main.accountCreationDate = "Please Login";
        Main.selectedWallpaper = null;
        Main.selectedAvatar = null;

        javafx.stage.Stage currentStage = (javafx.stage.Stage) newUsernameInput.getScene().getWindow();
        currentStage.close();

        try {
            Main newMain = new Main();
            javafx.stage.Stage newStage = new javafx.stage.Stage();
            newMain.start(newStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}