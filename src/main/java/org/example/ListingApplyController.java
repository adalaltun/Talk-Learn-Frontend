package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea; // Changed from TextField

public class ListingApplyController {

    @FXML
    private Button applyButton;

    @FXML
    private Button goBackButton;

    @FXML
    private TextArea applicationTextField; // Fixed name and type to match FXML

    @FXML
    private Label applicationTitle;

    @FXML
    private Label applicationLanguage;

    @FXML
    public void handleApplication(ActionEvent actionEvent) {
        Main.displayAlert(Alert.AlertType.INFORMATION, "Application Success!",
                "Your application has been sent to the host. You will be added to chat if accepted.");
        Main.changeMenu(Main.discoveryMenu);
    }

    @FXML
    public void handleGoBack(ActionEvent actionEvent) {
        Main.changeMenu(Main.discoveryMenu);
    }
}