package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.paint.ImagePattern;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class CustomizationController implements Initializable {
    @FXML private Button avatar1;
    @FXML private Button wallpaper1;
    @FXML private Button wallpaper2;
    @FXML private Button wallpaper3;
    @FXML private Circle avatarOpt1;
    @FXML private Circle avatarOpt2;
    @FXML private Circle avatarOpt3;
    @FXML private Circle avatarOpt4;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        applyWallpaperImage(wallpaper1, "/images/wallpapers/wp1.jpg");
        applyWallpaperImage(wallpaper2, "/images/wallpapers/wp2.jpg");
        applyWallpaperImage(wallpaper3, "/images/wallpapers/wp3.jpg");

        loadAvatarImage("/images/profilephotos/pp1.png", avatarOpt1);

        setupWallpaperButton(wallpaper1, "/images/wallpapers/wp1.jpg");
        setupWallpaperButton(wallpaper2, "/images/wallpapers/wp2.jpg");
        setupWallpaperButton(wallpaper3, "/images/wallpapers/wp3.jpg");

        setupAvatarButton(avatar1, "/images/profilephotos/pp1.png", avatarOpt1);
    }

    private void setupWallpaperButton(Button button, String imagePath) {
        if (button == null) return;
        button.setOnAction(e -> handleWallpaperSelection(imagePath, button));
    }

    private void handleWallpaperSelection(String imagePath, Button selectedButton) {
        Main.selectedWallpaper = imagePath;

        if (wallpaper1 != null) updateButtonBorder(wallpaper1, false);
        if (wallpaper2 != null) updateButtonBorder(wallpaper2, false);
        if (wallpaper3 != null) updateButtonBorder(wallpaper3, false);

        updateButtonBorder(selectedButton, true);
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

    private void handleAvatarSelection(String imagePath, Circle targetCircle) {
        Main.selectedAvatar = imagePath;

        if (avatarOpt1 != null) avatarOpt1.setStroke(javafx.scene.paint.Color.TRANSPARENT);
        if (avatarOpt2 != null) avatarOpt2.setStroke(javafx.scene.paint.Color.TRANSPARENT);
        if (avatarOpt3 != null) avatarOpt3.setStroke(javafx.scene.paint.Color.TRANSPARENT);
        if (avatarOpt4 != null) avatarOpt4.setStroke(javafx.scene.paint.Color.TRANSPARENT);

        if (targetCircle != null) {
            targetCircle.setStroke(javafx.scene.paint.Color.valueOf("#0078fe"));
            loadAvatarImage(imagePath, targetCircle);
        }

        if (Main.homeController != null) {
            Main.homeController.refreshWallpaperAndProfile();
        }

        if (avatar1 != null && avatar1.getScene() != null) {
            javafx.scene.Node sidebarCircle = avatar1.getScene().lookup("#sidebarProfileCircle");
            if (sidebarCircle instanceof Circle) {
                try {
                    java.io.InputStream is = getClass().getResourceAsStream(imagePath);
                    if (is != null) {
                        ((Circle) sidebarCircle).setFill(new javafx.scene.paint.ImagePattern(new Image(is)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadAvatarImage(String imagePath, Circle targetCircle) {
        if (targetCircle == null) return;
        try {
            if (!imagePath.startsWith("/")) {
                imagePath = "/" + imagePath;
            }
            InputStream is = getClass().getResourceAsStream(imagePath);
            if (is == null) {
                return;
            }
            Image img = new Image(is);
            ImagePattern imgPattern = new ImagePattern(img);
            targetCircle.setFill(imgPattern);
            targetCircle.setOpacity(1.0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleAvatarSelection(ActionEvent actionEvent) {
    }

    public void handleWallpaperSelection(ActionEvent actionEvent) {
    }

    public void applyWallpaperImage(Button button, String imagePath) {
        if (button == null) return;

        try {
            if (!imagePath.startsWith("/")) {
                imagePath = "/" + imagePath;
            }

            URL imageURL = getClass().getResource(imagePath);
            if (imageURL == null) {
                return;
            }

            String externalForm = imageURL.toExternalForm();
            String currentStyle = button.getStyle();
            if (currentStyle == null) currentStyle = "";

            button.setStyle(
                    currentStyle +
                            "; -fx-background-image: url('" + externalForm + "'); " +
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

    public void getWallpapers(String imagePath) {
        if (Main.homeMenu != null) {
            try {
                if (!imagePath.startsWith("/")) {
                    imagePath = "/" + imagePath;
                }
                URL bgURL = getClass().getResource(imagePath);
                if (bgURL != null) {
                    Main.homeMenu.setStyle("-fx-background-image: url('" + bgURL.toExternalForm() + "'); " +
                            "-fx-background-size: cover; " +
                            "-fx-background-position: center;");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void getAvatars(String imagePath) {
        loadAvatarImage(imagePath, avatarOpt1);
    }
}