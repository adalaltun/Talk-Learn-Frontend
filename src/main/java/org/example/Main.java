package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application{

    @Override
    public void start(Stage stage) {

        try {

            System.out.println("RESOURCE URL: " + getClass().getResource("/test.fxml"));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginScreen.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 800, 620);

            //scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            stage.setTitle("Talk & Learn");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error: Could not load test.fxml. ");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
