package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ListingApplyController {

    // VBox from MyMeetupsScreen in HomeMenu
    @FXML private VBox applicationsContainer;

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

        String title = applicationTitle.getText();
        String username = "deneme";

        System.out.println(title);
        System.out.println(username);

        HBox smallApplicationBox = (HBox) Main.loadFXML("smallApplicationBox");

        VBox applicationBox = (VBox) smallApplicationBox.getChildren().get(1);

        Label applicantName = (Label) applicationBox.getChildren().get(0);
        Label applicationTopic = (Label) applicationBox.getChildren().get(1);

        applicantName.setText(username);
        applicantName.setText("Wants to join: " + title);

        //burası sıkıntılı
        //myMeetups fxml içinden fx:id'si applicantionsContainer olanı alıyorum
        AnchorPane myMeetups = (AnchorPane) Main.myMeetupsScreen;
        ScrollPane applicationsPane =  (ScrollPane)(myMeetups.getChildren().getFirst());
        VBox applicationsContainer = (VBox)(applicationsPane.getContent());

        applicationsContainer.getChildren().add(smallApplicationBox);
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