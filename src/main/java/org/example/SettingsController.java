package org.example;

import javafx.event.ActionEvent;

public class SettingsController {
    public void handleAccountInfo(ActionEvent actionEvent) {
    }

    public void handleCustomization(ActionEvent actionEvent) {
    }

    public void handleMain(ActionEvent actionEvent) {
        Main.changeMenu(Main.homeMenu);
    }

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
}
