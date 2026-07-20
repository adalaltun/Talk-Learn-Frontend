package org.example;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.ColorAdjust;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomizationController implements Initializable, ProfileUpdatable {
    @FXML
    private SplitPane rootPane;
    @FXML
    private Label profileName;
    @FXML
    private Label profileDate;
    @FXML
    private Circle profileCircle;
    @FXML
    private Label profileLetterLabel;

    @FXML
    private Button avatar1;
    @FXML
    private Button avatar2;
    @FXML
    private Button avatar3;
    @FXML
    private Button wallpaper1;
    @FXML
    private Button wallpaper2;
    @FXML
    private Button wallpaper3;
    @FXML
    private Circle avatarOpt1;
    @FXML
    private Circle avatarOpt2;
    @FXML
    private Circle avatarOpt3;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        applyWallpaperImage(wallpaper1, "/images/wallpapers/wp1.jpg");
        applyWallpaperImage(wallpaper2, "/images/wallpapers/wp2.jpg");
        applyWallpaperImage(wallpaper3, "/images/wallpapers/wp3.jpg");

        loadAvatarImage("/images/profilephotos/pp1.png", avatarOpt1);
        loadAvatarImage("/images/profilephotos/pp2.png", avatarOpt2);
        loadAvatarImage("/images/profilephotos/pp3.png", avatarOpt3);

        setupWallpaperButton(wallpaper1, "/images/wallpapers/wp1.jpg");
        setupWallpaperButton(wallpaper2, "/images/wallpapers/wp2.jpg");
        setupWallpaperButton(wallpaper3, "/images/wallpapers/wp3.jpg");

        setupAvatarButton(avatar1, "/images/profilephotos/pp1.png", avatarOpt1);
        setupAvatarButton(avatar2, "/images/profilephotos/pp2.png", avatarOpt2);
        setupAvatarButton(avatar3, "/images/profilephotos/pp3.png", avatarOpt3);

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

        syncBordersToCurrentSelection();
    }

    private void syncBordersToCurrentSelection() {
        if (wallpaper1 != null)
            updateButtonBorder(wallpaper1, "/images/wallpapers/wp1.jpg".equals(Main.selectedWallpaper));
        if (wallpaper2 != null)
            updateButtonBorder(wallpaper2, "/images/wallpapers/wp2.jpg".equals(Main.selectedWallpaper));
        if (wallpaper3 != null)
            updateButtonBorder(wallpaper3, "/images/wallpapers/wp3.jpg".equals(Main.selectedWallpaper));

        if (avatarOpt1 != null)
            avatarOpt1.setStroke("/images/profilephotos/pp1.png".equals(Main.selectedAvatar) ? javafx.scene.paint.Color.valueOf("#0078fe") : javafx.scene.paint.Color.TRANSPARENT);
        if (avatarOpt2 != null)
            avatarOpt2.setStroke("/images/profilephotos/pp2.png".equals(Main.selectedAvatar) ? javafx.scene.paint.Color.valueOf("#0078fe") : javafx.scene.paint.Color.TRANSPARENT);
        if (avatarOpt3 != null)
            avatarOpt3.setStroke("/images/profilephotos/pp3.png".equals(Main.selectedAvatar) ? javafx.scene.paint.Color.valueOf("#0078fe") : javafx.scene.paint.Color.TRANSPARENT);
    }

    private void setupWallpaperButton(Button button, String imagePath) {
        if (button == null) return;
        button.setOnAction(e -> handleWallpaperSelection(imagePath, button));
    }

    private void updateButtonBorder(Button button, boolean isSelected) {
        String baseStyle = button.getStyle();
        if (baseStyle == null) baseStyle = "";

        baseStyle = baseStyle.replaceAll("-fx-border-color:[^;]+;", "").trim();

        if (isSelected) {
            button.setStyle(baseStyle + "; -fx-border-color: #0078fe;");
        } else {
            button.setStyle(baseStyle + "; -fx-border-color: transparent;");
        }
    }

    private void setupAvatarButton(Button button, String imagePath, Circle targetCircle) {
        if (button == null) return;
        button.setOnAction(e -> handleAvatarSelection(imagePath, targetCircle));
    }

    private void handleWallpaperSelection(String imagePath, Button selectedButton) {
        Main.selectedWallpaper = imagePath;
        refreshWallpaperAndProfile();

        if (Main.homeController != null) {
            Main.homeController.refreshWallpaperAndProfile();
        }
        if (Main.settingsController != null) {
            Main.settingsController.refreshWallpaperAndProfile();
        }

        saveCustomizationToDatabase();
    }

    private void handleAvatarSelection(String imagePath, Circle targetCircle) {
        Main.selectedAvatar = imagePath;
        refreshWallpaperAndProfile();

        if (Main.homeController != null) {
            Main.homeController.refreshWallpaperAndProfile();
        }
        if (Main.settingsController != null) {
            Main.settingsController.refreshWallpaperAndProfile();
        }

        saveCustomizationToDatabase();
    }

    private void loadAvatarImage(String imagePath, Circle targetCircle) {
        if (targetCircle == null) return;
        try {
            if (!imagePath.startsWith("/")) {
                imagePath = "/" + imagePath;
            }
            javafx.scene.image.Image img = new javafx.scene.image.Image(imagePath);
            javafx.scene.paint.ImagePattern imgPattern = new javafx.scene.paint.ImagePattern(img);
            targetCircle.setFill(imgPattern);
            targetCircle.setOpacity(1.0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void applyWallpaperImage(Button button, String imagePath) {
        if (button == null) return;

        try {
            if (!imagePath.startsWith("/")) {
                imagePath = "/" + imagePath;
            }

            String currentStyle = button.getStyle();
            if (currentStyle == null) currentStyle = "";

            button.setStyle(
                    currentStyle +
                            "; -fx-background-image: url('" + imagePath + "'); " +
                            "-fx-background-size: cover; " +
                            "-fx-background-position: center;"
            );

            Rectangle clip = new Rectangle();
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            clip.widthProperty().bind(button.widthProperty());
            clip.heightProperty().bind(button.heightProperty());
            button.setClip(clip);

            button.setOnMouseEntered(e -> button.setOpacity(0.85));
            button.setOnMouseExited(e -> button.setOpacity(1.0));

            ColorAdjust clickDarken = new ColorAdjust();
            clickDarken.setBrightness(-0.2);
            button.setOnMousePressed(e -> button.setEffect(clickDarken));
            button.setOnMouseReleased(e -> button.setEffect(null));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveCustomizationToDatabase() {
        String avatar = Main.selectedAvatar != null ? Main.selectedAvatar : "";
        String wallpaper = Main.selectedWallpaper != null ? Main.selectedWallpaper : "";

        String jsonBody = String.format(
                "{\"username\":\"%s\", \"selectedAvatar\":\"%s\", \"selectedWallpaper\":\"%s\"}",
                Main.loggedInUser, avatar, wallpaper
        );

        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8080/api/auth/update-customization"))
                    .header("Content-Type", "application/json")
                    .PUT(java.net.http.HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Customization save failed: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}