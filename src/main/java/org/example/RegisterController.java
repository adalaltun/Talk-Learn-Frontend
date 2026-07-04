package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;



public class RegisterController {
    public Button signUpButton;
    public Button loginButton;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField, repeatedPasswordField;

    @FXML
    public void handleRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String repeatedPassword = repeatedPasswordField.getText().trim();
        boolean fieldEmpty = false; //false if the username, password, repeatedPassword datas are entered

        if(username.isEmpty()) {
            displayAlert(Alert.AlertType.ERROR, "Insufficient Information", "Please Enter Username");
            fieldEmpty = true;
        }
        if(password.isEmpty()) {
            displayAlert(Alert.AlertType.ERROR, "Insufficient Information", "Please Enter Password");
            fieldEmpty = true;
        }
        else if(repeatedPassword.isEmpty()) {
            displayAlert(Alert.AlertType.ERROR, "Insufficient Information", "Please Repeat Your Password");
            fieldEmpty = true;
        }
        if(!fieldEmpty) {
            if(password.equals(repeatedPassword)) {
//                this part is for verifying
//                System.out.println("USERNAME: " + username);
//                System.out.println("PASSWORD: " + password);
//                System.out.println("REPEATED PASSWORD: " + repeatedPassword);
            }
            else{
                displayAlert(Alert.AlertType.ERROR, "Information Mismatch", "Passwords do not match");
            }

        }

    }

    public void switchScene(ActionEvent event) {
        Main.changeMenu(Main.loginScreen);
        clearFields();
    }

    private void clearFields()
    {
        usernameField.clear();
        passwordField.clear();
        repeatedPasswordField.clear();
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
