package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ListingApplyController {
    public Button applyButton, goBackButton;
    public TextField applicationDescriptionField;
    public Label applicationTitle, applicationLanguage;

    @FXML
    public void handleApplication(ActionEvent actionEvent) {
        Main.displayAlert(Alert.AlertType.INFORMATION, "Application Success!",
                "Your appliation has been sent to the host. You will be added to chat if accepted.");
        Main.changeMenu(Main.discoveryMenu);
    }

    @FXML
    public void handleGoBack(ActionEvent actionEvent) {
        Main.changeMenu(Main.discoveryMenu);
    }
}
