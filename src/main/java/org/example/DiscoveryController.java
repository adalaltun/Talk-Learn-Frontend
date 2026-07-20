package org.example;

import com.sun.javafx.scene.control.behavior.ToggleButtonBehavior;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiscoveryController {
    public VBox listingBox;
    @FXML private ScrollPane discoveryScrollPane;

    @FXML private GridPane filterBox;
    @FXML private GridPane tagBox;

    @FXML private Label placeLabel;
    @FXML private Label titleLabel;
    @FXML private Label rankLabel;
    @FXML private NavigationController navMenuController;

    @FXML
    public void initialize() {
        if (navMenuController != null) {
            navMenuController.setActive("discovery");
        }
    }

    private final static String ADD_BACKEND_URL = "http://localhost:8080/api/listings/add";
    private final static String READ_ALL_BACKEND_URL = "http://localhost:8080/api/listings/all";
    private final static String SEARCH_BACKEND_URL = "http://localhost:8080/api/listings/search";

    public void handleMain(ActionEvent actionEvent) {
        Main.changeMenu(Main.homeMenu);
    }

    public void handleSignUp(ActionEvent actionEvent) {
    }

    public void handleDiscovery(ActionEvent actionEvent) {
        Main.changeMenu(Main.discoveryMenu);
    }

    public void handleChat(ActionEvent actionEvent) {Main.changeMenu(Main.chatMenu);}


    public void handleLeaderboard(ActionEvent actionEvent) {Main.changeMenu(Main.leaderboardScreen);}


    public void handleSettings(ActionEvent actionEvent) {Main.changeMenu(Main.settingsScreen);}


    @FXML
    public void handleSearchListings(ActionEvent actionEvent) {
        String filter = "all";
        List<String> tags = new ArrayList<>();
        List<String> comboFilters = new ArrayList<>();
        List<String> toggleFilters = new ArrayList<>();

        // collecting selected tags and filters
        for (int i = 0; i < tagBox.getChildren().size(); i++) {
            ToggleButton tagBtn = (ToggleButton) tagBox.getChildren().get(i);
            if (tagBtn.isSelected())
                tags.add(tagBtn.getText());
        }
        for (int i = 0; i < filterBox.getChildren().size(); i++) {
            Node filterNode = filterBox.getChildren().get(i);

            if (((ToggleButton) filterNode).isSelected())
                filter = ((ToggleButton) filterNode).getText();
        }

        addReadListings( searchListings(tags, filter) );
    }

    public static List<HashMap<String, String>> searchListings(List<String> tags, String filter) {
        List<HashMap<String, String>> searchResult = new ArrayList<>();

        try {
            if (!tags.isEmpty()) {

                String tagsURL = "tags=" + tags.getFirst() + ",";
                for (int i = 1; i < tags.size(); i++)
                    tagsURL += "," + tags.get(i);

                String FINAL_SEARCH_BACKEND_URL = SEARCH_BACKEND_URL + "?" + tagsURL + "&filter=" + filter;

                // filter can be "A-Z", "Z-A", "HIGHEST RANK", "LOWEST RANK"
                // last element in URL is "filter".
                HttpResponse<String> response = Main.sendHttpGET(FINAL_SEARCH_BACKEND_URL);

                if (response.statusCode() == 200) {
                    Main.displayAlert(Alert.AlertType.INFORMATION, "Success", "Listing add success!");
                    System.out.println("Successfully sent to " + FINAL_SEARCH_BACKEND_URL + "\n");

                    System.out.println("-- searchListings response.body():\n" + response.body());
                    System.out.println("-- Main.listFromJSON( response.body() ):\n" + Main.listFromJSON(response.body()));

                    searchResult = Main.listFromJSON(response.body());

                } else {
                    Main.displayAlert(Alert.AlertType.ERROR, "Search failed.", response.body());
                }
            }

        } catch (Exception ex) {
            System.out.println("Could not connect to the database.");
        }
        return searchResult;
    }

    @FXML
    public void handleDebugAddListing(ActionEvent e) {
        addListing();
    }
    private void addListing() {
        // jsonbody to be added
        String jsonbody = String.format("{\"listingname\":\"%s\", \"language\":\"%s\"}", "addListingDemo32", "German");

        try {
            HttpResponse<String> response = Main.sendHttpPOST(jsonbody, ADD_BACKEND_URL);

            if (response.statusCode() == 200) {
                Main.displayAlert(Alert.AlertType.INFORMATION, "Success", "Listing add success!");
                System.out.println("Successfully sent to " + ADD_BACKEND_URL + "\n");
            } else {
                Main.displayAlert(Alert.AlertType.ERROR, "Addition failed.", response.body());
            }

        } catch (Exception ex) {
            System.out.println("Could not connect to the database.");
        }
    }

    @FXML
    public void handleReadAllListings(ActionEvent e) {
        readAllListings();
    }
    private void readAllListings() {
        try {
            HttpResponse<String> response = Main.sendHttpGET(READ_ALL_BACKEND_URL);

            if (response.statusCode() == 200) {
                Main.displayAlert(Alert.AlertType.INFORMATION, "Success", "Listings read success!");
                System.out.println("Successfully sent to " + READ_ALL_BACKEND_URL + "\n");

                this.addReadListings( Main.listFromJSON(response.body()) );

            } else {
                Main.displayAlert(Alert.AlertType.ERROR, "Reading failed.", response.body());
            }

        } catch (Exception ex) {
            System.out.println("Could not connect to the database.");
        }
    }

    private void addReadListings(List<HashMap<String, String>> maplistings) {
        for ( HashMap<String, String> map : maplistings) {
            StackPane listingPane = (StackPane) Main.loadFXML("smallListing");
            HBox listingLabels = (HBox) listingPane.getChildren().getFirst();

            Label placeLabel = (Label) listingLabels.getChildren().get(0);
            Label titleLabel = (Label) listingLabels.getChildren().get(1);

            placeLabel.setText(map.get("language"));
            titleLabel.setText(map.get("listingname"));

            listingBox.getChildren().add(listingPane);
        }
    }

    @FXML
    private void handleToggleColor(ActionEvent e) {
        ToggleButton btn = (ToggleButton) e.getSource();
        System.out.println(btn.getText() + " IS SELECTED.");

//        if (btn.isSelected())
//            btn.getStyleClass().add("filter-button");
//        else
//            btn.getStyleClass().remove("filter-button");
    }
}
