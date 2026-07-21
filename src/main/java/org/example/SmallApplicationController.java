package org.example;

import javafx.event.ActionEvent;

public class SmallApplicationController {

    public void handleAcceptApplication(ActionEvent actionEvent) {
        Main.homeController.handleAcceptApplication(actionEvent);
    }

    public void handleRejectApplication(ActionEvent actionEvent) {
        Main.homeController.handleRejectApplication(actionEvent);
    }
}
