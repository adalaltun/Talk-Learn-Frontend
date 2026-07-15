package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiscoveryController {
    public VBox listingBox;

    @FXML private Label placeLabel;
    @FXML private Label titleLabel;
    @FXML private Label rankLabel;

    private final static String ADD_BACKEND_URL = "http://localhost:8080/api/listings/add";
    private final static String READ_ALL_BACKEND_URL = "http://localhost:8080/api/listings/all";

    public void handleMain(ActionEvent actionEvent) {
        Main.changeMenu(Main.homeMenu);
    }

    public void handleSignUp(ActionEvent actionEvent) {
    }

    public void handleDiscovery(ActionEvent actionEvent) {
        Main.changeMenu(Main.discoveryMenu);
    }

    public void handleChat(ActionEvent actionEvent) {
    }

    public void handleLeaderboard(ActionEvent actionEvent) {
    }

    public void handleSettings(ActionEvent actionEvent) {
    }

    public void handleListing(ActionEvent actionEvent) {
        System.out.println("-- handleListing");
        Main.changeMenu(Main.applicationScene);
    }

    @FXML
    public void handleDebugAddListing(ActionEvent e) {
        Parent listing = Main.loadFXML(new FXMLLoader(), "smallDiscoveryListing");
        listingBox.getChildren().add(listing);

        // jsonbody to be added
        String jsonbody = String.format("{\"listingname\":\"%s\", \"language\":\"%s\"}", "addListingDemo33", "German");

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ADD_BACKEND_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonbody))
                    .build();

            System.out.println(jsonbody);

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

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
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(READ_ALL_BACKEND_URL))
                    .GET()
                    .header("Accept","application/json")
                    .build();

            HttpResponse<String> response = client.send(request,  HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Main.displayAlert(Alert.AlertType.INFORMATION, "Success", "Listings read success!");
                System.out.println("Successfully sent to " + READ_ALL_BACKEND_URL + "\n");

                this.addReadListings(
                    Main.fromJSON( response.body(), new TypeReference< List<HashMap<String, String>> >() {} )
                );
                System.out.println("response.body() " + response.body());

            } else {
                Main.displayAlert(Alert.AlertType.ERROR, "Reading failed.", response.body());
            }

        } catch (Exception ex) {
            System.out.println("Could not connect to the database.");
        }
    }
    private void addReadListings(List<HashMap<String, String>> maplistings) {
        for ( HashMap<String, String> map : maplistings) {
            StackPane listingPane = (StackPane) Main.loadFXML(new FXMLLoader(), "smallDiscoveryListing");
            HBox listingLabels = (HBox) listingPane.getChildren().getFirst();

            Label placeLabel = (Label) listingLabels.getChildren().get(0);
            Label titleLabel = (Label) listingLabels.getChildren().get(1);

            placeLabel.setText(map.get("language"));
            titleLabel.setText(map.get("listingname"));

            listingBox.getChildren().add(listingPane);
        }
    }

    // TODO bunun yeirne Main.fromJSON  olunca çalışmıyo bakmak lazım
    private List<HashMap<String, String>> readListings(String responsebody) {
        JsonMapper mapper = JsonMapper.builder().build();

        // converting jsonlistings to hashmaps
        List<String> jsonlistings = mapper.readValue(responsebody, new TypeReference<List<String>>() {} );
        List<HashMap<String, String>> maplistings = new ArrayList<>();

        for (String jsonbody : jsonlistings) {
            HashMap<String, String> map = mapper.readValue(jsonbody, new TypeReference<HashMap<String, String>>() {} );
            maplistings.add(map);
        }
        System.out.println("maplistings: " + maplistings);
        return maplistings;
    }
}
