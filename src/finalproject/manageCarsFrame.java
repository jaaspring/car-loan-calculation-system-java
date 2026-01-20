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
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author User
 */
public class manageCarsFrame extends javax.swing.JFrame {
    private String userId;
    private String username;
    /**
     * Creates new form manageCarsFrame
     */
    public manageCarsFrame() {
        initComponents();
    }
        public manageCarsFrame(String userId, String username) {
        this.userId = userId;
        this.username = username;
        initComponents();
        setTitle("Manage Cars");
        this.setLocationRelativeTo(null); // Center the window
        loadCarsData(); // Load cars data into JTable // Center the window
        // You can now use userId and username in this frame as needed
    }
private void loadCarsData() {
    DefaultTableModel model = (DefaultTableModel) tblCars.getModel();
    model.setRowCount(0); // Clear any existing rows

    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/loan_calculator", "root", "");
         PreparedStatement pst = con.prepareStatement("SELECT * FROM car_details")) {
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("id"), // Add carId as the first column
                rs.getString("model"),
                rs.getString("variant"),
                rs.getString("price"),
                rs.getString("engine"),
                rs.getString("transmission"),
                rs.getString("chassis"),
                rs.getString("performance"),
                rs.getString("image_path"),
                rs.getString("paint_type"),
                "Edit", // Action for Edit button
                "Delete" // Action for Delete button
            });
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading cars data: " + e.getMessage());
    }
}

   private void deleteCar(int carId) {
    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this car?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
    
    if (confirm == JOptionPane.YES_OPTION) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/loan_calculator", "root", "");
             PreparedStatement pst = con.prepareStatement("DELETE FROM car_details WHERE id = ?")) {
            
            pst.setInt(1, carId);  // Set the carId parameter to identify the car to delete
            int rowsAffected = pst.executeUpdate(); // Execute the delete query
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Car deleted successfully!");
                loadCarsData();  // Refresh the table with updated data
            } else {
                JOptionPane.showMessageDialog(this, "Car not found in the database.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting car: " + e.getMessage());
        }
    }
}

private void editCar(int carId) {
    // Fetch the current car details from the database using carId
    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/loan_calculator", "root", "");
         PreparedStatement pst = con.prepareStatement("SELECT * FROM car_details WHERE id = ?")) {
        
        pst.setInt(1, carId);  // Use carId to uniquely identify the car
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            // If car exists, pre-fill the fields with existing data
            String currentModel = rs.getString("model");
            String currentVariant = rs.getString("variant");
            String currentPrice = rs.getString("price");
            String currentEngine = rs.getString("engine");
            String currentTransmission = rs.getString("transmission");
            String currentChassis = rs.getString("chassis");
            String currentPerformance = rs.getString("performance");
            String currentPaintType = rs.getString("paint_type");

            // Open the Edit Car dialog with current data pre-filled
            JTextField modelField = new JTextField(currentModel);
            JTextField variantField = new JTextField(currentVariant);
            JTextField priceField = new JTextField(currentPrice);
            JTextField engineField = new JTextField(currentEngine);
            JTextField transmissionField = new JTextField(currentTransmission);
            JTextField chassisField = new JTextField(currentChassis);
            JTextField performanceField = new JTextField(currentPerformance);
            JTextField paintTypeField = new JTextField(currentPaintType);

            Object[] message = {
                "Model:", modelField,
                "Variant:", variantField,
                "Price:", priceField,
                "Engine:", engineField,
                "Transmission:", transmissionField,
                "Chassis:", chassisField,
                "Performance:", performanceField,
                "Paint Type:", paintTypeField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Edit Car", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                // Retrieve edited data
                String newModel = modelField.getText();
                String newVariant = variantField.getText();
                String newPrice = priceField.getText();
                String newEngine = engineField.getText();
                String newTransmission = transmissionField.getText();
                String newChassis = chassisField.getText();
                String newPerformance = performanceField.getText();
                String newPaintType = paintTypeField.getText();

                // Update the car data in the database using carId
                updateCar(carId, newModel, newVariant, newPrice, newEngine, newTransmission, newChassis, newPerformance, newPaintType);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Car not found in the database.");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error fetching car data: " + e.getMessage());
    }
}

private void updateCar(int carId, String model, String variant, String price, String engine, String transmission, String chassis, String performance, String paintType) {
    // Check if any required fields are empty
    if (model.trim().isEmpty() || variant.trim().isEmpty() || price.trim().isEmpty() || engine.trim().isEmpty() ||
        transmission.trim().isEmpty() || chassis.trim().isEmpty() || performance.trim().isEmpty() || paintType.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill all fields before submitting.", "Input Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

   
    String sql = "UPDATE car_details SET model = ?, variant = ?, price = ?, engine = ?, transmission = ?, chassis = ?, performance = ?, paint_type = ? WHERE id = ?";

    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/loan_calculator", "root", "");
         PreparedStatement pst = con.prepareStatement(sql)) {

        // Set values for the prepared statement
        pst.setString(1, model);
        pst.setString(2, variant);
        pst.setString(3, price);
        pst.setString(4, engine);
        pst.setString(5, transmission);
        pst.setString(6, chassis);
        pst.setString(7, performance);
        pst.setString(8, paintType);
        pst.setInt(9, carId);  // Use carId to uniquely identify the car

        int rowsAffected = pst.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(this, "Car details updated successfully!");
            loadCarsData(); // Refresh the table with updated data
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update car.");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error updating car: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    } catch (RuntimeException e) {
        JOptionPane.showMessageDialog(this, "Unexpected error occurred: " + e.getMessage(), "Runtime Error", JOptionPane.ERROR_MESSAGE);
    }
}


private void addNewCar(String model, String variant, String price, String engine, String transmission,
                        String chassis, String performance, String imagePath, String paintType) {

    // Check if any required fields are empty
    if (model.trim().isEmpty() || variant.trim().isEmpty() || price.trim().isEmpty() || engine.trim().isEmpty() ||
        transmission.trim().isEmpty() || chassis.trim().isEmpty() || performance.trim().isEmpty() ||
        imagePath.trim().isEmpty() || paintType.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill all fields before submitting.", "Input Error", JOptionPane.ERROR_MESSAGE);
        return;
    }


    String sql = "INSERT INTO car_details (model, variant, price, engine, transmission, chassis, performance, image_path, paint_type) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/loan_calculator", "root", "");
         PreparedStatement pst = con.prepareStatement(sql)) {

        // Set values for the prepared statement
        pst.setString(1, model);
        pst.setString(2, variant);
        pst.setString(3, price);
        pst.setString(4, engine);
        pst.setString(5, transmission);
        pst.setString(6, chassis);
        pst.setString(7, performance);
        pst.setString(8, imagePath);
        pst.setString(9, paintType);

        // Execute the insert query
        int rowsAffected = pst.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(this, "New car added successfully!");
            loadCarsData(); // Refresh the table with the new car
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add new car.");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error adding new car: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    } catch (RuntimeException e) {
        JOptionPane.showMessageDialog(this, "Unexpected error occurred: " + e.getMessage(), "Runtime Error", JOptionPane.ERROR_MESSAGE);
    }
}



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
        tblCars = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        btnAddCar = Styles.createRoundedButton("Add Car");
        BackButton = Styles.createRoundedButton("Back");
        ExitButton = Styles.createRoundedButton("Exit");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tblCars.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Id", "Model", "Variant", "Price", "Engine", "Transmission", "Chassis", "Performance", "Images", "Paint Type", "Edit", "Delete"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, true, true, true, true, true, true, true, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblCars.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Delete(evt);
                Edit(evt);
                tblCarsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCars);

        jLabel1.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        jLabel1.setText("Manage Cars");

        btnAddCar.setBackground(new java.awt.Color(51, 204, 0));
        btnAddCar.setForeground(new java.awt.Color(255, 255, 255));
        btnAddCar.setText("Add New Car");
        btnAddCar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddCarActionPerformed(evt);
            }
        });

        BackButton.setBackground(new java.awt.Color(0, 0, 0));
        BackButton.setForeground(new java.awt.Color(255, 255, 255));
        BackButton.setText("Back");
        BackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackButtonActionPerformed(evt);
            }
        });

        ExitButton.setBackground(new java.awt.Color(204, 51, 0));
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(BackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(ExitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addGap(18, 18, 18)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(10, 10, 10)
                                    .addComponent(btnAddCar))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 922, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(418, 418, 418)
                        .addComponent(jLabel1)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(btnAddCar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BackButton)
                    .addComponent(ExitButton))
                .addGap(34, 34, 34))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Delete(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Delete
        // TODO add your handling code here:
    }//GEN-LAST:event_Delete

    private void Edit(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Edit
        // TODO add your handling code here:
    }//GEN-LAST:event_Edit

    private void tblCarsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCarsMouseClicked
  
  int row = tblCars.getSelectedRow(); // Get the clicked row index
    int column = tblCars.getSelectedColumn(); // Get the clicked column index

    // Retrieve the carId from the first column (column 0)
    int carId = (int) tblCars.getValueAt(row, 0);  // carId is now in the first column (index 0)

    // Get the action (Edit/Delete) from the respective column
    String action = (String) tblCars.getValueAt(row, column);

    // If "Edit" is clicked in the "Edit" column (column 10)
    if (column == 10 && action.equals("Edit")) { 
        editCar(carId);  // Call the editCar method with carId
    }
    
    // If "Delete" is clicked in the "Delete" column (column 11)
    if (column == 11 && action.equals("Delete")) {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this car?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            deleteCar(carId);  // Call the deleteCar method with carId
        }
    }
    }//GEN-LAST:event_tblCarsMouseClicked

    private void btnAddCarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddCarActionPerformed
 // Create input fields for the car details
    JTextField modelField = new JTextField();
    JTextField variantField = new JTextField();
    JTextField priceField = new JTextField();
    JTextField engineField = new JTextField();
    JTextField transmissionField = new JTextField();
    JTextField chassisField = new JTextField();
    JTextField performanceField = new JTextField();
    JTextField imagePathField = new JTextField();
    JTextField paintTypeField = new JTextField("Solid");  // Default paint type

    // Show dialog with input fields
    Object[] message = {
        "Model:", modelField,
        "Variant:", variantField,
        "Price:", priceField,
        "Engine:", engineField,
        "Transmission:", transmissionField,
        "Chassis:", chassisField,
        "Performance:", performanceField,
        "Image Path:", imagePathField,
        "Paint Type:", paintTypeField
    };

    int option = JOptionPane.showConfirmDialog(this, message, "Add New Car", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION) {
        // Retrieve values from the input fields
        String model = modelField.getText();
        String variant = variantField.getText();
        String price = priceField.getText();
        String engine = engineField.getText();
        String transmission = transmissionField.getText();
        String chassis = chassisField.getText();
        String performance = performanceField.getText();
        String imagePath = imagePathField.getText();
        String paintType = paintTypeField.getText();

        // Call method to insert the new car data into the database
        addNewCar(model, variant, price, engine, transmission, chassis, performance, imagePath, paintType);
    }        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddCarActionPerformed

    private void BackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackButtonActionPerformed
           this.dispose(); // Close the current window
        new AdminDashboardFrame(userId, username).setVisible(true); // Open the Home frame        // TODO add your handling code here:
    }//GEN-LAST:event_BackButtonActionPerformed

    private void ExitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitButtonActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0); // Close the application
        } // TODO add your handling code here:
    }//GEN-LAST:event_ExitButtonActionPerformed

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
            java.util.logging.Logger.getLogger(manageCarsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(manageCarsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(manageCarsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(manageCarsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new manageCarsFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BackButton;
    private javax.swing.JButton ExitButton;
    private javax.swing.JButton btnAddCar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblCars;
    // End of variables declaration//GEN-END:variables
}
