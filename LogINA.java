import javax.swing.*;
import java.sql.*;

public class proj {
    // Update with your actual database URL, username, and password
    private static final String URL = "jdbc:mysql://localhost:3306/users"; // Replace with your database name
    private static final String USER = "root"; // Replace with your MySQL username
    private static final String PASSWORD = "your_password"; // Replace with your MySQL password

    public static void main(String[] args) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create GUI for username and password input
            JTextField userField = new JTextField();
            JPasswordField passField = new JPasswordField();
            Object[] message = {
                    "Username:", userField,
                    "Password:", passField
            };

            boolean loginSuccessful = false;

            while (!loginSuccessful) {
                int opt = JOptionPane.showConfirmDialog(null, message, "LOGIN", JOptionPane.OK_CANCEL_OPTION);

                if (opt == JOptionPane.OK_OPTION) {
                    // Establish the database connection
                    conn = DriverManager.getConnection(URL, USER, PASSWORD);

                    // Check if username or password is empty
                    if (userField.getText().isEmpty() || passField.getPassword().length == 0) {
                        JOptionPane.showMessageDialog(null, "Please enter both username and password", "LOGIN", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }

                    // SQL Query
                    String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, userField.getText());
                    pstmt.setString(2, new String(passField.getPassword()));

                    rs = pstmt.executeQuery();

                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null, "LOGIN SUCCESSFUL", "LOGIN", JOptionPane.INFORMATION_MESSAGE);
                        loginSuccessful = true;
                        showMenu(); // Call the menu method after successful login
                    } else {
                        JOptionPane.showMessageDialog(null, "LOGIN FAILED! Try again.", "LOGIN", JOptionPane.ERROR_MESSAGE);
                        userField.setText("");
                        passField.setText("");
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "LOGIN CANCELLED!", "LOGIN", JOptionPane.WARNING_MESSAGE);
                    break;
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Exception", JOptionPane.WARNING_MESSAGE);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void showMenu() {
        String[] options = {"Add User", "Edit User", "Remove User", "View Users", "Exit"};
        int choice;

        do {
            choice = JOptionPane.showOptionDialog(null, "Menu", "User  Management",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            switch (choice) {
                case 0:
                    addUser ();
                    break;
                case 1:
                    editUser ();
                    break;
                case 2:
                    removeUser ();
                    break;
                case 3:
                    viewUsers();
                    break;
                case 4:
                    System.exit(0);
                    break;
                default:
                    break;
            }
        } while (choice != -1);
    }

    private static void addUser () {
        String username = JOptionPane.showInputDialog("Enter new username:");
        String password = JOptionPane.showInputDialog("Enter new password:");

        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "User  added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void editUser () {
        String username = JOptionPane.showInputDialog("Enter username of the user to edit:");
        String newPassword = JOptionPane.showInputDialog("Enter new password:");

        String query = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, username);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Password updated successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "User  not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void removeUser () {
        String username = JOptionPane.showInputDialog("Enter username of the user to remove:");

        String query = "DELETE FROM users WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "User  removed successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "User  not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewUsers() {
        String query = "SELECT * FROM users";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            StringBuilder users = new StringBuilder("Users:\n");
            while (resultSet.next()) {
                users.append("Username: ").append(resultSet.getString("Username")).append("\n");
            }
            JOptionPane.showMessageDialog(null, users.toString());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
