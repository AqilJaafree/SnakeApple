package db;

import constants.CommonConstants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MyJDBC {

    // Register new player in the database
    // true - registration success
    // false - registration failure
    public static boolean register(String username, double score) {
        try {
            // First time check if the username already exists in the database
            if (!checkUser(username)) {
                // Connect to the database
                try (Connection connection = DriverManager.getConnection(CommonConstants.DB_URL, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD)) {
                    // Create insert query
                    try (PreparedStatement insertUser = connection.prepareStatement(
                            "INSERT INTO " + CommonConstants.DB_PLAYERS_TABLE_NAME + "(username, score) VALUES (?, ?)")) {

                        // Insert parameters in the insert query
                        insertUser.setString(1, username.trim()); // Trim to remove leading/trailing whitespaces
                        insertUser.setDouble(2, score);

                        // Update the database with the new user
                        insertUser.executeUpdate();
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Check if user exists in the database
    public static boolean checkUser(String username) {
        try (Connection connection = DriverManager.getConnection(
                CommonConstants.DB_URL,
                CommonConstants.DB_USERNAME,
                CommonConstants.DB_PASSWORD
        )) {
            // Prepare the SQL statement for user existence check
            try (PreparedStatement checkUserExist = connection.prepareStatement(
                    "SELECT * FROM " + CommonConstants.DB_PLAYERS_TABLE_NAME +
                            " WHERE LOWER(USERNAME) = LOWER(?)")) {

                // Set parameter for the statement
                checkUserExist.setString(1, username.trim()); // Trim to remove leading/trailing whitespaces

                // Execute the query to check if the user exists
                try (ResultSet resultSet = checkUserExist.executeQuery()) {
                    // If resultSet is empty, the user does not exist
                    return resultSet.isBeforeFirst();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
    // Update the score for a given username
    public static void updateScore(String username, int newScore) {
        try (Connection connection = DriverManager.getConnection(
                CommonConstants.DB_URL,
                CommonConstants.DB_USERNAME,
                CommonConstants.DB_PASSWORD
        )) {
            // Prepare the SQL statement for updating the score
            try (PreparedStatement updateScoreStatement = connection.prepareStatement(
                    "UPDATE " + CommonConstants.DB_PLAYERS_TABLE_NAME +
                            " SET SCORE = ? WHERE LOWER(USERNAME) = LOWER(?)"
            )) {
                // Set parameters for the statement
                updateScoreStatement.setInt(1, newScore);

                // Add a null check before invoking trim()
                if (username != null) {
                    updateScoreStatement.setString(2, username.trim());
                } else {
                    // Handle the case where username is null (optional)
                    System.out.println("Warning: Username is null.");
                    return;
                }

                // Execute the update query
                updateScoreStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
