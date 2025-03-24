package loginucan;
import java.awt.HeadlessException;
import javax.swing.*;
import java.sql.*;
 public class LogINA {
    public static void main(String[] args) {
     
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        
        // UCanAccess connection URL
        String url = "jdbc:ucanaccess://C:/Users/ArgelPascua/Documents/Datos.accdb";

        try {
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
                    // Load UCanAccess Driver
                    Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

                    // Establish the database connection
                    conn = DriverManager.getConnection(url);

                    // Check if username or password is empty
                    if (userField.getText().isEmpty() || passField.getPassword().length == 0) {
                        JOptionPane.showMessageDialog(null, "Please enter both username and password", "LOGIN", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }

                    // SQL Query
                    String sql = "SELECT * FROM TblUsers WHERE Username = ? AND Password = ?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, userField.getText());
                    pstmt.setString(2, new String(passField.getPassword()));

                    rs = pstmt.executeQuery();

                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null, "LOGIN SUCCESSFUL", "LOGIN", JOptionPane.INFORMATION_MESSAGE);
                        loginSuccessful = true;
                        
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

        } catch (HeadlessException | ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Exception", JOptionPane.WARNING_MESSAGE);
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

 
   }