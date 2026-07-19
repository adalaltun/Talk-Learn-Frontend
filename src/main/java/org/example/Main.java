package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
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
    public static Parent settingsScreen;
    public static String loggedInUser = "NoAccount";
    public static String accountCreationDate = "Please Login";
    public VBox MainBox;
    public VBox buttonVBox;

    private LoginController loginController;
    public static HomeController homeController;
    public static LeaderboardController leaderboardController;

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
            }else if (fxmlname.equalsIgnoreCase("leaderboardScreen")) {
                leaderboardController = loader.getController();
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
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static <T> List<String> listToJSON(List<T> objs) {
        JsonMapper mapper = JsonMapper.builder().build();
        List<String> jsonObjs = new ArrayList<>();

        for (T o : objs) {
            String json = mapper.writeValueAsString(o);

            jsonObjs.add(json);
        }
        return jsonObjs;
    }

    /**
     * JSON'u bir şeyin List'ine çevirir.
     * <p>
     * Frontend'de backend'deki classlar burda yok diye otomatik {@code HashMap<String, String>} yapıyo.
     */
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

    @Override
    public void start(Stage stage) {

        System.out.println("RESOURCE URL: " + getClass().getResource("/test.fxml"));

        FXMLLoader loader = new FXMLLoader();

        //GUI'ye kod yazıp test edecekseniz startingScene'nin fxmlname'sini değiştirin
        startingScene = loadFXML("homeScreen");
        //fxmlname'nin sonunda .fxml olmasına gerek yok.
        homeMenu = loadFXML("homeScreen");
        //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        registerScreen = loadFXML("registerScreen");
        loginScreen = loadFXML("loginScreen");
        leaderboardScreen = loadFXML("leaderboardScreen");
        discoveryMenu = loadFXML("discoveryScreen");
        applicationScene = loadFXML("listingApplicationScreen");
        chatMenu = loadFXML("chatScreen");
        settingsScreen = loadFXML("settingsScreen");

        scene = new Scene(startingScene, 800, 620);

        //scene.getStylesheets().add(getClass().getResource("/test_style.css").toExternalForm());

        stage.setTitle("Talk & Learn");
        stage.setScene(scene);

        stage.show();


    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void changeMenu(Parent root) {
        scene.setRoot(root);
    }
}
