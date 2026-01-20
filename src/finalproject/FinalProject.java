package finalproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class FinalProject {

    public static void main(String[] args) {
        // Check database connection
        try (Connection connection = connectDatabase()) {
            System.out.println("Connection successful!");
        } catch (SQLException ex) {
            System.err.println("Connection failed: " + ex.getMessage());
        }
    }

    // Method to connect to the database
    private static Connection connectDatabase() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/ps"; // Replace with your database name
        String user = "root"; // Replace with your MySQL username
        String password = ""; // Replace with your MySQL password
        return DriverManager.getConnection(url, user, password);
    }
}
