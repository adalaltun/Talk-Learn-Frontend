package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AccountInfoController {
    @FXML private TextField newUsernameInput;
    @FXML private PasswordField currentPasswordInput;
    @FXML private PasswordField newPasswordInput;

    public void handleUpdateUsername(ActionEvent actionEvent) {}
    public void handleUpdatePassword(ActionEvent actionEvent) {}
}