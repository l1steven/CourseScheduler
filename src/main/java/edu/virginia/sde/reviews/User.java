package edu.virginia.sde.reviews;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private ObservableList<Review> reviews;
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public ObservableList<Review> getReviews()
    {
        if(reviews==null)
        {
            retrieveReviews();
        }
        return reviews;
    }
    private void retrieveReviews()
    {
        /*
        retrieve all the reviewed courses from the database
         */
        List<Review> list=new LinkedList<>();
        reviews= FXCollections.observableList(list);
        reviews.add(new Review(username,"CS",3140,"Software Development",5,"12/2/2023","Great class!"));
        reviews.add(new Review(username,"CS",2100,"Discrete Mathematics",2,"12/2/2023","Sorry class!"));
        reviews.add(new Review(username,"APMA",3100,"Probability",4,"12/2/2023","OK class!"));
    }
    public void editReview()
    {

    }
    public void deleteReview()
    {

    }
}
