package org.example;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Callback;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

public class HomeController implements Initializable, ProfileUpdatable {

    public static Label smallApplicationText;
    public static Label smallApplicationUsername;

    @FXML private SplitPane rootPane;
    @FXML private AnchorPane contentArea;
    @FXML private VBox profileView;
    @FXML private Label profileName;
    @FXML private Label profileDate;
    @FXML private Circle profileCircle;
    @FXML private Label profileLetterLabel;
    @FXML private ScrollPane myMeetupsListView;
    @FXML private TextField meetupTitleInput;
    @FXML private ComboBox<String> languageComboBox;
    @FXML private TextArea meetupDescriptionInput;
    @FXML private ToggleGroup locationToggleGroup;
    @FXML private ToggleButton tagGameNight;
    @FXML private ToggleButton tagCooking;
    @FXML private ToggleButton tagSports;
    @FXML private VBox plannedMeetupsContainer;
    @FXML private VBox pastMeetupsContainer;
    @FXML private VBox applicationsContainer;
    @FXML private NavigationController navMenuController;

    private String titleStyle = "";
    private String subtitleStyle = "";
    private String bodyStyle = "";

    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label bodyLabel;
    @FXML private Label calendarMonthYearLabel;
    @FXML private GridPane calendarGrid;
    private YearMonth currentYearMonth = YearMonth.now();
    private Map<LocalDate, List<String>> plannedMeetupsMap = new HashMap<LocalDate, List<String>>();
    @FXML private DatePicker meetupDatePicker;

    @FXML private AnchorPane listingDetailView;
    @FXML private Label listingDetailTitle;
    @FXML private Label listingDetailRating;
    @FXML private Label listingDetailLanguage;
    @FXML private Label listingDetailType;
    @FXML private Label listingDetailPlace;
    @FXML private Label listingDetailSize;
    @FXML private Label listingDetailDate;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public void initialize(java.net.URL location, ResourceBundle resources) {
        if (navMenuController != null) {
            navMenuController.setActive("home");
        }

        if (profileName != null) {
            profileName.setText(Main.loggedInUser);
        }
        if (profileDate != null) {
            profileDate.setText(Main.accountCreationDate);
        }

        if (calendarGrid != null && calendarMonthYearLabel != null) {
            populateCalendar(currentYearMonth);
        }
        fetchMeetupsFromBackend();

        if (Main.selectedWallpaper != null && rootPane != null) {
            rootPane.setStyle("-fx-background-image: url('" + Main.selectedWallpaper + "'); " +
                    "-fx-background-size: cover; " +
                    "-fx-background-position: center;");
        }

        refreshPage();
    }

    public void refreshPage() {
        refreshWallpaperAndProfile();

        if (plannedMeetupsContainer != null) {
            plannedMeetupsContainer.getChildren().clear();
            addMeetupToSidebar("Spanish Meeting", true);
        }
        if (pastMeetupsContainer != null) {
            pastMeetupsContainer.getChildren().clear();
            addMeetupToSidebar("Japanese Talk", false);
        }
    }

    public void addMeetupToSidebar(String title, boolean isPlanned) {
        Button meetupBtn = new Button(title);
        meetupBtn.setMaxWidth(10000);
        meetupBtn.setMnemonicParsing(false);

        if (isPlanned) {
            meetupBtn.setStyle("-fx-background-color: #e3f2fd; -fx-background-radius: 10; -fx-text-fill: #0078fe; -fx-font-weight: bold; -fx-padding: 8; -fx-cursor: hand;");
        } else {
            meetupBtn.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10; -fx-text-fill: #555555; -fx-padding: 8; -fx-cursor: hand;");
        }

        if (isPlanned && plannedMeetupsContainer != null) {
            plannedMeetupsContainer.getChildren().add(meetupBtn);
        } else if (!isPlanned && pastMeetupsContainer != null) {
            pastMeetupsContainer.getChildren().add(meetupBtn);
        }
    }

    @Override
    public void refreshWallpaperAndProfile() {
        if (Main.selectedWallpaper != null && !Main.selectedWallpaper.isEmpty()) {
            rootPane.setStyle("-fx-background-image: url('" + Main.selectedWallpaper + "'); " +
                    "-fx-background-size: cover; " +
                    "-fx-background-position: center;");
        }

        if (Main.selectedAvatar != null && !Main.selectedAvatar.isEmpty()) {
            setProfilePicture(Main.selectedAvatar);
        }

        if (profileName != null) {
            profileName.setText(Main.loggedInUser);
            profileName.setStyle("-fx-font-family: 'System'; -fx-font-weight: bold; -fx-font-size: 24px; -fx-text-fill: white; -fx-effect: dropshadow(one-pass-box, black, 2.0, 1.0, 0, 0);");
        }
        if (profileDate != null) {
            profileDate.setText(Main.accountCreationDate);
            profileDate.setStyle("-fx-font-family: 'System'; -fx-font-size: 16px; -fx-text-fill: white; -fx-effect: dropshadow(one-pass-box, black, 1.5, 1.0, 0, 0);");
        }

        refreshTextColors();
    }

    public void handleMain(ActionEvent actionEvent) {
        if (profileView != null) {
            profileView.setVisible(true);
        }
        if (myMeetupsListView != null) {
            myMeetupsListView.setVisible(false);
        }
        refreshPage();
    }

    public void refreshTextColors() {
        if (titleLabel == null || subtitleLabel == null || bodyLabel == null) {
            return;
        }

        String titleStyleStr;
        String subtitleStyleStr;
        String bodyStyleStr;

        if (Main.selectedWallpaper == null || Main.selectedWallpaper.contains("wp1.jpg") || Main.selectedWallpaper.isEmpty()) {
            titleStyleStr = "-fx-text-fill: black; -fx-effect: dropshadow(gaussian, white, 10, 0.4, 3, 0);";
            subtitleStyleStr = "-fx-text-fill: black; -fx-effect: dropshadow(gaussian, white, 8, 0.4, 2, 0);";
            bodyStyleStr = "-fx-text-fill: black; -fx-effect: dropshadow(gaussian, white, 8, 0.4, 1.5, 0);";
        } else {
            titleStyleStr = "-fx-text-fill: white; -fx-effect: dropshadow(gaussian, black, 10, 0.2, 3, 0);";
            subtitleStyleStr = "-fx-text-fill: white; -fx-effect: dropshadow(gaussian, black, 8, 0.2, 2, 0);";
            bodyStyleStr = "-fx-text-fill: white; -fx-effect: dropshadow(gaussian, black, 8, 0.2, 1.5, 0);";
        }

        titleLabel.setStyle(titleStyleStr);
        subtitleLabel.setStyle(subtitleStyleStr);
        bodyLabel.setStyle(bodyStyleStr);
    }

    public void handleSignUp(ActionEvent actionEvent) {}

    public void handleDiscovery(ActionEvent actionEvent) {
        Main.changeMenu(Main.discoveryMenu);
    }

    public void handleChat(ActionEvent actionEvent) {
        Main.changeMenu(Main.chatMenu);
    }

    public void handleLeaderboard(ActionEvent actionEvent) {
        if (Main.leaderboardController != null) {
            Main.leaderboardController.refreshData();
        }
        Main.changeMenu(Main.leaderboardScreen);
    }

    public void handleSettings(ActionEvent actionEvent) {
        Main.changeMenu(Main.settingsScreen);
    }

    public void handleCreateMeetup(ActionEvent actionEvent) {
        profileView.setVisible(true);
        myMeetupsListView.setVisible(false);
        listingDetailView.setVisible(false);
        changeView("/MeetupCreationScreen.fxml");
    }

    public void handleMyMeetups(ActionEvent actionEvent) {
        if (profileView != null) {
            profileView.setVisible(false);
        }
        if (myMeetupsListView != null) {
            myMeetupsListView.setVisible(true);
        }
        if (listingDetailView != null) {
            listingDetailView.setVisible(false);
        }

        changeView("/MyMeetupsScreen.fxml");
        fetchIncomingApplicationsFromBackend();
    }

    public void fetchIncomingApplicationsFromBackend() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/listings/applications?hostUsername=" + Main.loggedInUser))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(new Consumer<HttpResponse<String>>() {
                    @Override
                    public void accept(final HttpResponse<String> response) {
                        if (response.statusCode() == 200) {
                            try {
                                JsonMapper mapper = JsonMapper.builder().build();
                                final List<Map<String, String>> apps = mapper.readValue(response.body(), new TypeReference<List<Map<String, String>>>() {});

                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (applicationsContainer != null) {
                                            List<Node> nodesToRemove = new ArrayList<Node>();
                                            for (Node node : applicationsContainer.getChildren()) {
                                                if (node instanceof HBox) {
                                                    nodesToRemove.add(node);
                                                }
                                            }
                                            applicationsContainer.getChildren().removeAll(nodesToRemove);

                                            for (Map<String, String> app : apps) {
                                                String applicant = app.get("username");
                                                String topic = app.get("listingTitle");
                                                String appId = String.valueOf(app.get("id"));
                                                addIncomingApplicationCard(applicant, topic, appId);
                                            }
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    public void addIncomingApplicationCard(final String name, final String meetupTopic, final String appId) {
        if (applicationsContainer == null) {
            return;
        }

        final HBox card = new HBox();
        card.setAlignment(Pos.CENTER_LEFT);
        card.setSpacing(15);
        card.setPadding(new Insets(12, 20, 12, 20));
        card.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 15; -fx-border-color: #e0e0e0; -fx-border-radius: 15;");
        card.setMaxWidth(10000);

        Circle avatar = new Circle(20);
        avatar.setFill(Color.web("#e3f2fd"));
        avatar.setStroke(Color.web("#0078fe"));
        avatar.setStrokeWidth(1.5);

        VBox textStack = new VBox();
        textStack.setSpacing(4);
        HBox.setHgrow(textStack, Priority.ALWAYS);

        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1a1a1a;");

        String displayTopic = meetupTopic.startsWith("Wants to join:") ? meetupTopic : "Wants to join: " + meetupTopic;
        Label subLbl = new Label(displayTopic);
        subLbl.setStyle("-fx-text-fill: #757575; -fx-font-size: 13px;");

        textStack.getChildren().addAll(nameLbl, subLbl);

        HBox actionsContainer = new HBox();
        actionsContainer.setAlignment(Pos.CENTER);
        actionsContainer.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-border-color: #e0e0e0; -fx-border-radius: 20;");
        actionsContainer.setPadding(new Insets(4, 8, 4, 8));

        Button acceptBtn = new Button("✓");
        acceptBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 4 12 4 12;");

        Line separator = new Line(0, 0, 0, 20);
        separator.setStroke(Color.web("#e0e0e0"));

        Button rejectBtn = new Button("✘");
        rejectBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 4 12 4 12;");

        acceptBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleAcceptAction(name, meetupTopic, appId, card);
            }
        });

        rejectBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleRejectAction(name, meetupTopic, card);
            }
        });

        actionsContainer.getChildren().addAll(acceptBtn, separator, rejectBtn);
        card.getChildren().addAll(avatar, textStack, actionsContainer);

        applicationsContainer.getChildren().add(card);
    }

    public void addIncomingApplication(String name, String meetupTopic) {
        addIncomingApplication(name, meetupTopic, null);
    }

    public void addIncomingApplication(String name, String meetupTopic, String appId) {
        if (applicationsContainer == null) {
            handleMyMeetups(null);
        }

        if (applicationsContainer != null) {
            addIncomingApplicationCard(name, meetupTopic, appId);
        }
    }

    public void handleSavedMeetups(ActionEvent actionEvent) {
        profileView.setVisible(true);
        myMeetupsListView.setVisible(false);
        listingDetailView.setVisible(false);
        changeView("/SavedMeetupsScreen.fxml");
    }

    public void handleListingDetail(ActionEvent actionEvent) {
        HBox parentBox = (HBox) (((Button) actionEvent.getSource()).getParent());
        HBox listingDetailBox = (HBox) parentBox.getChildren().getLast();

        profileView.setVisible(false);
        listingDetailView.setVisible(true);

        Label favListingTitle = (Label) listingDetailBox.getChildren().get(0);
        Label favListingLanguage = (Label) listingDetailBox.getChildren().get(1);
        Label favListingSize = (Label) listingDetailBox.getChildren().get(2);

        listingDetailTitle.setText(favListingTitle.getText());
        listingDetailLanguage.setText(favListingLanguage.getText());
        listingDetailSize.setText(favListingSize.getText());
    }

    public void handleCalendar(ActionEvent actionEvent) {
        profileView.setVisible(false);
        myMeetupsListView.setVisible(false);
    }

    public void setProfilePicture(String imagePath) {
        try {
            Image img = new Image(imagePath);
            ImagePattern imgPattern = new ImagePattern(img);
            profileCircle.setFill(imgPattern);
            profileLetterLabel.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWindowWallpaper(String imagePath) {
        try {
            if (!imagePath.startsWith("/")) {
                imagePath = "/" + imagePath;
            }
            rootPane.setStyle(
                    "-fx-background-image: url('" + imagePath + "'); " +
                            "-fx-background-position: center center; " +
                            "-fx-background-repeat: no-repeat; " +
                            "-fx-background-size: cover;"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(new Callback<Class<?>, Object>() {
                @Override
                public Object call(Class<?> param) {
                    if (param == HomeController.class) {
                        return HomeController.this;
                    }
                    try {
                        return param.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            Parent newView = loader.load();
            contentArea.getChildren().clear();

            AnchorPane.setTopAnchor(newView, 0.0);
            AnchorPane.setBottomAnchor(newView, 0.0);
            AnchorPane.setLeftAnchor(newView, 0.0);
            AnchorPane.setRightAnchor(newView, 0.0);

            contentArea.getChildren().add(newView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handlePlannedMeetupClick(ActionEvent actionEvent) {}
    public void handleOldMeetupClick(ActionEvent actionEvent) {}

    @FXML
    public static void handleAcceptApplication(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();
        btn.setDisable(true);
    }

    public void handleProcessApplication(Long applicationId, final String applicantUsername, final String meetupTopic, final HBox cardNode, final Button acceptBtn) {
        if (acceptBtn != null) {
            acceptBtn.setDisable(true);
        }

        String url = "http://localhost:8080/api/applications/accept/" + applicationId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(new Consumer<HttpResponse<String>>() {
                    @Override
                    public void accept(final HttpResponse<String> response) {
                        if (response.statusCode() == 200) {
                            if (applicationsContainer != null && cardNode != null) {
                                applicationsContainer.getChildren().remove(cardNode);
                            }

                            Main.displayAlert(Alert.AlertType.INFORMATION, "Application Accepted",
                                    applicantUsername + " has been added to chat for '" + meetupTopic + "'!");
                        } else {
                            Main.displayAlert(Alert.AlertType.ERROR, "Error", "Failed to accept application: " + response.body());
                            if (acceptBtn != null) {
                                acceptBtn.setDisable(false);
                            }
                        }
                    }
                });
    }

    public void handleAcceptAction(final String name, final String meetupTopic, String appId, final HBox cardNode) {
        if (applicationsContainer != null && cardNode != null) {
            applicationsContainer.getChildren().remove(cardNode);
        }

        if (appId == null || appId.equalsIgnoreCase("null") || appId.isEmpty()) {
            Main.displayAlert(Alert.AlertType.ERROR, "Error", "Application ID is missing. Cannot accept.");
            return;
        }

        try {
            String url = "http://localhost:8080/api/listings/applications/accept/" + appId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(new Consumer<HttpResponse<String>>() {
                        @Override
                        public void accept(final HttpResponse<String> response) {
                            if (response.statusCode() == 200) {
                                Main.displayAlert(Alert.AlertType.INFORMATION, "Application Accepted",
                                        name + " has been added to the chat for '" + meetupTopic + "'!");
                            } else {
                                Main.displayAlert(Alert.AlertType.ERROR, "Error",
                                        "Failed to accept application: " + response.body());
                            }
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void handleRejectAction(String name, String meetupTopic, HBox cardNode) {
        if (applicationsContainer != null && cardNode != null) {
            applicationsContainer.getChildren().remove(cardNode);
        }

        try {
            String cleanTitle = meetupTopic.replace("Wants to join:", "").trim();
            String encodedTitle = URLEncoder.encode(cleanTitle, StandardCharsets.UTF_8);
            String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);

            String url = String.format("http://localhost:8080/api/listings/applications/reject?applicantUsername=%s&listingTitle=%s",
                    encodedName, encodedTitle);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .DELETE()
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void handleRejectApplication(ActionEvent actionEvent) {
        if (actionEvent != null && actionEvent.getSource() instanceof Button) {
            Button btn = (Button) actionEvent.getSource();
            btn.setDisable(true);
        }
    }

    @FXML
    private void handlePrevMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        populateCalendar(currentYearMonth);
    }

    @FXML
    private void handleNextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        populateCalendar(currentYearMonth);
    }

    private void fetchMeetupsFromBackend() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/listings/all"))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(new Consumer<HttpResponse<String>>() {
                    @Override
                    public void accept(HttpResponse<String> response) {
                        if (response.statusCode() == 200) {
                            try {
                                JsonMapper mapper = JsonMapper.builder().build();
                                List<String> listingsJsonList = mapper.readValue(response.body(), new TypeReference<List<String>>() {});

                                plannedMeetupsMap.clear();

                                for (int i = 0; i < listingsJsonList.size(); i++) {
                                    String jsonString = listingsJsonList.get(i);
                                    JsonNode node = mapper.readTree(jsonString);

                                    if (node.has("listingname") && node.has("meetupDate") && !node.get("meetupDate").isNull()) {
                                        String meetupName = node.get("listingname").asText();
                                        JsonNode dateNode = node.get("meetupDate");

                                        LocalDate meetupDate;
                                        if (dateNode.isArray()) {
                                            meetupDate = LocalDate.of(
                                                    dateNode.get(0).asInt(),
                                                    dateNode.get(1).asInt(),
                                                    dateNode.get(2).asInt()
                                            );
                                        } else {
                                            meetupDate = LocalDate.parse(dateNode.asText());
                                        }

                                        if (!plannedMeetupsMap.containsKey(meetupDate)) {
                                            plannedMeetupsMap.put(meetupDate, new ArrayList<String>());
                                        }
                                        plannedMeetupsMap.get(meetupDate).add(meetupName);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    public void handlePublishMeetup(ActionEvent actionEvent) {
        if (meetupDatePicker.getValue() == null) {
            return;
        }

        String selectedDate = meetupDatePicker.getValue().toString();
        String title = meetupTitleInput.getText();
        String language = languageComboBox.getValue();
        String description = meetupDescriptionInput.getText();

        ToggleButton selectedLocation = (ToggleButton) locationToggleGroup.getSelectedToggle();
        String location = (selectedLocation != null) ? selectedLocation.getText() : "None";

        List<String> selectedTags = new ArrayList<String>();
        if (tagGameNight != null && tagGameNight.isSelected()) {
            selectedTags.add(tagGameNight.getText());
        }
        if (tagCooking != null && tagCooking.isSelected()) {
            selectedTags.add(tagCooking.getText());
        }
        if (tagSports != null && tagSports.isSelected()) {
            selectedTags.add(tagSports.getText());
        }

        StringBuilder tagsJsonBuilder = new StringBuilder("[");
        for (int i = 0; i < selectedTags.size(); i++) {
            tagsJsonBuilder.append("\"").append(selectedTags.get(i).replace("\"", "\\\"")).append("\"");
            if (i < selectedTags.size() - 1) {
                tagsJsonBuilder.append(", ");
            }
        }
        tagsJsonBuilder.append("]");
        String tagsJson = tagsJsonBuilder.toString();

        String jsonPayload = String.format(
                "{\n" +
                        "  \"listingname\": \"%s\",\n" +
                        "  \"language\": \"%s\",\n" +
                        "  \"listingdescription\": \"%s\",\n" +
                        "  \"location\": \"%s\",\n" +
                        "  \"meetupDate\": \"%s\",\n" +
                        "  \"date\": \"%s\",\n" +
                        "  \"participantLimit\": 8,\n" +
                        "  \"tags\": %s\n" +
                        "}",
                title.replace("\"", "\\\""),
                (language != null) ? language.replace("\"", "\\\"") : "",
                description.replace("\"", "\\\""),
                location.replace("\"", "\\\""),
                selectedDate,
                selectedDate,
                tagsJson
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/listings/add?username=" + Main.loggedInUser))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        meetupTitleInput.clear();
        meetupDescriptionInput.clear();
        languageComboBox.setValue(null);

        if (locationToggleGroup.getSelectedToggle() != null) {
            locationToggleGroup.getSelectedToggle().setSelected(false);
        }

        if (tagGameNight != null) {
            tagGameNight.setSelected(false);
        }
        if (tagCooking != null) {
            tagCooking.setSelected(false);
        }
        if (tagSports != null) {
            tagSports.setSelected(false);
        }

        if (profileView != null) {
            profileView.setVisible(true);
        }
        if (myMeetupsListView != null) {
            myMeetupsListView.setVisible(false);
        }

        if (contentArea != null) {
            contentArea.getChildren().clear();
        }
    }

    public void removeMeetupFromSidebar(String title, boolean isPlanned) {
        VBox targetContainer = isPlanned ? plannedMeetupsContainer : pastMeetupsContainer;
        if (targetContainer == null) {
            return;
        }

        List<Node> nodesToRemove = new ArrayList<Node>();
        for (Node node : targetContainer.getChildren()) {
            if (node instanceof Button && ((Button) node).getText().equals(title)) {
                nodesToRemove.add(node);
            }
        }
        targetContainer.getChildren().removeAll(nodesToRemove);
    }

    private void populateCalendar(YearMonth yearMonth) {
        calendarGrid.getChildren().clear();

        calendarMonthYearLabel.setText(yearMonth.getMonth().toString().substring(0, 1) +
                yearMonth.getMonth().toString().substring(1).toLowerCase() +
                " " + yearMonth.getYear());

        String[] daysOfWeek = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < daysOfWeek.length; i++) {
            Label dayLabel = new Label(daysOfWeek[i]);
            dayLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
            calendarGrid.add(dayLabel, i, 0);
            GridPane.setHalignment(dayLabel, javafx.geometry.HPos.CENTER);
        }

        LocalDate firstOfMonth = yearMonth.atDay(1);
        int dayOfWeekOfFirst = firstOfMonth.getDayOfWeek().getValue();
        int daysInMonth = yearMonth.lengthOfMonth();

        int row = 1;
        int col = dayOfWeekOfFirst - 1;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate currentDate = yearMonth.atDay(day);

            VBox dayPane = new VBox();
            dayPane.setAlignment(Pos.TOP_CENTER);
            dayPane.setPadding(new Insets(5));
            dayPane.setSpacing(4);

            Label dateLabel = new Label(String.valueOf(day));

            if (currentDate.equals(LocalDate.now())) {
                dayPane.setStyle("-fx-background-color: #e3f2fd; -fx-background-radius: 10; -fx-border-color: #0078fe; -fx-border-radius: 10; -fx-cursor: hand;");
                dateLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0078fe;");
            } else {
                dayPane.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10; -fx-cursor: hand;");
                dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #1a1a1a;");
            }

            dayPane.getChildren().add(dateLabel);

            if (plannedMeetupsMap.containsKey(currentDate)) {
                List<String> meetups = plannedMeetupsMap.get(currentDate);
                for (int j = 0; j < meetups.size(); j++) {
                    String meetupName = meetups.get(j);
                    Label meetupLabel = new Label(meetupName);
                    meetupLabel.setStyle("-fx-background-color: #0078fe; -fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 2 5 2 5;");
                    meetupLabel.setMaxWidth(Double.MAX_VALUE);
                    meetupLabel.setAlignment(Pos.CENTER);
                    dayPane.getChildren().add(meetupLabel);
                }
            }

            calendarGrid.add(dayPane, col, row);

            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }
    }
}