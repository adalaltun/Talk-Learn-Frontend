package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class Main extends Application{

    public static Scene scene;
    public static Parent homeMenu;
    public static Parent discoveryMenu;
    public static Parent chatMenu;
    public static Parent leaderboardMenu;
    public static Parent settingsMenu;

    public VBox MainBox;
    public VBox buttonVBox;

    private LoginController loginController;

    public static Parent loadFXML(FXMLLoader loader, String fxmlname) {
        Parent root = null;
        try {
            loader = new FXMLLoader(Main.class.getResource("/" + fxmlname + ".fxml"));
            root = loader.load();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("-- Couldn't load FXML. loadFXML will return null.");
        }
        return root;
    }

    @Override
    public void start(Stage stage) {

            System.out.println("RESOURCE URL: " + getClass().getResource("/test.fxml"));

            FXMLLoader loader = new FXMLLoader();

            homeMenu = loadFXML(loader, "test");
            discoveryMenu = loadFXML(loader, "discoveryScene");

            scene = new Scene(homeMenu, 800, 620);

            scene.getStylesheets().add(getClass().getResource("/test_style.css").toExternalForm());

            stage.setTitle("Talk & Learn");
            stage.setScene(scene);


            stage.show();
    }

    public static void main(String[] args) { launch(args); }

    public static void changeMenu(Parent root) {
        scene.setRoot(root);
    }
}
