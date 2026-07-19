package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML private AnchorPane contentArea;
    @FXML private javafx.scene.shape.Circle sidebarProfileCircle;
    @FXML private NavigationController navMenuController;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (navMenuController != null) navMenuController.setActive("settings");
        changeView("/AccountInfoScreen.fxml");
    }

    public void handleAccountInfo(ActionEvent actionEvent) {
        changeView("/AccountInfoScreen.fxml");
    }

    public void handleCustomization(ActionEvent actionEvent) {
        changeView("/CustomizationScreen.fxml");
    }

    public void refreshSidebarAvatar() {
        if (Main.selectedAvatar != null && !Main.selectedAvatar.isEmpty() && sidebarProfileCircle != null) {
            try {
                java.io.InputStream is = getClass().getResourceAsStream(Main.selectedAvatar);
                if (is != null) {
                    javafx.scene.image.Image img = new javafx.scene.image.Image(is);
                    sidebarProfileCircle.setFill(new javafx.scene.paint.ImagePattern(img));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void handleMain(ActionEvent actionEvent) { Main.changeMenu(Main.homeMenu); }

    public void handleDiscovery(ActionEvent actionEvent) { Main.changeMenu(Main.discoveryMenu); }

    public void handleChat(ActionEvent actionEvent) { Main.changeMenu(Main.chatMenu); }

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
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                System.err.println("Settings UI Error: FXML file not found at " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
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