package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SmallListingController {

    @FXML private Label listingTitle;
    @FXML private Label listingLanguage;
    @FXML private Label listingRank;
    @FXML private Label listingUser; //

    @FXML
    public void handleListing(ActionEvent actionEvent) {
        System.out.println("-- handleListing");
        Main.changeMenu(Main.applicationScene);

        if (Main.listingApplyController != null) {
            String hostUser = (listingUser != null) ? listingUser.getText() : "";
            Main.listingApplyController.setLabels(listingTitle, listingLanguage, hostUser);
        } else {
            System.err.println("Error: Main.listingApplyController is null!");
        }
    }
}