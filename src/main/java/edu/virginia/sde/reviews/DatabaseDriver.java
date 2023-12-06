package edu.virginia.sde.reviews;

import javafx.scene.Parent;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class DatabaseDriver {
    private final String sqliteFilename;
    private Connection connection;

    // creates the Users database
    final String createUsers = """
            create table if not exists Users
            (
                Username TEXT primary key not null,
                Password TEXT not null
            );
            """;

    // creates the courses database
    final String createCourses = """
			create table if not exists Courses
			(
				ID INTEGER primary key not null,
				Department TEXT not null,
				Num TEXT not null,
				Title TEXT not null
			);
			""";

    // creates the reviews database
    final String createReviews = """
			create table if not exists Reviews
			(
				ID INTEGER primary key AUTOINCREMENT not null,
				Username TEXT not null,
				CourseID INTEGER not null,
				Rating INTEGER not null,
				Time_Stamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
				Comment TEXT not null
			);
			""";

    public DatabaseDriver() { this.sqliteFilename = "ApplicationDatabase.sqlite"; }

    public void connect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            throw new IllegalStateException("The connection is already opened");
        }
        connection = DriverManager.getConnection("jdbc:sqlite:" + sqliteFilename);
        connection.createStatement().execute("PRAGMA foreign_keys = ON");
        connection.setAutoCommit(false);
    }

	public void commit() throws SQLException {
		connection.commit();
	}

	public void rollback() throws SQLException {
		connection.rollback();
	}

	public void disconnect() throws SQLException {
		connection.close();
	}

	public void createTables() throws SQLException {
		if (connection.isClosed()) {
			throw new IllegalStateException("Connection must be open to create database tables.");
		}

		PreparedStatement userTable = connection.prepareStatement(createUsers);
		userTable.executeUpdate();
		userTable.close();

		PreparedStatement courseTable = connection.prepareStatement(createCourses);
		courseTable.executeUpdate();
		courseTable.close();

		PreparedStatement reviewTable = connection.prepareStatement(createReviews);
		reviewTable.executeUpdate();
		reviewTable.close();
	}

	public void addUser(User user) throws SQLException {
		try {
			if (connection.isClosed()) {
				throw new IllegalStateException("Connection must be open to create database tables.");
			}

			PreparedStatement sql = connection.prepareStatement("""
					INSERT INTO Users(Username, Password) values (?, ?)
					""");
			sql.setString(1, user.getUsername());
			sql.setString(2, user.getPassword());
			sql.executeUpdate();
			sql.close();

		} catch(SQLException e) {
			rollback();
			throw e;
		}
	}

	public boolean findUser(String username) throws SQLException {
		try {
			if (connection.isClosed()) {
				throw new IllegalStateException("Connection must be open to create database tables.");
			}

			PreparedStatement sql = connection.prepareStatement("""
					SELECT * FROM Users WHERE Username = ?
					""");
			sql.setString(1, username);
			ResultSet setUsers = sql.executeQuery();
			if(setUsers.isBeforeFirst()) {
				sql.close();
				return true;
			}
			else {
				sql.close();
				return false;
			}

		} catch(SQLException e) {
			rollback();
			throw e;
		}
	}

	public boolean verifyLogin(String username, String password) throws SQLException {
		try {
			if (connection.isClosed()) {
				throw new IllegalStateException("Connection must be open to create database tables.");
			}

			PreparedStatement sql = connection.prepareStatement("""
					SELECT * FROM Users WHERE Username = ? AND Password = ?
					""");
			sql.setString(1, username);
			sql.setString(2, password);
			ResultSet setUsers = sql.executeQuery();
			if(setUsers.isBeforeFirst()) {
				sql.close();
				return true;
			}
			else {
				sql.close();
				return false;
			}

		} catch(SQLException e) {
			rollback();
			throw e;
		}
	}

	public void addCourse(Course course) throws SQLException {
		try {
			if (connection.isClosed()) {
				throw new IllegalStateException("Connection must be open to create database tables.");
			}

			PreparedStatement sql1 = connection.prepareStatement("""
					INSERT INTO Courses(Department, Num, Title) values (?, ?, ?)
					""");
			sql1.setString(1, course.getDept());
			sql1.setInt(2, course.getNumber());
			sql1.setString(3, course.getTitle());
			sql1.executeUpdate();
			sql1.close();

			PreparedStatement sql2 = connection.prepareStatement("""
					SELECT * FROM Courses WHERE Department = ? AND Num = ? AND Title = ?
					""");
			sql2.setString(1, course.getDept());
			sql2.setInt(2, course.getNumber());
			sql2.setString(3, course.getTitle());
			ResultSet courseSet = sql2.executeQuery();
			course.setID(courseSet.getInt("ID"));
			sql2.close();

		} catch(SQLException e) {
			rollback();
			throw e;
		}
	}

	public List<Course> searchCourse(String dept, String num, String title) throws SQLException {
		try {
			if (connection.isClosed()) {
				throw new IllegalStateException("Connection must be open to create database tables.");
			}

			String statement = "SELECT * FROM Courses";
			String[] args = new String[3]; // stores non-empty args
			int counter = 0; // counts how many args are non-empty

			if(!dept.equals("")) {
				statement += " WHERE Department = ?";
				args[counter] = dept.toUpperCase();
				counter++;
			}
			if(!num.equals("")) {
				if(counter == 0) { statement += " WHERE"; }
				else { statement += " AND"; }
				statement += " Num  = ?";
				args[counter] = num;
				counter++;
			}
			if(!title.equals("")) {
				if(counter == 0) { statement += " WHERE"; }
				else { statement += " AND"; }
				statement += " upper(Title) LIKE ?";
				args[counter] = "%" + title.toUpperCase() + "%";
				counter++;
			}

			PreparedStatement sql = connection.prepareStatement(statement);
			for(int i = 0; i < counter; i++) {
				sql.setString(i+1, args[i]);
			}
			ResultSet courseSet = sql.executeQuery();
			List<Course> courseList = new ArrayList<>();
			while(courseSet.next()) {
				Course course = new Course(
						courseSet.getString("Department"),
						Integer.parseInt(courseSet.getString("Num")),
						courseSet.getString("Title"),
						getRating(courseSet.getInt("ID"))
				);
				course.setID(courseSet.getInt("ID"));
				courseList.add(course);
			}
			sql.close();
			return courseList;

		} catch(SQLException e) {
			rollback();
			throw e;
		}
	}

	public boolean courseExists(Course course) throws SQLException{
		try {
			if (connection.isClosed()) {
				throw new IllegalStateException("Connection must be open to create database tables.");
			}

			PreparedStatement sql = connection.prepareStatement("""
					SELECT * FROM Courses WHERE Department = ? AND Num = ? AND upper(Title) = ?
					""");
			sql.setString(1, course.getDept().toUpperCase());
			sql.setInt(2, course.getNumber());
			sql.setString(3, course.getTitle().toUpperCase());
			ResultSet courseSet = sql.executeQuery();
			if(courseSet.isBeforeFirst()) {
				sql.close();
				return true;
			}
			else {
				sql.close();
				return false;
			}

		} catch(SQLException e) {
			rollback();
			throw e;
		}
	}

	public Course getCourse(int courseID) throws SQLException {
		try {
			if (connection.isClosed()) {
				throw new IllegalStateException("Connection must be open to create database tables.");
			}

			PreparedStatement sql = connection.prepareStatement("""
					SELECT * FROM Courses WHERE ID = ?
					""");
			sql.setInt(1, courseID);
			ResultSet courseSet = sql.executeQuery();
			courseSet.next();
			return new Course(
					courseSet.getString("Department"),
					courseSet.getInt("Num"),
					courseSet.getString("Title"),
					getRating(courseID)
			);

		} catch(SQLException e) {
			rollback();
			throw e;
		}
	}

	public void addReview(Review review) throws SQLException {
		try {
			if (connection.isClosed()) {
				throw new IllegalStateException("Connection must be open to create database tables.");
			}

			PreparedStatement sql1 = connection.prepareStatement("""
					INSERT INTO Reviews(Username, CourseID, Rating, Comment) values (?, ?, ?, ?)
					""");
			sql1.setString(1, review.getUsername());

			// retrieve courseID from course table using department, number, and title
			PreparedStatement sql2 = connection.prepareStatement("""
					SELECT * FROM Courses WHERE Department = ? AND Num = ? AND Title = ?
					""");
			sql2.setString(1, review.getDept());
			sql2.setInt(2, review.getNumber());
			sql2.setString(3, review.getTitle());
			ResultSet courseSet = sql2.executeQuery();
			// end of retrieving courseID

			sql1.setInt(2, courseSet.getInt("ID"));
			sql1.setInt(3, review.getRating());
			sql1.setString(4, review.getComment());

			sql1.executeUpdate();
			sql1.close();
			sql2.close();

		} catch(SQLException e) {
			rollback();
			throw e;
		}
	}

	public List<Review> findUserReviews(String username) throws SQLException {
		try {
			if (connection.isClosed()) {
				throw new IllegalStateException("Connection must be open to create database tables.");
			}

			PreparedStatement sql = connection.prepareStatement("""
					SELECT * FROM Reviews WHERE Username = ?
					""");
			sql.setString(1, username);
			ResultSet reviewSet = sql.executeQuery();
			List<Review> reviewList = new ArrayList<>();

			while(reviewSet.next()){
				Course course = getCourse(reviewSet.getInt("CourseID"));
				Review review = new Review(
						username,
						course.getDept(),
						course.getNumber(),
						course.getTitle(),
						reviewSet.getInt("Rating"),
						reviewSet.getTimestamp("Time_Stamp").toString(),
						reviewSet.getString("Comment")
				);
				reviewList.add(review);
			}
			sql.close();
			return reviewList;

		} catch(SQLException e) {
			rollback();
			throw e;
		}
	}

	public String getRating(int id) throws SQLException {
		try {
			if (connection.isClosed()) {
				throw new IllegalStateException("Connection must be open to create database tables.");
			}

			PreparedStatement sql = connection.prepareStatement("""
					SELECT * FROM Reviews WHERE CourseID = ?
					""");
			sql.setInt(1, id);
			ResultSet reviewSet = sql.executeQuery();

			float sumRatings = 0;
			int count = 0;
			while(reviewSet.next()){
				sumRatings += reviewSet.getInt("Rating");
				count ++;
			}
			sql.close();
			if(count == 0) { return ""; }
			return String.format("%.2f", sumRatings / count);

		} catch(SQLException e) {
			rollback();
			throw e;
		}
	}

	public boolean verifyUser(int courseID, String username) throws SQLException {
		try {
			if (connection.isClosed()) {
				throw new IllegalStateException("Connection must be open to create database tables.");
			}

			PreparedStatement sql = connection.prepareStatement("""
					SELECT * FROM Reviews WHERE CourseID = ? AND Username = ?
					""");
			sql.setInt(1, courseID);
			sql.setString(2, username);
			ResultSet reviewSet = sql.executeQuery();
			if(reviewSet.isBeforeFirst()) {
				sql.close();
				return true;
			}
			else {
				sql.close();
				return false;
			}

		} catch(SQLException e) {
			rollback();
			throw e;
		}
	}

	public List<Review> findCourseReviews(int courseID) throws SQLException {
		try {
			if (connection.isClosed()) {
				throw new IllegalStateException("Connection must be open to create database tables.");
			}

			PreparedStatement sql = connection.prepareStatement("""
					SELECT * FROM Reviews WHERE CourseID = ?
					""");
			sql.setInt(1, courseID);
			ResultSet reviewSet = sql.executeQuery();
			List<Review> reviewList = new ArrayList<>();
			while(reviewSet.next()){
				Course course = getCourse(courseID);
				Review review = new Review(
						reviewSet.getString("Username"),
						course.getDept(),
						course.getNumber(),
						course.getTitle(),
						reviewSet.getInt("Rating"),
						reviewSet.getTimestamp("Time_Stamp").toString(),
						reviewSet.getString("Comment")
				);
				reviewList.add(review);
			}
			sql.close();
			return reviewList;

		} catch(SQLException e) {
			rollback();
			throw e;
		}
	}

	public void deleteReview(int courseID, String username) throws SQLException {
		try {
			if (connection.isClosed()) {
				throw new IllegalStateException("Connection must be open to create database tables.");
			}

			PreparedStatement sql = connection.prepareStatement("""
					DELETE FROM Reviews WHERE CourseID = ? AND Username = ?
					""");
			sql.setInt(1, courseID);
			sql.setString(2, username);
			sql.executeUpdate();
			sql.close();

		} catch(SQLException e) {
			rollback();
			throw e;
		}
	}

	public Review getCourseUserReview(int courseID, String username) throws SQLException{
		try {
			if (connection.isClosed()) {
				throw new IllegalStateException("Connection must be open to create database tables.");
			}

			PreparedStatement sql = connection.prepareStatement("""
					SELECT * FROM Reviews WHERE CourseID = ? AND Username = ?
					""");
			sql.setInt(1, courseID);
			sql.setString(2, username);
			ResultSet reviewSet = sql.executeQuery();
			reviewSet.next();
			Course course = getCourse(courseID);
			Review review = new Review(
					username,
					course.getDept(),
					course.getNumber(),
					course.getTitle(),
					reviewSet.getInt("Rating"),
					reviewSet.getTimestamp("Time_Stamp").toString(),
					reviewSet.getString("Comment")
			);
			sql.close();
			return review;

		} catch(SQLException e) {
			rollback();
			throw e;
		}
	}

	public Course getCourseFromReview(Review review) throws SQLException {
		try {
			if (connection.isClosed()) {
				throw new IllegalStateException("Connection must be open to create database tables.");
			}
			PreparedStatement sql = connection.prepareStatement("""
						SELECT * FROM Courses WHERE Department = ? AND Num = ? AND Title = ?
						""");
			sql.setString(1, review.getDept());
			sql.setInt(2, review.getNumber());
			sql.setString(3, review.getTitle());
			ResultSet courseSet = sql.executeQuery();
			courseSet.next();
			Course newCourse = new Course(
					courseSet.getString("Department"),
					Integer.parseInt(courseSet.getString("Num")),
					courseSet.getString("Title"),
					getRating(courseSet.getInt("ID"))
			);
			newCourse.setID(courseSet.getInt("ID"));
			return newCourse;
		} catch(SQLException e) {
			rollback();
			throw e;
		}
	}
}
