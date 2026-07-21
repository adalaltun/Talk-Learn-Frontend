package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class NavigationController {

    @FXML private Button homeBtn;
    @FXML private Button discoveryBtn;
    @FXML private Button chatBtn;
    @FXML private Button leaderboardBtn;
    @FXML private Button settingsBtn;

    private static final String ACTIVE_STYLE = "-fx-background-color: #0078fe; -fx-background-radius: 20; -fx-border-color: #0078fe; -fx-border-radius: 20; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;";
    private static final String INACTIVE_STYLE = "-fx-background-color: #f5f5f5; -fx-background-radius: 20; -fx-border-color: #cccccc; -fx-border-radius: 20; -fx-text-fill: #1a1a1a; -fx-font-weight: bold; -fx-cursor: hand;";

    public void setActive(String page) {
        resetStyles();

        if (page == null) return;

        switch (page.toLowerCase()) {
            case "home":
                homeBtn.setStyle(ACTIVE_STYLE);
                break;
            case "discovery":
                discoveryBtn.setStyle(ACTIVE_STYLE);
                break;
            case "chat":
                chatBtn.setStyle(ACTIVE_STYLE);
                break;
            case "leaderboard":
                leaderboardBtn.setStyle(ACTIVE_STYLE);
                break;
            case "settings":
                settingsBtn.setStyle(ACTIVE_STYLE);
                break;
        }
    }

    private void resetStyles() {
        homeBtn.setStyle(INACTIVE_STYLE);
        discoveryBtn.setStyle(INACTIVE_STYLE);
        chatBtn.setStyle(INACTIVE_STYLE);
        leaderboardBtn.setStyle(INACTIVE_STYLE);
        settingsBtn.setStyle(INACTIVE_STYLE);
    }

    @FXML
    private void handleMain(ActionEvent event) {
        try {
            // Load the home screen layout
            Parent root = FXMLLoader.load(getClass().getResource("/homeScreen.fxml"));

            // Get the current window (stage) from the button click event
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the screen to the fresh Home Screen (restoring the calendar)
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML public void handleDiscovery(ActionEvent e) { Main.changeMenu(Main.discoveryMenu); }
    @FXML public void handleChat(ActionEvent e) { Main.changeMenu(Main.chatMenu); }
    @FXML public void handleLeaderboard(ActionEvent e) { Main.changeMenu(Main.leaderboardScreen); }
    @FXML public void handleSettings(ActionEvent e) { Main.changeMenu(Main.settingsScreen); }
}