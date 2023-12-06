package edu.virginia.sde.reviews;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class CourseReviewApplication extends Application {
    public static void main(String[] args) throws SQLException {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        DatabaseDriver dbDriver = new DatabaseDriver();
        dbDriver.connect();
        dbDriver.createTables();
        dbDriver.commit();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("log-in.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Course Review Application");
        stage.setScene(scene);
        stage.show();
        dbDriver.disconnect();
    }
}
