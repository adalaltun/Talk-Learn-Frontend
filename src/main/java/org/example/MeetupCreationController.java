package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.ArrayList;

public class MeetupCreationController {

    private static final String ADD_APPLICATION_URL = "";

    @FXML private TextField meetupTitleField;
    @FXML private TextField meetupLanguageField;
    @FXML private TextArea meetupDescField;
    private List<String> tags = new ArrayList<>();

    @FXML
    public void handleAddTag(ActionEvent event) {
        StackPane tagPart = (StackPane) ((Button) event.getSource()).getParent();
        Label tagLabel = (Label) tagPart.getChildren().get(1);

        tags.add(tagLabel.getText());
    }

    @FXML
    private void handleAddListing(ActionEvent event) {
        String title = meetupTitleField.getText();
        String language = meetupLanguageField.getText();
        String description = meetupDescField.getText();

        if (title.isBlank() || language.isBlank()) {

            String jsonbody = "{\"listingname\":%s, \"language\":%s, \"description\":%s, }"
                    .formatted(title, language, description);

            try {
                Main.sendHttpPOST(jsonbody, ADD_APPLICATION_URL);

            } catch (Exception e) {
                System.out.println("Couldn't connect to database.");
            }
        }
    }

    @FXML
    private void handlePublishMeetup(ActionEvent actionEvent) {

    }
}
