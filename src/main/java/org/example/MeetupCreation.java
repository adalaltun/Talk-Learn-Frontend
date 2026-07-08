package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class MeetupCreation {

    @FXML
    private TextField titleField;

    @FXML
    private TextField languageField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    public void handleCreateMeetup(ActionEvent event){
        String title= titleField.getText();
        String language = languageField.getText();
        String description = descriptionArea.getText();

        try{
            Parent root = FXMLLoader.load(getClass().getResource("homeScreen.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();


        } catch (IOException e){
            e.printStackTrace();
            Main.displayAlert(Alert.AlertType.ERROR, "Page Load Error", "Failed to load the main menu screen!");
        }
    }
}
