package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main extends Application {

    public static Scene scene;
    public static Parent homeMenu;
    public static Parent startingScene;
    public static Parent registerScreen;
    public static Parent loginScreen;
    public static Parent discoveryMenu;
    public static Parent applicationScene;
    public static Parent chatMenu;
    public static Parent leaderboardScreen;
    public static Parent accountInfoScreen;
    public static Parent customizationScreen;
    public static Parent settingsScreen;

    public static Parent myMeetupsMenu;
    public static Parent meetupCreationMenu;

    public static Parent leaderboardMenu;
    public static Parent settingsMenu;
    public static String loggedInUser = "NoAccount";
    public static String accountCreationDate = "Please Login";
    public static String selectedWallpaper = null;
    public static String selectedAvatar = null;

    public VBox MainBox;
    public VBox buttonVBox;

    private LoginController loginController;
    public static HomeController homeController;
    public static LeaderboardController leaderboardController;
    public static AccountInfoController accountInfoController;
    public static CustomizationController customizationController;
    public static SettingsController settingsController;
    public static ListingApplyController listingApplyController;

    public static Parent loadFXML(String fxmlname) {
        Parent root = null;
        try {
            java.net.URL resourceLocation = Main.class.getResource("/" + fxmlname + ".fxml");

            if (resourceLocation == null) {
                System.err.println("CRITICAL ERROR: Cannot find FXML file -> " + fxmlname + ".fxml");
                return null;
            }

            FXMLLoader loader = new FXMLLoader(resourceLocation);
            root = loader.load();

            if (fxmlname.equalsIgnoreCase("homeScreen")) {
                homeController = loader.getController();
            } else if (fxmlname.equalsIgnoreCase("leaderboardScreen")) {
                leaderboardController = loader.getController();
            } else if (fxmlname.equalsIgnoreCase("AccountInfoScreen")) {
                accountInfoController = loader.getController();
            } else if (fxmlname.equalsIgnoreCase("CustomizationScreen")) {
                customizationController = loader.getController();
            } else if (fxmlname.equalsIgnoreCase("settingsScreen")) {
                settingsController = loader.getController();
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("-- Couldn't load FXML. loadFXML will return null.");
        }
        return root;
    }

    //displayAlert method's first parameter is the error type the others are title and the message
    //it is used to prompt a read only warning screen!
    public static void displayAlert(Alert.AlertType type, String title, String message) {

        // "Platform.runLater()"'ı bunu DiscoveryController'ın initialize()'ında kullandığım için yapmam gerekti.
        Platform.runLater(() ->
        {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static <T> List<String> listToJSON(List<T> objs) {
        JsonMapper mapper = JsonMapper.builder().build();
        List<String> jsonObjs = new ArrayList<>();

        for (T o : objs) {
            String json = mapper.writeValueAsString(o);
            jsonObjs.add( json );
        }
        return jsonObjs;
    }

    public static List<HashMap<String, String>> listFromJSON(String body) {
        JsonMapper MAPPER = JsonMapper.builder().build();

        List<String> jsons = new ArrayList<>();
        jsons = MAPPER.readValue(body, new TypeReference<List<String>>() {
        });

        List<HashMap<String, String>> objs = new ArrayList<>();
        for (String j : jsons) {
            objs.add(MAPPER.readValue(j, new TypeReference<HashMap<String, String>>() {
            }));
        }
        return objs;
    }

    public static HttpResponse<String> sendHttpPOST(String jsonbody, String URL) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonbody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> sendHttpGET(String URL) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .GET()
                .header("Accept","application/json")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }


    @Override
    public void start(Stage stage) {

        System.out.println("RESOURCE URL: " + getClass().getResource("/test.fxml"));

        FXMLLoader loader = new FXMLLoader();

        //GUI'ye kod yazıp test edecekseniz startingScene'nin fxmlname'sini değiştirin
        startingScene = loadFXML("loginScreen");
        //fxmlname'nin sonunda .fxml olmasına gerek yok.
        homeMenu = loadFXML("homeScreen");
        //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        registerScreen = loadFXML("registerScreen");
        loginScreen = loadFXML("loginScreen");
        leaderboardScreen = loadFXML("leaderboardScreen");
        discoveryMenu = loadFXML("discoveryScreen");
        accountInfoScreen = loadFXML("AccountInfoScreen");
        customizationScreen = loadFXML("CustomizationScreen");
        applicationScene = loadFXML("listingApplicationScreen");
        chatMenu = loadFXML("chatScreen");
        settingsScreen = loadFXML("settingsScreen");

        //GUI'ye kod yazıp test edecekseniz startingScene'nin fxmlname'sini değiştirin
        startingScene = discoveryMenu;
        //fxmlname'nin sonunda .fxml olmasına gerek yok.

        scene = new Scene(startingScene, 800, 620);

        scene.getStylesheets().add(getClass().getResource("/test_style.css").toExternalForm());

        stage.setTitle("Talk & Learn");
        stage.setScene(scene);

        stage.show();


    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void changeMenu(Parent root) {
        scene.setRoot(root);

        if (selectedWallpaper != null && !selectedWallpaper.isEmpty()) {
            java.net.URL bgURL = Main.class.getResource(selectedWallpaper);
            if (bgURL != null) {
                root.setStyle("-fx-background-image: url('" + bgURL.toExternalForm() + "'); " +
                        "-fx-background-size: cover; " +
                        "-fx-background-position: center;");
            }
        }

        if (root == homeMenu && homeController != null) {
            homeController.refreshWallpaperAndProfile();
        } else if (root == settingsScreen && settingsController != null) {
            settingsController.refreshWallpaperAndProfile();
        } else if (root == accountInfoScreen && accountInfoController != null) {
            accountInfoController.refreshWallpaperAndProfile();
        } else if (root == customizationScreen && customizationController != null) {
            customizationController.refreshWallpaperAndProfile();
        }
    }
}
