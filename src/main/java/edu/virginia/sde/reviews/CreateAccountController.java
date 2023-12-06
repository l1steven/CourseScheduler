package edu.virginia.sde.reviews;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;


public class CreateAccountController {
    @FXML
    public TextField usernameField;
    @FXML
    public TextField passwordField;
    @FXML
    public Label title;

    public void attemptCreateAccount(ActionEvent actionEvent) throws SQLException {
        DatabaseDriver dbDriver = new DatabaseDriver();
        dbDriver.connect();
        String username = usernameField.getText();
        String password = passwordField.getText();
        if(validateCreateAccount(username, password)) {
            System.out.println("GOT HERE!");
            dbDriver.addUser(new User(username, password));
            dbDriver.commit();
            Alert a=new Alert(Alert.AlertType.INFORMATION);
            a.setHeaderText("Account successfully created!");
            a.show();
        }
        else {
            Alert a = new Alert(Alert.AlertType.ERROR);
            if (passwordField.getText().length() < 8) {
                a.setHeaderText("Your password was too short!");
            } else {
                a.setHeaderText("That username is already taken!");
            }
            a.show();
        }
        dbDriver.disconnect();
    }
    private boolean validateCreateAccount(String username, String password) throws SQLException {
        DatabaseDriver dbDriver = new DatabaseDriver();
        try {
            dbDriver.connect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        boolean isValid = !dbDriver.findUser(username) && password.length() >= 8;
        dbDriver.disconnect();
        return isValid;
    }

    public void takeToLogin(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Parent pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("log-in.fxml")));
        stage.getScene().setRoot(pane);
    }

    public void closeApp(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }
}
