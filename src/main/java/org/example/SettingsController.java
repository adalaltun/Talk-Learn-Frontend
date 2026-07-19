package org.example;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML private AnchorPane contentArea;

    @FXML private Button avatar1;
    @FXML private Button avatar2;
    @FXML private Button avatar3;

    @FXML private Button wallpaper1;
    @FXML private Button wallpaper2;
    @FXML private Button wallpaper3;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {
            changeView("/AccountInfoScreen.fxml");
        });

        getWallpapers("/images/wallpaper.jpg");
        getAvatars("/images/pp1.png");
    }

    public void handleAccountInfo(ActionEvent actionEvent) {
        changeView("/AccountInfoScreen.fxml");
    }

    public void handleCustomization(ActionEvent actionEvent) {
        changeView("/CustomizationScreen.fxml");
    }

    public void handleMain(ActionEvent actionEvent) {Main.changeMenu(Main.homeMenu);}

    public void handleDiscovery(ActionEvent actionEvent) {Main.changeMenu(Main.discoveryMenu);}

    public void handleChat(ActionEvent actionEvent) { Main.changeMenu(Main.chatMenu);}

    public void handleLeaderboard(ActionEvent actionEvent) {
        if (Main.leaderboardController != null) {
            Main.leaderboardController.refreshData();
        }
        Main.changeMenu(Main.leaderboardScreen);
    }

    public void handleSettings(ActionEvent actionEvent) {
    }

    private void changeView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newView = loader.load();

            // Clear previous right panel content
            contentArea.getChildren().clear();

            // Ensure the incoming view stretches perfectly to fill the container anchors
            AnchorPane.setTopAnchor(newView, 0.0);
            AnchorPane.setBottomAnchor(newView, 0.0);
            AnchorPane.setLeftAnchor(newView, 0.0);
            AnchorPane.setRightAnchor(newView, 0.0);

            // Append the new pane to the visible layout tree
            contentArea.getChildren().add(newView);

        } catch (IOException e) {
            System.err.println("Settings UI Error: Could not swap right view to " + fxmlPath);
            e.printStackTrace();
        }
    }


    public void handleAvatarSelection(ActionEvent actionEvent) {
    }

    public void handleWallpaperSelection(ActionEvent actionEvent) {
    }

    public void getWallpapers(String imagePath) {
        try {
            if (!imagePath.startsWith("/")) {
                imagePath = "/" + imagePath;
            }

            java.net.URL imageURL = getClass().getResource(imagePath);
            if (imageURL == null) {
                System.err.println("Wallpaper image not found at path: " + imagePath);
                return;
            }

            String externalForm = imageURL.toExternalForm();

            wallpaper1.setStyle(
                    "-fx-background-image: url('" + externalForm + "'); " +
                            "-fx-background-size: cover; " +
                            "-fx-background-position: center;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-radius: 10;"
            );

            javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();

            clip.setArcWidth(20);
            clip.setArcHeight(20);

            clip.widthProperty().bind(wallpaper1.widthProperty());
            clip.heightProperty().bind(wallpaper1.heightProperty());

            wallpaper1.setClip(clip);

            wallpaper1.setOnMouseEntered(e -> wallpaper1.setOpacity(0.85));
            wallpaper1.setOnMouseExited(e -> wallpaper1.setOpacity(1.0));

            javafx.scene.effect.ColorAdjust clickDarken = new javafx.scene.effect.ColorAdjust();
            clickDarken.setBrightness(-0.2);

            wallpaper1.setOnMousePressed(e -> wallpaper1.setEffect(clickDarken));
            wallpaper1.setOnMouseReleased(e -> wallpaper1.setEffect(null));


        }catch (Exception e) {
            System.err.println("Could not load global wallpaper: " + imagePath);
        }
    }


    public void getAvatars(String imagePath) {
        try {
            if (!imagePath.startsWith("/")) {
                imagePath = "/" + imagePath;
            }

            java.net.URL imageURL = getClass().getResource(imagePath);
            if (imageURL == null) {
                System.err.println("Wallpaper image not found at path: " + imagePath);
                return;
            }

            String externalForm = imageURL.toExternalForm();

            avatar1.setStyle(
                    "-fx-background-image: url('" + externalForm + "'); " +
                            "-fx-background-size: cover; " +
                            "-fx-background-position: center;" +
                            "-fx-background-radius: 999;" +
                            "-fx-border-radius: 999;"
            );

            javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();

            clip.widthProperty().bind(avatar1.widthProperty());
            clip.heightProperty().bind(avatar1.heightProperty());

            clip.arcWidthProperty().bind(avatar1.widthProperty());
            clip.arcHeightProperty().bind(avatar1.heightProperty());

            avatar1.setClip(clip);




        }catch (Exception e) {
            System.err.println("Could not load global wallpaper: " + imagePath);
        }
    }


    public void handleUpdateUsername(ActionEvent actionEvent) {
    }

    public void handleUpdatePassword(ActionEvent actionEvent) {
    }
}
