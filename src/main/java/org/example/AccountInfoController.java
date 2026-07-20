package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;


import java.net.URL;
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

    public void handleUpdateUsername(ActionEvent actionEvent) {}
    public void handleUpdatePassword(ActionEvent actionEvent) {}
}