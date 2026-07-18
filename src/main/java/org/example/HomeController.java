package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    static int i = 0;

    @FXML private AnchorPane contentArea;

    @FXML private VBox profileView;

    @FXML private ScrollPane myMeetupsListView;

    @FXML private TextField meetupTitleInput;

    @FXML private ComboBox<String> languageComboBox;

    @FXML private TextArea meetupDescriptionInput;

    @FXML private ToggleGroup locationToggleGroup;

    @FXML private javafx.scene.control.ToggleButton tagGameNight;
    @FXML private javafx.scene.control.ToggleButton tagCooking;
    @FXML private javafx.scene.control.ToggleButton tagSports;

    @FXML private VBox plannedMeetupsContainer;
    @FXML private VBox pastMeetupsContainer;
    @FXML private VBox applicationsContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (plannedMeetupsContainer != null) plannedMeetupsContainer.getChildren().clear();
        if (pastMeetupsContainer != null) pastMeetupsContainer.getChildren().clear();



        addMeetupToSidebar("Tuvalet Partisi 2", true);
        addMeetupToSidebar("Tuvalet Partisi", false);

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
    }

    public void handleSettings(ActionEvent actionEvent) {
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

    /**
     * Adds a new meetup button card to the left sidebar.
     * @param title The text displayed on the button.
     * @param isPlanned True if it goes under PLANNED (blue), false for PAST (gray).
     */
    public void addMeetupToSidebar(String title, boolean isPlanned) {
        Button meetupBtn = new Button(title);
        meetupBtn.setMaxWidth(Double.MAX_VALUE);
        meetupBtn.setMnemonicParsing(false);

        if (isPlanned) {
            meetupBtn.setStyle("-fx-background-color: #e3f2fd; -fx-background-radius: 10; -fx-text-fill: #0078fe; -fx-font-weight: bold; -fx-padding: 8; -fx-cursor: hand;");
        } else {
            meetupBtn.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10; -fx-text-fill: #555555; -fx-padding: 8; -fx-cursor: hand;");
        }

        meetupBtn.setOnAction(e -> {
            // Handle view change or details panel updates here
        });

        // Append to target section container
        if (isPlanned) {
            plannedMeetupsContainer.getChildren().add(meetupBtn);
        } else {
            pastMeetupsContainer.getChildren().add(meetupBtn);
        }
    }

    /**
     * Removes a meetup button by matching its text title label.
     */
    public void removeMeetupFromSidebar(String title, boolean isPlanned) {
        VBox targetContainer = isPlanned ? plannedMeetupsContainer : pastMeetupsContainer;
        targetContainer.getChildren().removeIf(node ->
                node instanceof Button && ((Button) node).getText().equals(title)
        );
    }



    /**
     * Generates and adds a detailed application info card to the right view panel
     */
    public void addIncomingApplication(String name, String meetupTopic) {
        // 1. Outer Container Bar
        HBox card = new HBox();
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        card.setSpacing(15);
        card.setPadding(new javafx.geometry.Insets(12, 20, 12, 20));
        card.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 15; -fx-border-color: #e0e0e0; -fx-border-radius: 15;");
        card.setMaxWidth(Double.MAX_VALUE);

        // 2. Profile Avatar Graphic
        Circle avatar = new Circle(20);
        avatar.setFill(Color.web("#e3f2fd"));
        avatar.setStroke(Color.web("#0078fe"));
        avatar.setStrokeWidth(1.5);

        // 3. Info Text Elements Label stack
        VBox textStack = new VBox();
        textStack.setSpacing(4);
        HBox.setHgrow(textStack, javafx.scene.layout.Priority.ALWAYS);

        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1a1a1a;");

        Label subLbl = new Label("Wants to join: " + meetupTopic);
        subLbl.setStyle("-fx-text-fill: #757575; -fx-font-size: 13px;");

        textStack.getChildren().addAll(nameLbl, subLbl);

        // 4. Action Button Dock Grouping (Accept / Deny Split)
        HBox actionsContainer = new HBox();
        actionsContainer.setAlignment(javafx.geometry.Pos.CENTER);
        actionsContainer.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-border-color: #e0e0e0; -fx-border-radius: 20;");
        actionsContainer.setPadding(new javafx.geometry.Insets(4, 8, 4, 8));

        Button acceptBtn = new Button("✓");
        acceptBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 4 12 4 12;");

        Line separator = new Line(0, 0, 0, 20);
        separator.setStroke(Color.web("#e0e0e0"));

        Button rejectBtn = new Button("✘");
        rejectBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 4 12 4 12;");

        // Connect action logic handles
        acceptBtn.setOnAction(e -> handleAcceptAction(name, card));
        rejectBtn.setOnAction(e -> handleRejectAction(name, card));

        actionsContainer.getChildren().addAll(acceptBtn, separator, rejectBtn);

        // Assemble components into the main card, then push card to screen container
        card.getChildren().addAll(avatar, textStack, actionsContainer);
        applicationsContainer.getChildren().add(card);
    }

    private void handleAcceptAction(String name, HBox cardNode) {
        System.out.println("Accepted application from: " + name);
        // Remove immediately from active visual array
        applicationsContainer.getChildren().remove(cardNode);
    }

    private void handleRejectAction(String name, HBox cardNode) {
        System.out.println("Rejected application from: " + name);
        // Remove immediately from active visual array
        applicationsContainer.getChildren().remove(cardNode);
    }

}

