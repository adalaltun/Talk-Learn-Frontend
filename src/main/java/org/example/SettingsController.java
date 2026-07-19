package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable, ProfileUpdatable {

    @FXML private SplitPane rootPane;
    @FXML private AnchorPane contentArea;
    @FXML private javafx.scene.shape.Circle sidebarProfileCircle;
    @FXML private NavigationController navMenuController;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (navMenuController != null) navMenuController.setActive("settings");
        changeView("/AccountInfoScreen.fxml");
        refreshWallpaperAndProfile();
    }

    @Override
    public void refreshWallpaperAndProfile() {
        if (profileName != null) {
            profileName.setText(Main.loggedInUser);
            profileName.setStyle("-fx-font-family: 'System'; -fx-font-weight: bold; -fx-font-size: 24px; -fx-text-fill: white; -fx-effect: dropshadow(one-pass-box, black, 2.0, 1.0, 0, 0);");
        }
        if (profileDate != null) {
            profileDate.setText(Main.accountCreationDate);
            profileDate.setStyle("-fx-font-family: 'System'; -fx-font-size: 16px; -fx-text-fill: white; -fx-effect: dropshadow(one-pass-box, black, 1.5, 1.0, 0, 0);");
        }

        if (Main.selectedWallpaper != null && !Main.selectedWallpaper.isEmpty() && rootPane != null) {
            rootPane.setStyle("-fx-background-image: url('" + Main.selectedWallpaper + "'); -fx-background-size: cover; -fx-background-position: center;");
        }

        if (Main.selectedAvatar != null && !Main.selectedAvatar.isEmpty() && profileCircle != null) {
            try {
                javafx.scene.image.Image img = new javafx.scene.image.Image(Main.selectedAvatar);
                javafx.scene.paint.ImagePattern pattern = new javafx.scene.paint.ImagePattern(img);
                profileCircle.setFill(pattern);

                if (profileLetterLabel != null) {
                    profileLetterLabel.setVisible(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void handleAccountInfo(ActionEvent actionEvent) {
        changeView("/AccountInfoScreen.fxml");
    }

    public void handleCustomization(ActionEvent actionEvent) {
        changeView("/CustomizationScreen.fxml");
    }

    public void refreshSidebarAvatar() {
        refreshWallpaperAndProfile();
    }

    public void handleMain(ActionEvent actionEvent) {
        Main.changeMenu(Main.homeMenu);
    }

    public void handleDiscovery(ActionEvent actionEvent) {
        Main.changeMenu(Main.discoveryMenu);
    }

    public void handleChat(ActionEvent actionEvent) {
        Main.changeMenu(Main.chatMenu);
    }

    public void handleLeaderboard(ActionEvent actionEvent) {
        if (Main.leaderboardController != null) {
            Main.leaderboardController.refreshData();
        }
        Main.changeMenu(Main.leaderboardScreen);
    }

    public void handleSettings(ActionEvent actionEvent) {}

    private void changeView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Parent newView = loader.load();
            contentArea.getChildren().clear();

            AnchorPane.setTopAnchor(newView, 0.0);
            AnchorPane.setBottomAnchor(newView, 0.0);
            AnchorPane.setLeftAnchor(newView, 0.0);
            AnchorPane.setRightAnchor(newView, 0.0);

            contentArea.getChildren().add(newView);

        } catch (IOException e) {
            System.err.println("Settings UI Error: Could not swap right view to " + fxmlPath);
            e.printStackTrace();
        }
    }
}