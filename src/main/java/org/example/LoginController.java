package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;


public class LoginController {
    public Button loginButton;
    public Button signUpButton;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        boolean fieldEmpty = false; //false if the username and password datas are entered

        if(username.isEmpty()) {
            displayAlert(Alert.AlertType.ERROR, "Insufficient Information", "Please Enter Username");
            fieldEmpty = true;
        }
        if(password.isEmpty()) {
            displayAlert(Alert.AlertType.ERROR, "Insufficient Information", "Please Enter Password");
            fieldEmpty = true;
        }
        if(!fieldEmpty) {
            //this part is for verifying
            //System.out.println("USERNAME: " + username);
            //System.out.println("PASSWORD: " + password);
        }


    }

    public void switchScene(ActionEvent event) {
        Main.changeMenu(Main.registerScreen);
        clearFields();
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
    }
    //displayAlert method's first parameter is the error type the others are title and the message
    //it is used to prompt a read only warning screen!
    private void displayAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
