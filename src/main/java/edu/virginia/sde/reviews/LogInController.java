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


public class LogInController {
    @FXML
    public TextField usernameField;
    @FXML
    public TextField passwordField;

    @FXML
    public Label title;

    public void attemptLogin(ActionEvent actionEvent) throws IOException, SQLException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if(validateLogin(username, password)) {
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            FXMLLoader loader=new FXMLLoader(getClass().getResource("course_catalog_view.fxml"));
            CourseCatalogController controller=new CourseCatalogController(new User(usernameField.getText(), passwordField.getText()));
            loader.setController(controller);
            stage.getScene().setRoot(loader.load());
        }
        else
        {
            //SQL FUNCTIONALITY: Error message should display appropriate text
            Alert a=new Alert(Alert.AlertType.ERROR);
            a.setHeaderText("Invalid login!");
            a.show();
        }
    }
    private boolean validateLogin(String username, String password) throws SQLException {
        DatabaseDriver dbDriver = new DatabaseDriver();
        dbDriver.connect();
        boolean isValid = dbDriver.verifyLogin(username, password);
        dbDriver.disconnect();
        return isValid;
    }
    public void takeToAccountCreationPage(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Parent pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("create-account.fxml")));
        stage.getScene().setRoot(pane);
    }

    public void closeApp(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }
}
