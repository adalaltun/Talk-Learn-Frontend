package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class NavigationController {

    @FXML private Button homeBtn;
    @FXML private Button discoveryBtn;
    @FXML private Button chatBtn;
    @FXML private Button leaderboardBtn;
    @FXML private Button settingsBtn;

    // This method is called by the controller of the loaded page to set the active style
    public void setActive(String page) {
        // Reset all buttons to default
        resetStyles();

        // Apply active style to the specific button
        switch (page) {
            case "home": homeBtn.getStyleClass().add("nav-button-active"); break;
            case "discovery": discoveryBtn.getStyleClass().add("nav-button-active"); break;
            case "chat": chatBtn.getStyleClass().add("nav-button-active"); break;
            case "leaderboard": leaderboardBtn.getStyleClass().add("nav-button-active"); break;
            case "settings": settingsBtn.getStyleClass().add("nav-button-active"); break;
        }
    }

    private void resetStyles() {
        homeBtn.getStyleClass().remove("nav-button-active");
        discoveryBtn.getStyleClass().remove("nav-button-active");
        chatBtn.getStyleClass().remove("nav-button-active");
        leaderboardBtn.getStyleClass().remove("nav-button-active");
        settingsBtn.getStyleClass().remove("nav-button-active");
    }

    @FXML public void handleMain(ActionEvent e) { Main.changeMenu(Main.homeMenu); }
    @FXML public void handleDiscovery(ActionEvent e) { Main.changeMenu(Main.discoveryMenu); }
    @FXML public void handleChat(ActionEvent e) { Main.changeMenu(Main.chatMenu); }
    @FXML public void handleLeaderboard(ActionEvent e) { Main.changeMenu(Main.leaderboardMenu); }
    @FXML public void handleSettings(ActionEvent e) { Main.changeMenu(Main.settingsMenu); }
}