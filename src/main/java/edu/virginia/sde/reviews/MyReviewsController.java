package edu.virginia.sde.reviews;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class MyReviewsController {
    @FXML private TableView<Review> myReviewsTable;
    @FXML private TableColumn<Review, String> deptColumn;
    @FXML private TableColumn<Review, Integer> numberColumn;
    @FXML private TableColumn<Review, String> titleColumn;
    @FXML private TableColumn<Review, String> ratingColumn;
    @FXML private Label myReviewsTitle;
    private ObservableList<Review> reviewList;
    private User user;

    public MyReviewsController(User user)
    {
        this.user=user;
    }
    @FXML
    private void initialize() throws SQLException {
        DatabaseDriver dbDriver = new DatabaseDriver();
        dbDriver.connect();
        List<Review> reviews = dbDriver.findUserReviews(user.getUsername());
        dbDriver.disconnect();
        reviewList = FXCollections.observableArrayList();
        populateMyReviews(reviews);
        handleDoubleClick();
        myReviewsTitle.setText(user.getUsername()+"'s Reviews");
        handleDoubleClick();
    }

    private void populateMyReviews(List<Review> reviews) {
        myReviewsTable.getItems().clear();
        reviewList.addAll(reviews);
        deptColumn.setCellValueFactory(new PropertyValueFactory<Review,String>("dept"));
        numberColumn.setCellValueFactory(new PropertyValueFactory<Review,Integer>("number"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<Review,String>("title"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<Review,String>("rating"));
        myReviewsTable.setItems(reviewList);
    }

    private void handleDoubleClick() throws SQLException {

        myReviewsTable.setRowFactory(tv -> {
            TableRow<Review> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Course course = null;
                    DatabaseDriver dbDriver = new DatabaseDriver();
                    try {
                        dbDriver.connect();
                        course = dbDriver.getCourseFromReview(row.getItem());
                        dbDriver.disconnect();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("course-review-view.fxml"));
                    CourseReviewsController controller = new CourseReviewsController(user, course);
                    loader.setController(controller);
                    try {
                        stage.getScene().setRoot(loader.load());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return row;
        });
    }
    public void myReviewToCatalogButton(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        FXMLLoader loader=new FXMLLoader(getClass().getResource("course_catalog_view.fxml"));
        CourseCatalogController controller=new CourseCatalogController(user);
        loader.setController(controller);
        stage.getScene().setRoot(loader.load());
    }

}
