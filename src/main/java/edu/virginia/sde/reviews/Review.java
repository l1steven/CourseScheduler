package edu.virginia.sde.reviews;

public class Review {
    private String date;
    private int rating;
    private String comment;
    private String dept;
    private int number;
    private String title;
    private String username;

    public Review(String username, String dept, int number, String title, int rating,String date, String comment) {
        this.date = date;
        setRating(rating);
        this.comment = comment;
        this.dept = dept;
        this.number = number;
        this.title = title;
        this.username = username;
    }
    public Review(User user, Course course, int rating, String date, String comment)
    {
        username=user.getUsername();
        dept=course.getDept();
        number=course.getNumber();
        title=course.getTitle();
        setRating(rating);
        this.date=date;
        this.comment=comment;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if(rating<1||rating>5) throw new IllegalArgumentException();
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public Course getCourse()
    {
        Course course=new Course(dept,number,title,"N/A");
        return course;
    }
}
