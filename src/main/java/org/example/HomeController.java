package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class HomeController {

    static int i = 0;

    @FXML
    private AnchorPane contentArea;

    @FXML
    private VBox profileView;

    @FXML
    private ScrollPane myMeetupsListView;

    @FXML
    private TextField meetupTitleInput;

    @FXML
    private ComboBox<String> languageComboBox;

    @FXML
    private TextArea meetupDescriptionInput;

    @FXML
    private ToggleGroup locationToggleGroup;

    @FXML
    private javafx.scene.control.ToggleButton tagGameNight;

    @FXML
    private javafx.scene.control.ToggleButton tagCooking;

    @FXML
    private javafx.scene.control.ToggleButton tagSports;
    @FXML private NavigationController navMenuController;
    @FXML
    public void initialize() {
        if (navMenuController != null) {
            navMenuController.setActive("home");
        }
    }

    public void handleMain(ActionEvent actionEvent) {

        profileView.setVisible(true);
        myMeetupsListView.setVisible(false);

        contentArea.getChildren().clear();
    }

    public void handleSignUp(ActionEvent actionEvent) {
    }

    public void handleDiscovery(ActionEvent actionEvent) {
        Main.changeMenu(Main.discoveryMenu);
    }

    public void handleChat(ActionEvent actionEvent) {
        Main.changeMenu(Main.chatMenu);
    }

    public void handleLeaderboard(ActionEvent actionEvent) {
        Main.changeMenu(Main.leaderboardMenu);
    }

    public void handleSettings(ActionEvent actionEvent) {
        Main.changeMenu(Main.settingsMenu);
    }

    public void handleDrag(MouseEvent e) {
    }

    public void handleCreateMeetup(ActionEvent actionEvent) {
        profileView.setVisible(true);
        myMeetupsListView.setVisible(false);
        changeView("/MeetupCreationScreen.fxml");
    }

    public void handleMyMeetups(ActionEvent actionEvent) {
        profileView.setVisible(false);
        myMeetupsListView.setVisible(true);
        changeView("/MyMeetupsScreen.fxml");
    }

    public void handleSavedMeetups(ActionEvent actionEvent) {
        profileView.setVisible(true);
        myMeetupsListView.setVisible(false);
        changeView("/SavedMeetupsScreen.fxml");
    }

    private void changeView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            loader.setControllerFactory(param -> {
                if (param == HomeController.class) {
                    return this;
                }
                try {
                    return param.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            Parent newView = loader.load();

            contentArea.getChildren().clear();

            AnchorPane.setTopAnchor(newView, 0.0);
            AnchorPane.setBottomAnchor(newView, 0.0);
            AnchorPane.setLeftAnchor(newView, 0.0);
            AnchorPane.setRightAnchor(newView, 0.0);

            contentArea.getChildren().add(newView);

        } catch (IOException e) {
            System.err.println("Could not swap view: " + fxmlPath);
        }
    }

    public void handlePlannedMeetupClick(ActionEvent actionEvent) {

    }

    public void handleOldMeetupClick(ActionEvent actionEvent) {

    }

    public void handleAcceptApplication(ActionEvent actionEvent) {
    }

    public void handleRejectApplication(ActionEvent actionEvent) {
    }

    public void handlePublishMeetup(ActionEvent actionEvent) {
        // Read the values filled by the user
        String title = meetupTitleInput.getText();
        String language = languageComboBox.getValue();
        String description = meetupDescriptionInput.getText();

        // Check which location toggle button was selected
        javafx.scene.control.ToggleButton selectedLocation =
                (javafx.scene.control.ToggleButton) locationToggleGroup.getSelectedToggle();

        String location = (selectedLocation != null) ? selectedLocation.getText() : "None";

        java.util.List<String> selectedTags = new java.util.ArrayList<>();

        if (tagGameNight != null && tagGameNight.isSelected()) {
            selectedTags.add(tagGameNight.getText());
        }
        if (tagCooking != null && tagCooking.isSelected()) {
            selectedTags.add(tagCooking.getText());
        }
        if (tagSports != null && tagSports.isSelected()) {
            selectedTags.add(tagSports.getText());
        }


        //Values: title (string), language (string), description (string), location (string) and selected tags (arraylist) will be used to create a listing under here



        System.out.println("Publishing Meetup: " + title + " [" + language + "] at " + location);
        System.out.println("Selected Tags: " + selectedTags);
        System.out.println("Saved Meetup: " + title);

        meetupTitleInput.clear();
        meetupDescriptionInput.clear();
        languageComboBox.setValue(null);

        if (locationToggleGroup.getSelectedToggle() != null) {
            locationToggleGroup.getSelectedToggle().setSelected(false);
        }

        if (tagGameNight != null) tagGameNight.setSelected(false);
        if (tagCooking != null) tagCooking.setSelected(false);
        if (tagSports != null) tagSports.setSelected(false);

        if (profileView != null) profileView.setVisible(true);
        if (myMeetupsListView != null) myMeetupsListView.setVisible(false);

        if (contentArea != null) {
            contentArea.getChildren().clear();
        }








    }
}

