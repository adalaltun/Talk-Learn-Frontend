package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;

public class HomeController {
    static int i = 0;
    public void handleMain(ActionEvent actionEvent) {
        Main.changeMenu(Main.homeMenu);
    }

    public void handleSignUp(ActionEvent actionEvent) {
    }

    public void handleDiscovery(ActionEvent actionEvent) {
        Main.changeMenu(Main.discoveryMenu);
    }

    public void handleChat(ActionEvent actionEvent) {
        Parent root =Main.loadFXML(new FXMLLoader(),"chatScreen");

        if(root != null){
            Main.changeMenu(root);
        }else{
            Main.displayAlert(Alert.AlertType.ERROR, "Error", "chatScreen not found!");
        }
    }

    public void handleLeaderboard(ActionEvent actionEvent) {
    }

    public void handleSettings(ActionEvent actionEvent) {
    }

    public void handleDrag(MouseEvent e) {
    }
}

