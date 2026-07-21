package org.example;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

public class ListingApplyController {

    @FXML private Button applyButton;
    @FXML private Button goBackButton;

    @FXML private TextArea applicationTextField;
    @FXML private Label applicationTitle;
    @FXML private Label applicationLanguage;

    private String hostUsername;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    public void handleApplication(ActionEvent actionEvent) {
        if (Main.loggedInUser != null && Main.loggedInUser.equalsIgnoreCase(hostUsername)) {
            Main.displayAlert(Alert.AlertType.WARNING, "Warning", "You cannot apply to your own meetup listing!");
            return;
        }

        sendApplication();
    }

    private void sendApplication() {
        try {
            String text = (applicationTextField != null) ? applicationTextField.getText().trim() : "";
            String title = (applicationTitle != null) ? applicationTitle.getText() : "";
            String jsonBody = String.format("{\"username\":\"%s\",\"listingTitle\":\"%s\",\"message\":\"%s\"}",
                    Main.loggedInUser, title, text);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/listings/apply"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(new Consumer<HttpResponse<String>>() {
                        @Override
                        public void accept(final HttpResponse<String> response) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    if (response.statusCode() == 200) {
                                        Main.displayAlert(Alert.AlertType.INFORMATION, "Application Success!",
                                                "Your application has been sent to the host.");
                                        Main.changeMenu(Main.homeMenu);
                                        if (Main.homeController != null) {
                                            Main.homeController.handleMyMeetups(null);
                                        }
                                    } else {
                                        Main.displayAlert(Alert.AlertType.WARNING, "Application Failed", response.body());
                                    }
                                }
                            });
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLabels(Label listingTitle, Label listingLanguage, String hostUser) {
        if (this.applicationTitle != null && listingTitle != null) {
            this.applicationTitle.setText(listingTitle.getText());
        }
        if (this.applicationLanguage != null && listingLanguage != null) {
            this.applicationLanguage.setText(listingLanguage.getText());
        }
        this.hostUsername = hostUser;
    }

    @FXML
    public void handleGoBack(ActionEvent actionEvent) {
        if (applyButton != null && applyButton.getScene() != null) {
            Node appScreen = applyButton.getScene().lookup("#smallApplicationScreen");
            Node discPane = applyButton.getScene().lookup("#discoveryScrollPane");

            if (appScreen != null) {
                appScreen.setVisible(false);
                appScreen.setManaged(false);
            }
            if (discPane != null) {
                discPane.setVisible(true);
                discPane.setManaged(true);
            }
        }
    }
}