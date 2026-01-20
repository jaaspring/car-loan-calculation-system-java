/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author User
 */
public class manageUsersFrame extends javax.swing.JFrame {
    private String userId;
    private String username;
    /**
     * Creates new form manageUsers
     */
    public manageUsersFrame() {
        initComponents();
    }

        // Constructor to accept userId and username
    public manageUsersFrame(String userId, String username) {
        this.userId = userId;
        this.username = username;
        initComponents();
        setTitle("Manage Users");
        this.setLocationRelativeTo(null); // Center the window
        loadUsersData();// Center the window
    }

    // Method to load users data into JTable
   
    // Method to load users data into JTable
    private void loadUsersData() {
    DefaultTableModel model = (DefaultTableModel) tblUsers.getModel();
    model.setRowCount(0); // Clear any existing rows

    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/loan_calculator", "root", "");
         PreparedStatement pst = con.prepareStatement("SELECT * FROM users")) {
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            model.addRow(new Object[] {
                rs.getString("username"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("role"),
                "Edit", // Edit button
                "Delete" // Delete button
            });
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading users data: " + e.getMessage());
    }
}



    private void deleteUser(String username) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/loan_calculator", "root", "");
             PreparedStatement pst = con.prepareStatement("DELETE FROM users WHERE username = ?")) {
            pst.setString(1, username);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
                loadUsersData(); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(this, "User not found in the database.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage());
        }
    }

 private void addNewUser() {
    // Create fields for user input (username, name, email, phone, role)
    JTextField usernameField = new JTextField();
    JTextField nameField = new JTextField();
    JTextField emailField = new JTextField();
    JTextField phoneField = new JTextField();
    JTextField roleField = new JTextField();
    JPasswordField passwordField = new JPasswordField();

    // Show a dialog to get the user's information
    Object[] message = {
        "Username:", usernameField,
        "Name:", nameField,
        "Email:", emailField,
        "Phone:", phoneField,
        "Role (admin/user):", roleField,
        "Password:", passwordField
    };

    int option = JOptionPane.showConfirmDialog(this, message, "Add New User", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION) {
        // Get the values entered by the user
        String username = usernameField.getText();
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String password = new String(passwordField.getPassword());
        String role = roleField.getText();

        // Validate input
        if (username.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || role.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled!");
        } else {
            try {
                // Insert the new user into the database
                insertUser(username, name, email, phone, password, role);
            }catch (Exception e) {
                // Handle any other unexpected exceptions
                JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "Unexpected Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace(); // Optionally log the error for debugging
            }
            // Handle SQLException (Database error)
            // Optionally log the error for debugging
            
        }
    }
}

private void insertUser(String username, String name, String email, String phone, String password, String role) {
    String sql = "INSERT INTO users (username, name, email, phone, password, role) VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/loan_calculator", "root", "");
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setString(1, username);
        pst.setString(2, name);
        pst.setString(3, email);
        pst.setString(4, phone);
        pst.setString(5, password);  // Make sure to hash the password in production
        pst.setString(6, role);

        int rowsAffected = pst.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(this, "User added successfully!");
            loadUsersData(); // Refresh the table with updated data
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add user.");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error adding user: " + e.getMessage());
    }
}





    // Add your code for Edit and Delete functionalities here


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new Styles.GradientPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUsers = new javax.swing.JTable();
        AddButton = Styles.createRoundedButton("Add User");
        jLabel1 = new javax.swing.JLabel();
        BackButton = Styles.createRoundedButton("Back");
        ExitButton = Styles.createRoundedButton("Exit");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tblUsers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Username", "Name", "Email", "Phone", "Role", "Edit", "Delete"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, true, true, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblUsers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Delete(evt);
                Edit(evt);
                tblUsersMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblUsers);

        AddButton.setBackground(new java.awt.Color(51, 204, 0));
        AddButton.setFont(new java.awt.Font("Century Gothic", 1, 12)); // NOI18N
        AddButton.setForeground(new java.awt.Color(255, 255, 255));
        AddButton.setText("Add New User");
        AddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        jLabel1.setText("Manage Users");

        BackButton.setBackground(new java.awt.Color(0, 0, 0));
        BackButton.setFont(new java.awt.Font("Century Gothic", 1, 12)); // NOI18N
        BackButton.setForeground(new java.awt.Color(255, 255, 255));
        BackButton.setText("Back");
        BackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackButtonActionPerformed(evt);
            }
        });

        ExitButton.setBackground(new java.awt.Color(204, 51, 0));
        ExitButton.setFont(new java.awt.Font("Century Gothic", 1, 12)); // NOI18N
        ExitButton.setForeground(new java.awt.Color(255, 255, 255));
        ExitButton.setText("Exit");
        ExitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(BackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(ExitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(29, 29, 29)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 862, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(41, 41, 41)
                            .addComponent(AddButton))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(393, 393, 393)
                            .addComponent(jLabel1))))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(49, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(AddButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ExitButton)
                    .addComponent(BackButton))
                .addGap(28, 28, 28))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void editUser(String username) {
    // Fetch the current user details from the database
    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/loan_calculator", "root", "");
         PreparedStatement pst = con.prepareStatement("SELECT * FROM users WHERE username = ?")) {
        
        pst.setString(1, username);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            // If user exists, pre-fill the fields with existing data
            String currentName = rs.getString("name");
            String currentEmail = rs.getString("email");
            String currentPhone = rs.getString("phone");
            String currentRole = rs.getString("role");

            // Open the Edit User dialog with current data pre-filled
            JTextField nameField = new JTextField(currentName);
            JTextField emailField = new JTextField(currentEmail);
            JTextField phoneField = new JTextField(currentPhone);
            JTextField roleField = new JTextField(currentRole);
            
            Object[] message = {
                "Name:", nameField,
                "Email:", emailField,
                "Phone:", phoneField,
                "Role:", roleField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Edit User", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                // Retrieve edited data
                String newName = nameField.getText();
                String newEmail = emailField.getText();
                String newPhone = phoneField.getText();
                String newRole = roleField.getText();
                
                // Update the user data in the database
                updateUser(username, newName, newEmail, newPhone, newRole);
            }
        } else {
            JOptionPane.showMessageDialog(this, "User not found in the database.");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error fetching user data: " + e.getMessage());
    }
}

// Method to update user details in the database
private void updateUser(String username, String name, String email, String phone, String role) {
    String sql = "UPDATE users SET name = ?, email = ?, phone = ?, role = ? WHERE username = ?";

    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/loan_calculator", "root", "");
         PreparedStatement pst = con.prepareStatement(sql)) {
        
        pst.setString(1, name);
        pst.setString(2, email);
        pst.setString(3, phone);
        pst.setString(4, role);
        pst.setString(5, username);

        int rowsAffected = pst.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(this, "User details updated successfully!");
            loadUsersData(); // Refresh the table with updated data
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update user.");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error updating user: " + e.getMessage());
    }
}
  

    // Delete user method

    private void AddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddButtonActionPerformed
            addNewUser(); 
    }//GEN-LAST:event_AddButtonActionPerformed

    private void ExitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitButtonActionPerformed
        // TODO add your handling code here:
           int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0); // Close the application
        } // TODO add your handling code here:
    }//GEN-LAST:event_ExitButtonActionPerformed

    private void Delete(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Delete
        // TODO add your handling code here:
    }//GEN-LAST:event_Delete

    private void Edit(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Edit
        // TODO add your handling code here:
    }//GEN-LAST:event_Edit

    private void tblUsersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblUsersMouseClicked
        int row = tblUsers.getSelectedRow(); // Get the clicked row index
        int column = tblUsers.getSelectedColumn(); // Get the clicked column index

        // Check if the clicked column is "Edit" or "Delete"
        if (column == 5) { // "Edit" column (assumed to be column 5)
            String username = tblUsers.getValueAt(row, 0).toString(); // Get the username from the row
            editUser(username); // Call edit method
        } else if (column == 6) { // "Delete" column (assumed to be column 6)
            String username = tblUsers.getValueAt(row, 0).toString(); // Get the username from the row
            deleteUser(username); // Call delete method
        }
    }//GEN-LAST:event_tblUsersMouseClicked

    private void BackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackButtonActionPerformed
        this.dispose(); // Close the current window
        new AdminDashboardFrame(userId, username).setVisible(true);
    }//GEN-LAST:event_BackButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(manageUsersFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(manageUsersFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(manageUsersFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(manageUsersFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new manageUsersFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddButton;
    private javax.swing.JButton BackButton;
    private javax.swing.JButton ExitButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblUsers;
    // End of variables declaration//GEN-END:variables
}
