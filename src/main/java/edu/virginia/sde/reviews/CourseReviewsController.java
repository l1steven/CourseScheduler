package edu.virginia.sde.reviews;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CourseReviewsController {
    @FXML
    private TableView<Review> reviewsTable;
    @FXML private TableColumn<Review, String> dateColumn;
    @FXML private TableColumn<Review, Integer> ratingColumn;
    @FXML private TableColumn<Review, String> commentColumn;
    @FXML private TextField ratingField;
    @FXML private TextArea commentField;
    @FXML private Label courseTitleLabel;
    @FXML private Label courseRatingLabel;
    private User user;
    private Course course;
    private ObservableList<Review> reviewList;
    private boolean editMode=false;

    public CourseReviewsController(User user,Course course)
    {
        this.user=user;
        this.course=course;
    }
    @FXML
    private void initialize() throws SQLException {
        DatabaseDriver dbDriver = new DatabaseDriver();
        dbDriver.connect();
        List<Review> reviews = dbDriver.findCourseReviews(course.getID());
        reviewList = FXCollections.observableArrayList();
        reviewList.addAll(reviews);
        dateColumn.setCellValueFactory(new PropertyValueFactory<Review,String>("date"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<Review,Integer>("rating"));
        commentColumn.setCellValueFactory(new PropertyValueFactory<Review,String>("comment"));
        prioritizeMyReview();
        reviewsTable.setItems(reviewList);
        String s=course.getDept()+" "+course.getNumber()+": "+course.getTitle();
        courseTitleLabel.setText(s);
        courseRatingLabel.setText("Rating: "+dbDriver.getRating(course.getID()));
        dbDriver.disconnect();
    }

    private void prioritizeMyReview(){
        if(editMode) return;
        Review review=null;
        int index=0;
        for(Review r:reviewList)
        {
            if(r.getUsername().equals(user.getUsername()))
            {
                review=r;
                break;
            }
            index++;
        }
        if(review==null) return;
        reviewList.remove(index);
        reviewList.add(0,review);
    }
    @FXML
    protected void courseToCatalogButton(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        FXMLLoader loader=new FXMLLoader(getClass().getResource("course_catalog_view.fxml"));
        CourseCatalogController controller=new CourseCatalogController(user);
        loader.setController(controller);
        stage.getScene().setRoot(loader.load());
    }
    @FXML
    protected void addReviewButton() throws SQLException {
        DatabaseDriver dbDriver = new DatabaseDriver();
        dbDriver.connect();
        if(dbDriver.verifyUser(course.getID(), user.getUsername())) {
            Alert a=new Alert(Alert.AlertType.ERROR);
            a.setHeaderText("Your review already exists!");
            a.show();
        }
        else {
            try {
                editMode=false;
                Review review = new Review(user, course, Integer.parseInt(ratingField.getText()), "", commentField.getText());
                dbDriver.addReview(review);
                dbDriver.commit();
                ratingField.clear();
                commentField.clear();
                initialize();
            }
            catch(Exception e)
            {
                Alert a=new Alert(Alert.AlertType.ERROR);
                a.setHeaderText("Invalid arguments for new review!");
                a.show();
            }
        }
        dbDriver.disconnect();
    }
    @FXML
    protected void editReviewButton() throws SQLException {
        DatabaseDriver dbDriver = new DatabaseDriver();
        dbDriver.connect();
        if(!dbDriver.verifyUser(course.getID(), user.getUsername())) {
            Alert a=new Alert(Alert.AlertType.ERROR);
            a.setHeaderText("No existing review to edit!");
            a.show();
        }
        else
        {
            try {
                editMode=true;
                Review userReview = dbDriver.getCourseUserReview(course.getID(), user.getUsername());
                commentField.setText(userReview.getComment());
                ratingField.setText(userReview.getRating() + "");
                dbDriver.deleteReview(course.getID(), user.getUsername());
                dbDriver.commit();
                initialize();
            }
            catch(Exception e)
            {
                Alert a=new Alert(Alert.AlertType.ERROR);
                a.setHeaderText("Invalid arguments for new review!");
                a.show();
            }
        }
        dbDriver.disconnect();
    }
    @FXML
    protected void deleteReviewButton() throws SQLException {
        DatabaseDriver dbDriver = new DatabaseDriver();
        dbDriver.connect();
        if(!dbDriver.verifyUser(course.getID(), user.getUsername())) {
            Alert a=new Alert(Alert.AlertType.ERROR);
            a.setHeaderText("No existing review to delete!");
            a.show();
        }
        else {
            dbDriver.deleteReview(course.getID(), user.getUsername());
            dbDriver.commit();
            initialize();
        }
        dbDriver.disconnect();
    }
}
