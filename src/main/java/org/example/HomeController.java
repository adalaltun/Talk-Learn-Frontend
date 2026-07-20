package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import javafx.application.Platform;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URL;

public class HomeController implements Initializable, ProfileUpdatable {

    static int i = 0;

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

    String titleStyle = "", subtitleStyle = "", bodyStyle = "";
    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label bodyLabel;
    @FXML private Label calendarMonthYearLabel;
    @FXML private GridPane calendarGrid;
    private YearMonth currentYearMonth = YearMonth.now();
    private Map<LocalDate, List<String>> plannedMeetupsMap = new HashMap<>();
    @FXML private javafx.scene.control.DatePicker meetupDatePicker;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (navMenuController != null) {
            navMenuController.setActive("home");
        }

        profileName.setText(Main.loggedInUser);
        profileDate.setText(Main.accountCreationDate);
        if (calendarGrid != null && calendarMonthYearLabel != null) {
            populateCalendar(currentYearMonth);
        }
        fetchMeetupsFromBackend();



        if (Main.selectedWallpaper != null) {
            URL bgURL = getClass().getResource(Main.selectedWallpaper);
            if (bgURL != null) {
                rootPane.setStyle("-fx-background-image: url('" + bgURL.toExternalForm() + "'); " +
                        "-fx-background-size: cover; " +
                        "-fx-background-position: center;");
            }
        }


        profileName.setText("Zenco");
        profileDate.setText("Lvl 99 ngga");

        setProfilePicture("/images/defaultProfilePhoto.png");
        //setWindowWallpaper("/images/wallpaper.jpg");
        refreshPage();

    }


    public void refreshPage() {
        refreshWallpaperAndProfile();

        if (plannedMeetupsContainer != null) {
            plannedMeetupsContainer.getChildren().clear();
        }
        if (pastMeetupsContainer != null) {
            pastMeetupsContainer.getChildren().clear();
        }

        addMeetupToSidebar("Spanish Meeting", true);
        addMeetupToSidebar("Japanese Talk", false);
    }

    @Override
    public void refreshWallpaperAndProfile() {
        if (Main.selectedWallpaper != null && !Main.selectedWallpaper.isEmpty()) {
            URL bgURL = getClass().getResource(Main.selectedWallpaper);
            if (bgURL != null) {
                rootPane.setStyle("-fx-background-image: url('" + bgURL.toExternalForm() + "'); " +
                        "-fx-background-size: cover; " +
                        "-fx-background-position: center;");
            }
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
        if (titleLabel == null || subtitleLabel == null || bodyLabel == null) return;

        String titleStyleStr, subtitleStyleStr, bodyStyleStr;

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

    private void loadWelcomeMessage() {
        if (contentArea == null) {
            return;
        }
        contentArea.getChildren().clear();

        VBox welcomeBox = new VBox();
        welcomeBox.setAlignment(Pos.CENTER);
        welcomeBox.setSpacing(25.0);
        welcomeBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.25); -fx-background-radius: 20; -fx-border-color: rgba(224, 224, 224, 0.5); -fx-border-radius: 20;");
        welcomeBox.setPadding(new Insets(40, 40, 40, 40));

        AnchorPane.setTopAnchor(welcomeBox, 40.0);
        AnchorPane.setBottomAnchor(welcomeBox, 60.0);
        AnchorPane.setLeftAnchor(welcomeBox, 40.0);
        AnchorPane.setRightAnchor(welcomeBox, 40.0);

        if (Main.selectedWallpaper == null || Main.selectedWallpaper.contains("wp1.jpg") || Main.selectedWallpaper.isEmpty()) {
            titleStyle += "-fx-text-fill: black; -fx-effect: dropshadow(gaussian, white, 10, 0.4, 3, 0);";
            subtitleStyle += "-fx-text-fill: black; -fx-effect: dropshadow(gaussian, white, 8, 0.4, 2, 0);";
            bodyStyle += "-fx-text-fill: black; -fx-effect: dropshadow(gaussian, white, 8, 0.4, 1.5, 0);";
        } else if (Main.selectedWallpaper.contains("wp2.jpg") || Main.selectedWallpaper.contains("wp3.jpg")) {
            titleStyle += "-fx-text-fill: white; -fx-effect: dropshadow(gaussian, black, 10, 0.2, 3, 0);";
            subtitleStyle += "-fx-text-fill: white; -fx-effect: dropshadow(gaussian, black, 8, 0.2, 2, 0);";
            bodyStyle += "-fx-text-fill: white; -fx-effect: dropshadow(gaussian, black, 8, 0.2, 1.5, 0);";
        }

        titleLabel = new Label("Talk & Learn");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 42.0));
        titleLabel.setStyle(titleStyle);

        subtitleLabel = new Label("Welcome to the ultimate language exchange platform!");
        subtitleLabel.setFont(Font.font("System", FontWeight.BOLD, 20.0));
        subtitleLabel.setStyle(subtitleStyle);

        bodyLabel = new Label("Connect with people around the world, join active language sessions, or create your own meetings. Improve your communication skills naturally while expanding your network.\n\nUse the sidebar to manage your meetups, check out your favorites, or find ongoing talks near you.");
        bodyLabel.setFont(Font.font("System", FontWeight.BOLD, 16.0));
        bodyLabel.setPrefWidth(500.0);
        bodyLabel.setWrapText(true);
        bodyLabel.setTextAlignment(TextAlignment.CENTER);
        bodyLabel.setLineSpacing(6.0);
        bodyLabel.setStyle(bodyStyle);

        refreshTextColors();

        welcomeBox.getChildren().addAll(titleLabel, subtitleLabel, bodyLabel);
        contentArea.getChildren().add(welcomeBox);
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

    public void setProfilePicture(String imagePath) {
        try {
            Image img = new Image(imagePath);
            ImagePattern imgPattern = new ImagePattern(img);

            profileCircle.setFill(imgPattern);

            profileLetterLabel.setVisible(false);

        } catch (Exception e) {
            System.err.println("Could not load profile image: " + imagePath);
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
            System.err.println("Could not load global wallpaper: " + imagePath);
            e.printStackTrace();
        }
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

    public void handlePlannedMeetupClick(ActionEvent actionEvent) {}
    public void handleOldMeetupClick(ActionEvent actionEvent) {}

    public void handleAcceptApplication(ActionEvent actionEvent) {}
    public void handleRejectApplication(ActionEvent actionEvent) {}

    public void handlePublishMeetup(ActionEvent actionEvent) {
        // Read the values filled by the user
        if (meetupDatePicker.getValue() == null) {
            System.out.println("Please select a date!");
            return;
        }

        String selectedDate = meetupDatePicker.getValue().toString();
        String title = meetupTitleInput.getText();
        String language = languageComboBox.getValue();
        String description = meetupDescriptionInput.getText();

        ToggleButton selectedLocation = (ToggleButton) locationToggleGroup.getSelectedToggle();
        String location = (selectedLocation != null) ? selectedLocation.getText() : "None";

        List<String> selectedTags = new ArrayList<>();

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

        String tagsJson = selectedTags.stream()
                .map(tag -> "\"" + tag.replace("\"", "\\\"") + "\"")
                .collect(Collectors.joining(", ", "[", "]"));

        String jsonPayload = String.format(
                "{\n" +
                        "  \"listingname\": \"%s\",\n" +
                        "  \"language\": \"%s\",\n" +
                        "  \"listingdescription\": \"%s\",\n" +
                        "  \"location\": \"%s\",\n" +
                        "  \"tags\": %s\n" +
                        "}",
                title.replace("\"", "\\\""),
                (language != null) ? language.replace("\"", "\\\"") : "",
                description.replace("\"", "\\\""),
                location.replace("\"", "\\\""),
                tagsJson
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/listings/add?username=" + Main.loggedInUser))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        System.out.println("Sending Listing to backend...");

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    // Network responses run on a background thread.
                    // Use Platform.runLater to interact with UI components safely.
                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            System.out.println("Backend Success: " + response.body());
                        } else {
                            System.err.println("Backend Error (" + response.statusCode() + "): " + response.body());
                        }
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        System.err.println("Failed to connect to backend: " + ex.getMessage());
                    });
                    return null;
                });




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

    public void addMeetupToSidebar(String title, boolean isPlanned) {
        Button meetupBtn = new Button(title);
        meetupBtn.setMaxWidth(10000);
        meetupBtn.setMnemonicParsing(false);

        if (isPlanned) {
            meetupBtn.setStyle("-fx-background-color: #e3f2fd; -fx-background-radius: 10; -fx-text-fill: #0078fe; -fx-font-weight: bold; -fx-padding: 8; -fx-cursor: hand;");
        } else {
            meetupBtn.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10; -fx-text-fill: #555555; -fx-padding: 8; -fx-cursor: hand;");
        }

        meetupBtn.setOnAction(e -> {});

        // Append to target section container
        if (isPlanned) {
            plannedMeetupsContainer.getChildren().add(meetupBtn);
        } else {
            pastMeetupsContainer.getChildren().add(meetupBtn);
        }
    }

    public void removeMeetupFromSidebar(String title, boolean isPlanned) {
        VBox targetContainer = isPlanned ? plannedMeetupsContainer : pastMeetupsContainer;
        targetContainer.getChildren().removeIf(node ->
                node instanceof Button && ((Button) node).getText().equals(title)
        );
    }

    public void addIncomingApplication(String name, String meetupTopic) {
        HBox card = new HBox();
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

        Label subLbl = new Label("Wants to join: " + meetupTopic);
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

        acceptBtn.setOnAction(e -> handleAcceptAction(name, card));
        rejectBtn.setOnAction(e -> handleRejectAction(name, card));

        actionsContainer.getChildren().addAll(acceptBtn, separator, rejectBtn);

        card.getChildren().addAll(avatar, textStack, actionsContainer);
        applicationsContainer.getChildren().add(card);
    }

    private void handleAcceptAction(String name, HBox cardNode) {
        System.out.println("Accepted application from: " + name);
        applicationsContainer.getChildren().remove(cardNode);
    }

    private void handleRejectAction(String name, HBox cardNode) {
        System.out.println("Rejected application from: " + name);
        applicationsContainer.getChildren().remove(cardNode);
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
        // Run network request on a background thread to prevent UI freezing
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/listings/all"))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JsonMapper mapper = JsonMapper.builder().build();

                    // The backend returns a List of JSON Strings
                    List<String> listingsJsonList = mapper.readValue(response.body(), new TypeReference<List<String>>(){});

                    plannedMeetupsMap.clear();

                    for (String jsonString : listingsJsonList) {
                        // Parse each individual JSON string into a JsonNode
                        JsonNode node = mapper.readTree(jsonString);

                        if (node.has("listingname") && node.has("meetupDate") && !node.get("meetupDate").isNull()) {
                            String meetupName = node.get("listingname").asText();
                            JsonNode dateNode = node.get("meetupDate");

                            LocalDate meetupDate;

                            // Spring Boot sometimes serializes LocalDate as an array [YYYY, MM, DD]
                            // or as a standard ISO String "YYYY-MM-DD"
                            if (dateNode.isArray()) {
                                meetupDate = LocalDate.of(
                                        dateNode.get(0).asInt(),
                                        dateNode.get(1).asInt(),
                                        dateNode.get(2).asInt()
                                );
                            } else {
                                meetupDate = LocalDate.parse(dateNode.asText());
                            }

                            // Add the parsed meetup to our map
                            plannedMeetupsMap.computeIfAbsent(meetupDate, k -> new ArrayList<>()).add(meetupName);
                        }
                    }

                    // Update the UI on the JavaFX Application Thread
                    Platform.runLater(() -> populateCalendar(currentYearMonth));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void populateCalendar(YearMonth yearMonth) {
        calendarGrid.getChildren().clear();

        // Set Header Label
        calendarMonthYearLabel.setText(yearMonth.getMonth().toString().substring(0, 1) +
                yearMonth.getMonth().toString().substring(1).toLowerCase() +
                " " + yearMonth.getYear());

        // Add Day of Week Headers
        String[] daysOfWeek = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < daysOfWeek.length; i++) {
            Label dayLabel = new Label(daysOfWeek[i]);
            dayLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #757575;");
            calendarGrid.add(dayLabel, i, 0);
            GridPane.setHalignment(dayLabel, javafx.geometry.HPos.CENTER);
        }

        // Calculate days
        LocalDate firstOfMonth = yearMonth.atDay(1);
        int dayOfWeekOfFirst = firstOfMonth.getDayOfWeek().getValue();
        int daysInMonth = yearMonth.lengthOfMonth();

        int row = 1;
        int col = dayOfWeekOfFirst - 1;

        // Fill actual days
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate currentDate = yearMonth.atDay(day);

            // Use a VBox so we can stack the Date number and the Meetup Labels
            VBox dayPane = new VBox();
            dayPane.setAlignment(Pos.TOP_CENTER);
            dayPane.setPadding(new Insets(5));
            dayPane.setSpacing(4);

            Label dateLabel = new Label(String.valueOf(day));

            // Style current day vs regular days
            if (currentDate.equals(LocalDate.now())) {
                dayPane.setStyle("-fx-background-color: #e3f2fd; -fx-background-radius: 10; -fx-border-color: #0078fe; -fx-border-radius: 10; -fx-cursor: hand;");
                dateLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0078fe;");
            } else {
                dayPane.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10; -fx-cursor: hand;");
                dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #1a1a1a;");
            }

            dayPane.getChildren().add(dateLabel);

            // Inject the meetups into the calendar cells dynamically
            if (plannedMeetupsMap.containsKey(currentDate)) {
                for (String meetupName : plannedMeetupsMap.get(currentDate)) {
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