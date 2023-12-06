package edu.virginia.sde.reviews;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class CourseCatalogController {
    @FXML
    private TableView<Course> courseCatalogTable;
    @FXML private TableColumn<Course, String> deptColumn;
    @FXML private TableColumn<Course, Integer> numberColumn;
    @FXML private TableColumn<Course, String> titleColumn;
    @FXML private TableColumn<Course, String> ratingColumn;
    private User user;
    @FXML
    private TextField addDeptField;
    @FXML
    private TextField addNumberField;
    @FXML
    private TextField addTitleField;

    @FXML
    private TextField searchDeptField;
    @FXML
    private TextField searchNumberField;
    @FXML
    private TextField searchTitleField;
    private ObservableList<Course> courseList;
    public CourseCatalogController(User user)
    {
        this.user=user;
    }
    @FXML
    private void initialize() throws SQLException {
        DatabaseDriver dbDriver = new DatabaseDriver();
        dbDriver.connect();
        List<Course> courses = dbDriver.searchCourse("", "", "");
        dbDriver.disconnect();
        courseList = FXCollections.observableArrayList();
        populateCatalog(courses);
        handleDoubleClick();
    }

    private void populateCatalog(List<Course> courses) {
        courseCatalogTable.getItems().clear();
        courseList.addAll(courses);
        //courseList=FXCollections.observableArrayList(new Course("CS",3140,"Software Development","5"));
        deptColumn.setCellValueFactory(new PropertyValueFactory<Course,String>("dept"));
        numberColumn.setCellValueFactory(new PropertyValueFactory<Course,Integer>("number"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<Course,String>("title"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<Course,String>("rating"));
        courseCatalogTable.setItems(courseList);
    }
    private void handleDoubleClick()
    {
            courseCatalogTable.setRowFactory(tv -> {
                TableRow<Course> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (!row.isEmpty())) {
                        Course course = row.getItem();
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
    @FXML
    protected void myReviewsButton(ActionEvent actionEvent) throws IOException
    {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        FXMLLoader loader=new FXMLLoader(getClass().getResource("my-reviews.fxml"));
        MyReviewsController controller=new MyReviewsController(user);
        loader.setController(controller);
        stage.getScene().setRoot(loader.load());
    }
    @FXML
    protected void logoutButton(ActionEvent actionEvent) throws IOException
    {
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Parent pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("log-in.fxml")));
        stage.getScene().setRoot(pane);
    }
    @FXML
    protected void searchButton() throws SQLException {
        String dept = searchDeptField.getText();
        String number = searchNumberField.getText();
        String title = searchTitleField.getText();
        DatabaseDriver dbDriver = new DatabaseDriver();
        dbDriver.connect();
        List<Course> courses = dbDriver.searchCourse(dept, number, title);
        dbDriver.disconnect();
        populateCatalog(courses);
    }

    @FXML
    protected void addCourseButton() throws SQLException {
        DatabaseDriver dbDriver = new DatabaseDriver();
        dbDriver.connect();
        String dept = addDeptField.getText();
        String number = addNumberField.getText();
        String title = addTitleField.getText();
        if(!isValidCourse(dept, number, title)) {
            Alert a=new Alert(Alert.AlertType.ERROR);
            a.setHeaderText("Invalid input format while adding course!");
            a.setContentText("Requirements:\nDept: 2-4 letters\nNumber: 4 digits\nTitle: 1-50 chars inclusive");
            a.show();
        }
        else if(dbDriver.courseExists(new Course(dept.toUpperCase(), Integer.parseInt(number), title, ""))) {
            Alert a=new Alert(Alert.AlertType.ERROR);
            a.setHeaderText("This course already exists!");
            a.show();
        }
        else {
            dbDriver.addCourse(new Course(dept.toUpperCase(), Integer.parseInt(number), title, ""));
            dbDriver.commit();
            addDeptField.clear();
            addNumberField.clear();
            addTitleField.clear();
            initialize();
        }
        dbDriver.disconnect();
    }

    protected boolean isValidCourse(String dept, String number, String title) {
        if (dept.length() < 2 || dept.length() > 4 || !dept.chars().allMatch(Character::isLetter))  {
            return false;
        }
        else if(number.length() != 4 || !number.chars().allMatch(Character::isDigit)) {
            return false;
        }
        else return !title.isEmpty() && title.length() <= 50;
    }
}
