package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SmallListingController {

    @FXML private Label listingTitle;
    @FXML private Label listingLanguage;
    @FXML private Label listingRank;


    @FXML
    public void handleListing(ActionEvent actionEvent) {
        System.out.println("-- handleListing");
        Main.changeMenu(Main.applicationScene);

        Main.listingApplyController.setLabels(listingTitle, listingLanguage);
    }
}
