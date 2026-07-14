package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;


import java.io.IOException;

public class ChatController {
    @FXML
    public void handleGoHome(ActionEvent actionEvent){

        Parent root=Main.loadFXML(new FXMLLoader(),"homeScreen");
        if(root != null){
            Main.changeMenu(root);
        }else{
            Main.displayAlert(Alert.AlertType.ERROR, "Error", "home Scene not found!");
        }

    }

    @FXML
    public void handleGoDiscovery(ActionEvent actionEvent){
        Parent root=Main.loadFXML(new FXMLLoader(),"discoveryScene");
        if(root != null){
            Main.changeMenu(root);
        }else{
            Main.displayAlert(Alert.AlertType.ERROR, "Error", "Discovery Scene not found!");
        }
    }

    @FXML
    public void handleGoScoreBoard(ActionEvent actionEvent){
        Parent root=Main.loadFXML(new FXMLLoader(),"ScoreBoardController");
        if(root != null){
            Main.changeMenu(root);
        }else{
            Main.displayAlert(Alert.AlertType.ERROR, "Error", "ScoreBoard Scene not found!");
        }
    }


}
