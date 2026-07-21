package org.example;

import javafx.event.ActionEvent;

public class SmallApplicationController {

    public void handleAcceptApplication(ActionEvent actionEvent) {
        if (Main.homeController != null) {
            Main.homeController.handleAcceptApplication(actionEvent);
        }
    }

    public void handleRejectApplication(ActionEvent actionEvent) {
        if (Main.homeController != null) {
            Main.homeController.handleRejectApplication(actionEvent);
        }
    }
}