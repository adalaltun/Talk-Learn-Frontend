package org.example;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiscoveryController {

    @FXML public VBox listingBox;
    @FXML private ScrollPane discoveryScrollPane;
    @FXML private GridPane filterBox;
    @FXML private GridPane tagBox;
    @FXML private Label placeLabel;
    @FXML private Label titleLabel;
    @FXML private Label rankLabel;
    @FXML private NavigationController navMenuController;
    @FXML private Button showListingsButton;

    @FXML private AnchorPane smallApplicationScreen;
    @FXML private ListingApplyController smallApplicationScreenController;

    private static final String READ_ALL_BACKEND_URL = "http://localhost:8080/api/listings/all";
    private static final String SEARCH_BACKEND_URL = "http://localhost:8080/api/listings/search";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private static final String ACTIVE_STYLE = "-fx-background-color: #0078fe; -fx-text-fill: white; -fx-background-radius: 20; -fx-border-color: #0078fe; -fx-border-radius: 20; -fx-cursor: hand; -fx-font-weight: bold;";
    private static final String INACTIVE_STYLE = "-fx-background-color: #f5f5f5; -fx-text-fill: #333333; -fx-background-radius: 20; -fx-border-color: #cccccc; -fx-border-radius: 20; -fx-cursor: hand; -fx-font-weight: bold;";

    @FXML
    public void initialize() {
        if (navMenuController != null) {
            navMenuController.setActive("discovery");
        }

        if (smallApplicationScreen != null) {
            smallApplicationScreen.setVisible(false);
            smallApplicationScreen.setManaged(false);
        }
        if (discoveryScrollPane != null) {
            discoveryScrollPane.setVisible(false);
            discoveryScrollPane.setManaged(false);
        }
        if (showListingsButton != null) {
            showListingsButton.setVisible(true);
            showListingsButton.setManaged(true);
        }

        EventHandler<ActionEvent> buttonColorHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleToggleButtonColor(event);
            }
        };

        for (int i = 0; i < filterBox.getChildren().size(); i++) {
            if (filterBox.getChildren().get(i) instanceof ToggleButton) {
                ToggleButton button = (ToggleButton) filterBox.getChildren().get(i);
                button.setOnAction(buttonColorHandler);
            }
        }
        for (int i = 0; i < tagBox.getChildren().size(); i++) {
            if (tagBox.getChildren().get(i) instanceof ToggleButton) {
                ToggleButton button = (ToggleButton) tagBox.getChildren().get(i);
                button.setOnAction(buttonColorHandler);
            }
        }

        updateAllButtonStyles();
    }

    @FXML
    public void handleDebugAddListing(ActionEvent event) {
    }

    @FXML
    public void handleReadAllListings(ActionEvent event) {
        readAllListings();
    }

    @FXML
    public void handleShowListingsClick(ActionEvent event) {
        if (showListingsButton != null) {
            showListingsButton.setVisible(false);
            showListingsButton.setManaged(false);
        }
        handleResetFilters(null);
    }

    @FXML
    public void handleToggleButtonColor(ActionEvent event) {
        updateAllButtonStyles();
    }

    private void updateAllButtonStyles() {
        for (int i = 0; i < filterBox.getChildren().size(); i++) {
            if (filterBox.getChildren().get(i) instanceof ToggleButton) {
                ToggleButton button = (ToggleButton) filterBox.getChildren().get(i);
                if (button.isSelected()) {
                    button.setStyle(ACTIVE_STYLE);
                } else {
                    button.setStyle(INACTIVE_STYLE);
                }
            }
        }
        for (int i = 0; i < tagBox.getChildren().size(); i++) {
            if (tagBox.getChildren().get(i) instanceof ToggleButton) {
                ToggleButton button = (ToggleButton) tagBox.getChildren().get(i);
                if (button.isSelected()) {
                    button.setStyle(ACTIVE_STYLE);
                } else {
                    button.setStyle(INACTIVE_STYLE);
                }
            }
        }
    }

    @FXML
    public void handleResetFilters(ActionEvent event) {
        for (int i = 0; i < filterBox.getChildren().size(); i++) {
            if (filterBox.getChildren().get(i) instanceof ToggleButton) {
                ToggleButton button = (ToggleButton) filterBox.getChildren().get(i);
                button.setSelected(false);
            }
        }
        for (int i = 0; i < tagBox.getChildren().size(); i++) {
            if (tagBox.getChildren().get(i) instanceof ToggleButton) {
                ToggleButton button = (ToggleButton) tagBox.getChildren().get(i);
                button.setSelected(false);
            }
        }
        updateAllButtonStyles();
        readAllListings();
    }

    public void handleMain(ActionEvent actionEvent) {
        Main.changeMenu(Main.homeMenu);
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
        Main.changeMenu(Main.leaderboardScreen);
    }

    public void handleSettings(ActionEvent actionEvent) {
        Main.changeMenu(Main.settingsScreen);
    }

    @FXML
    public void handleSearchListings(ActionEvent actionEvent) {
        if (showListingsButton != null && showListingsButton.isVisible()) {
            showListingsButton.setVisible(false);
            showListingsButton.setManaged(false);
        }

        String filter = "all";
        List<String> tags = new ArrayList<>();

        for (int i = 0; i < tagBox.getChildren().size(); i++) {
            if (tagBox.getChildren().get(i) instanceof ToggleButton) {
                ToggleButton button = (ToggleButton) tagBox.getChildren().get(i);
                if (button.isSelected()) {
                    tags.add(button.getText());
                }
            }
        }
        for (int i = 0; i < filterBox.getChildren().size(); i++) {
            if (filterBox.getChildren().get(i) instanceof ToggleButton) {
                ToggleButton button = (ToggleButton) filterBox.getChildren().get(i);
                if (button.isSelected()) {
                    filter = button.getText();
                }
            }
        }

        List<HashMap<String, String>> searchResult = searchListings(tags, filter);
        addReadListings(searchResult);
    }

    public List<HashMap<String, String>> searchListings(List<String> tags, String filter) {
        List<HashMap<String, String>> searchResult = new ArrayList<>();
        try {
            StringBuilder tagsURL = new StringBuilder("tags=");
            if (!tags.isEmpty()) {
                tagsURL.append(tags.get(0));
                for (int i = 1; i < tags.size(); i++) {
                    tagsURL.append(",").append(tags.get(i));
                }
            }

            String finalUrl = SEARCH_BACKEND_URL + "?" + tagsURL.toString() + "&filter=" + filter;
            String cleanUrl = finalUrl.replace(" ", "%20");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(cleanUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                searchResult = parseJsonArray(response.body());
            } else {
                Main.displayAlert(Alert.AlertType.ERROR, "Search Failed", response.body());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return searchResult;
    }

    private void readAllListings() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(READ_ALL_BACKEND_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                List<HashMap<String, String>> parsedList = parseJsonArray(response.body());
                addReadListings(parsedList);
            } else {
                Main.displayAlert(Alert.AlertType.ERROR, "Reading Failed", response.body());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addReadListings(List<HashMap<String, String>> maplistings) {
        if (listingBox == null) {
            return;
        }
        listingBox.getChildren().clear();

        if (smallApplicationScreen != null) {
            smallApplicationScreen.setVisible(false);
            smallApplicationScreen.setManaged(false);
        }
        if (discoveryScrollPane != null) {
            discoveryScrollPane.setVisible(true);
            discoveryScrollPane.setManaged(true);
        }

        for (int i = 0; i < maplistings.size(); i++) {
            HashMap<String, String> map = maplistings.get(i);
            StackPane listingPane = (StackPane) Main.loadFXML("smallListing");

            if (listingPane != null && !listingPane.getChildren().isEmpty()) {
                HBox listingLabels = (HBox) listingPane.getChildren().get(0);

                Label ratingLabel = (Label) listingLabels.getChildren().get(0);
                final Label userLabel = (Label) listingLabels.getChildren().get(1);
                Label langLabel = (Label) listingLabels.getChildren().get(2);
                final Label topicLabel = (Label) listingLabels.getChildren().get(3);
                Label tagsLabel = (Label) listingLabels.getChildren().get(4);
                Label locLabel = (Label) listingLabels.getChildren().get(5);
                Label limitLabel = (Label) listingLabels.getChildren().get(6);
                Label dateLabel = (Label) listingLabels.getChildren().get(7);

                final Label currentLangLabel = langLabel;

                String rating = map.get("fairRating");
                if (rating != null) {
                    ratingLabel.setText(rating);
                } else {
                    ratingLabel.setText("0.0");
                }

                String hostUser = map.get("user");
                if (hostUser != null && !hostUser.isEmpty()) {
                    userLabel.setText(hostUser);
                } else {
                    userLabel.setText("Unknown");
                }

                String lang = map.get("language");
                if (lang != null) {
                    langLabel.setText(lang);
                } else {
                    langLabel.setText("Unknown");
                }

                String topic = map.get("listingname");
                if (topic != null) {
                    topicLabel.setText(topic);
                } else {
                    topicLabel.setText("Untitled");
                }

                String rawTags = map.get("tags");
                if (rawTags != null && !rawTags.equals("[]")) {
                    String cleanTags = rawTags.replace("[", "").replace("]", "").replace("\"", "");
                    String[] tagArray = cleanTags.split(",");
                    List<String> formattedTags = new ArrayList<>();

                    int limitCount = 4;
                    if (tagArray.length < 4) {
                        limitCount = tagArray.length;
                    }

                    for (int j = 0; j < limitCount; j++) {
                        formattedTags.add(tagArray[j].trim());
                    }
                    tagsLabel.setText(String.join(", ", formattedTags));
                } else {
                    tagsLabel.setText("General");
                }

                String loc = map.get("location");
                if (loc != null) {
                    locLabel.setText(loc);
                } else {
                    locLabel.setText("Unspecified");
                }

                String limit = map.get("participantLimit");
                if (limit != null) {
                    limitLabel.setText("Max: " + limit);
                } else {
                    limitLabel.setText("Unlimited");
                }

                String date = map.get("date");
                if (date != null) {
                    dateLabel.setText(date);
                } else {
                    dateLabel.setText("No Date");
                }

                listingPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (smallApplicationScreen != null && smallApplicationScreenController != null) {
                            String hostName = (userLabel != null) ? userLabel.getText() : "";
                            smallApplicationScreenController.setLabels(topicLabel, currentLangLabel, hostName);

                            if (discoveryScrollPane != null) {
                                discoveryScrollPane.setVisible(false);
                                discoveryScrollPane.setManaged(false);
                            }
                            smallApplicationScreen.setVisible(true);
                            smallApplicationScreen.setManaged(true);
                        }
                    }
                });

                listingBox.getChildren().add(listingPane);
            }
        }
    }

    private List<HashMap<String, String>> parseJsonArray(String json) {
        List<HashMap<String, String>> list = new ArrayList<>();
        try {
            String clean = json.trim();
            if (clean.startsWith("[")) {
                clean = clean.substring(1);
            }
            if (clean.endsWith("]")) {
                clean = clean.substring(0, clean.length() - 1);
            }
            clean = clean.trim();
            if (clean.isEmpty()) {
                return list;
            }

            String[] objects = clean.split("\",\\s*\"");

            for (int i = 0; i < objects.length; i++) {
                String object = objects[i];
                String obj = object.trim();
                if (obj.startsWith("\"")) {
                    obj = obj.substring(1);
                }
                if (obj.endsWith("\"")) {
                    obj = obj.substring(0, obj.length() - 1);
                }

                obj = obj.replace("{", "").replace("}", "");
                obj = obj.replace("\\\"", "\"");

                HashMap<String, String> map = new HashMap<>();
                String[] pairs = obj.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                for (int j = 0; j < pairs.length; j++) {
                    String pair = pairs[j];
                    String[] kv = pair.split(":", 2);
                    if (kv.length == 2) {
                        String key = kv[0].replace("\"", "").trim();
                        String value = kv[1].replace("\"", "").trim();
                        map.put(key, value);
                    }
                }
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}