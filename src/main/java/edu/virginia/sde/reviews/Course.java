package edu.virginia.sde.reviews;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Course {
    private int ID;
    private String dept;
    private int number;
    private String title;
    private String rating;
    public Course(String dept, int number, String title, String rating) {
        setDept(dept);
        setNumber(number);
        setTitle(title);
        this.rating = rating;
    }

    public int getID() { return ID; }

    public void setID(int id) { this.ID = id; }
    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        if(dept.length()<2 || dept.length()>4) throw new IllegalArgumentException();
        if(!dept.chars().allMatch(Character::isLetter)) throw new IllegalArgumentException();
        this.dept = dept;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        if(number<=999||number>=10000) throw new IllegalArgumentException();
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if(title.length()<1||title.length()>50) throw new IllegalArgumentException();
        this.title = title;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
    public ObservableList<Review> getReviews()
    {
        List<Review> list=new LinkedList<>();
        ObservableList<Review> reviews= FXCollections.observableList(list);
        reviews.add(new Review("Steven",dept,number,title,5,"12/2/2023","Great class!"));
        reviews.add(new Review("Steven2",dept,number,title,2,"12/2/2023","Sorry class!"));
        reviews.add(new Review("Steven3",dept,number,title,4,"12/2/2023","OK class!"));
        return reviews;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return number == course.number && Objects.equals(dept, course.dept) && Objects.equals(title, course.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dept, number, title, rating);
    }
}
