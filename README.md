[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-24ddc0f5d75046c5622901739e7c5dd533143b0c8e959d652212380cedb1ea36.svg)](https://classroom.github.com/a/DC1SF4uZ)
# Homework 6 - Responding to Change

## Authors
1) Trishal Muthan, mdc9bv, [trishalmuthan]
2) Steven Li, ddm4wx, [l1steven]
3) Andy Cao, kcz3my, [andycao121]
4) Shritika Rao, auj9rz, [shritikarao]

## To Run

Main application file is called CourseReviewApplication.java. Run this file to start application. 

VM arguments: --module-path [PATH_TO_JAVAFX_LIB_FOLDER] --add-modules javafx.controls,javafx.fxml

Usage notes: 
- To edit a review, press the "edit" button. If the current user has a review for the course they are reviewing, then their review will be moved to the comment and rating display. The user can then edit their review, and when finished editing, they should press "add" to add their review back to the course reviews.
- When editing a review, leaving the page without re-adding will remove the review from the database as a precaution.
- Current user's review will be found at top of list of reviews for a particular course.

## Contributions

List the primary contributions of each author. It is recommended to update this with your contributions after each coding session.:

### [Author 1 - Trishal Muthan]

* Created login/account creation FXML files and UI
* Implemented controller-database functionality for all pages
* Helped design application and database structure

### [Author 2 - Steven Li]

* Built FXML files for Course Review and My Review screen
* Created Controller classes and handled transitions
* Handled different user errors by creating alerts

### [Author 3 - Andy Cao]

* Created database driver methods used to access and store user, course, and review values
* Helped with implementation of controller-database functionality
* General testing of application

### [Author 4 - Shritika Rao]

* Handled ordering and prioritizing reviews on the course review page
* Testing of application

## Issues

List any known issues (bugs, incorrect behavior, etc.) at the time of submission.
