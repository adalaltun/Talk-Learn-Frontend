package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ListingApplyController {

    @FXML private Button applyButton;
    @FXML private Button goBackButton;
    @FXML private TextArea applicationTextField;
    @FXML private Label applicationTitle;
    @FXML private Label applicationLanguage;

    @FXML
    public void handleApplication(ActionEvent actionEvent) {
        sendApplication();
        Main.displayAlert(Alert.AlertType.INFORMATION, "Application Success!",
                "Your application has been sent to the host. You will be added to chat if accepted.");
        Main.changeMenu(Main.discoveryMenu);
    }

    public void setLabels(Label listingTitle, Label listingLanguage) {
        this.applicationTitle.setText(listingTitle.getText());
        this.applicationLanguage.setText(listingLanguage.getText());
    }

    private void sendApplication() {
        try {
            String text = applicationTextField.getText().trim();
            String title = applicationTitle.getText();
            String jsonBody = String.format("{\"username\":\"%s\",\"listingTitle\":\"%s\",\"message\":\"%s\"}",
                    Main.loggedInUser, title, text);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/listings/apply"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleGoBack(ActionEvent actionEvent) {
        applyButton.getScene().lookup("#smallApplicationScreen").setVisible(false);
        applyButton.getScene().lookup("#smallApplicationScreen").setManaged(false);

        applyButton.getScene().lookup("#discoveryScrollPane").setVisible(true);
        applyButton.getScene().lookup("#discoveryScrollPane").setManaged(true);
    }
}