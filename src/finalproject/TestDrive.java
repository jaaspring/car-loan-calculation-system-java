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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.swing.JOptionPane;

/**
 *
 * @author Ezzah
 */
public class TestDrive extends javax.swing.JFrame {

    private String userId;
    private String username;
    private String selectedCarModelVariant;  
    private LinkedHashMap<String, LinkedHashMap<String, String>> carDetails;  // Store all car models
    private boolean isCarPreselected;

    /**
     * Creates new form TestDrive
     */
    public TestDrive() {
        initComponents();
        setLocationRelativeTo(null); // Center the frame
        loadLocations();
    }
    // Constructor when coming from HomeFrame (User must select a car)
    public TestDrive(String userId, String username) {
        this.userId = userId;
        this.username = username;
        this.isCarPreselected = false;  // User must pick a car

        initComponents();
        setLocationRelativeTo(null);
        loadLocations();
        setUserDetails();

        carDetails = fetchCarDetailsFromDatabase();  // Fetch all car models
        updateComboBoxModels();  // Populate the combo box for user selection
    }
    
    // Constructor when coming from CarComparisonFrame (Car is preselected)
    public TestDrive(String userId, String username, String selectedCarModelVariant) {
        this.userId = userId;
        this.username = username;
        this.selectedCarModelVariant = selectedCarModelVariant;
        this.isCarPreselected = true;  // Car is already chosen

        initComponents();
        setLocationRelativeTo(null);
        loadLocations();
        setUserDetails();

        carDetails = fetchCarDetailsFromDatabase();  // Fetch all car models
        updateComboBoxModels();  // Populate the combo box

        // Set the preselected car in the combo box
        vehicleComboBox.setSelectedItem(selectedCarModelVariant);
        vehicleComboBox.repaint();

        // Auto-fill details based on the preselected car
        updateCarDetails(selectedCarModelVariant);
    }
    
    // Fetch all car models and variants from the database
private LinkedHashMap<String, LinkedHashMap<String, String>> fetchCarDetailsFromDatabase() {
    LinkedHashMap<String, LinkedHashMap<String, String>> details = new LinkedHashMap<>();
    String query = "SELECT model, variant FROM car_details ORDER BY id ASC"; 

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            String key = rs.getString("model") + " - " + rs.getString("variant");
            details.put(key, new LinkedHashMap<>());  // Only store the model name for now
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage());
    }

    return details;
}

// Populate the vehicle combo box
private void updateComboBoxModels() {
    if (carDetails == null || carDetails.isEmpty()) {
        return;
    }

    ArrayList<String> variants = new ArrayList<>(carDetails.keySet());
    vehicleComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(variants.toArray(new String[0])));
}



// Update labels with selected car details
private void updateCarDetails(String selectedCar) {
    // No pop-up, just store the selected car
    this.selectedCarModelVariant = selectedCar;
}

// Method to load user details (name, phone, email) based on the logged-in user
      // Method to load user details (name, phone, email) based on the logged-in user
    private void setUserDetails() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/loan_calculator", "root", "");
             PreparedStatement pst = con.prepareStatement("SELECT name, phone, email FROM users WHERE id = ?")) {

            pst.setString(1, userId);  // Use the provided userId to fetch the user details
            ResultSet rs = pst.executeQuery();

            // If the user exists, set the name, phone, and email to the text fields
            if (rs.next()) {
                nameTextField.setText(rs.getString("name"));
                phoneTextField.setText(rs.getString("phone"));
                emailTextField.setText(rs.getString("email"));
            } else {
                JOptionPane.showMessageDialog(this, "User not found.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving user details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to load locations into the locationComboBox
    private void loadLocations() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/loan_calculator", "root", "");
             PreparedStatement pst = con.prepareStatement("SELECT DISTINCT location_name FROM locations_showrooms");
             ResultSet rs = pst.executeQuery()) {

            // Clear existing items from locationComboBox
            locationComboBox.removeAllItems();

            // Add a default empty item
            locationComboBox.addItem("Select Location");

            // Loop through the result set and add each location to the combo box
            while (rs.next()) {
                String location = rs.getString("location_name");
                locationComboBox.addItem(location);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to load showrooms for the selected location into showroomComboBox
    private void loadShowroomsForLocation(String location) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/loan_calculator", "root", "");
             PreparedStatement pst = con.prepareStatement("SELECT showroom_name FROM locations_showrooms WHERE location_name = ?")) {

            pst.setString(1, location);  // Set the location parameter

            try (ResultSet rs = pst.executeQuery()) {
                // Clear existing items from showroomComboBox
                showroomComboBox.removeAllItems();
                showroomComboBox.addItem("Select Showroom");  // Default item

                // Loop through the result set and add each showroom to the combo box
                while (rs.next()) {
                    String showroom = rs.getString("showroom_name");
                    showroomComboBox.addItem(showroom);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Validate form inputs before proceeding with the booking
    private boolean validateTestDriveForm() {
        if (!isCarPreselected && (vehicleComboBox.getSelectedItem() == null || vehicleComboBox.getSelectedItem().toString().isEmpty())) {
            JOptionPane.showMessageDialog(this, "Please select a car for the test drive!");
            return false;
        }
        if (nameTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required!");
            return false;
        }
        if (phoneTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone number is required!");
            return false;
        }
        if (emailTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email is required!");
            return false;
        }
            // Validate Location
        if (locationComboBox.getSelectedItem() == null || locationComboBox.getSelectedItem().toString().equals("Select Location")) {
            JOptionPane.showMessageDialog(this, "Please select a valid location!");
            return false;
        }

        // Validate Showroom
        if (showroomComboBox.getSelectedItem() == null || showroomComboBox.getSelectedItem().toString().equals("Select Showroom")) {
            JOptionPane.showMessageDialog(this, "Please select a valid showroom!");
            return false;
        }
        if (DateTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Date is required!");
            return false;
        }
        if (TimeTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Time is required!");
            return false;
        }
        return true; // All fields are valid
    }
        

    // Book the appointment if validation passes
    public void bookAppointment() {
    if (validateTestDriveForm()) {
        String carModelVariant;
        if (isCarPreselected) {
            carModelVariant = selectedCarModelVariant;  // Use preselected car
        } else {
            carModelVariant = (String) vehicleComboBox.getSelectedItem();  // Get car from combo box
        }

        String name = nameTextField.getText().trim();
        String phone = phoneTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String location = locationComboBox.getSelectedItem().toString();
        String showroom = showroomComboBox.getSelectedItem().toString();
        String date = DateTextField.getText().trim();
        String time = TimeTextField.getText().trim();

        // Ensure time is in HH:mm:ss format
        if (time.length() == 5) {  
            time = time + ":00";  // Add seconds to make it HH:mm:ss
        }

        try {
            java.sql.Date sqlDate = java.sql.Date.valueOf(date);
            java.sql.Time sqlTime = java.sql.Time.valueOf(time);

            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/loan_calculator", "root", "");
                 PreparedStatement pst = con.prepareStatement("INSERT INTO test_drive (name, phone, email, car_model_variant, location, showroom, date, time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

                pst.setString(1, name);
                pst.setString(2, phone);
                pst.setString(3, email);
                pst.setString(4, carModelVariant);  // Save selected car model
                pst.setString(5, location);
                pst.setString(6, showroom);
                pst.setDate(7, sqlDate);
                pst.setTime(8, sqlTime);

                int result = pst.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Thank you for booking! We will contact you soon.");
                    clearFormFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to book test drive.");
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid date or time format.");
        }
    }
}

    // Clear the form fields after successful booking
    private void clearFormFields() {
        locationComboBox.setSelectedIndex(0);
        showroomComboBox.setSelectedIndex(0);
        DateTextField.setText("");
        TimeTextField.setText("");
    }


    /**
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new Styles.GradientPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        backButton = Styles.createRoundedButton("Back");
        exitButton = Styles.createRoundedButton("Exit");
        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        phoneTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        emailTextField = new javax.swing.JTextField();
        locationComboBox = new javax.swing.JComboBox<>();
        showroomComboBox = new javax.swing.JComboBox<>();
        TimeTextField = new javax.swing.JTextField();
        DateTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        bookAppointmentButton = Styles.createRoundedButton("Book");
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        vehicleComboBox = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        nameLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Century Gothic", 1, 16)); // NOI18N
        jLabel1.setText("GET A FEEL FOR A PROTON BY BOOKING A TEST DRIVE WITH US.");

        jLabel4.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel4.setText("Just fill in your details below and we’ll do the rest. ");

        backButton.setBackground(new java.awt.Color(0, 0, 0));
        backButton.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        backButton.setForeground(new java.awt.Color(255, 255, 255));
        backButton.setText("Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        exitButton.setBackground(new java.awt.Color(204, 51, 0));
        exitButton.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        exitButton.setForeground(new java.awt.Color(255, 255, 255));
        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        nameLabel.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        nameLabel.setText("Name");

        nameTextField.setEditable(false);
        nameTextField.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N

        phoneTextField.setEditable(false);
        phoneTextField.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        phoneTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                phoneTextFieldActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel2.setText("Phone");

        jLabel3.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel3.setText("Email");

        emailTextField.setEditable(false);
        emailTextField.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N

        locationComboBox.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        locationComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        locationComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                locationComboBoxActionPerformed(evt);
            }
        });

        showroomComboBox.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        showroomComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        showroomComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showroomComboBoxActionPerformed(evt);
            }
        });

        TimeTextField.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        TimeTextField.setToolTipText("eg:11:30 (HH:MM)");
        TimeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TimeTextFieldActionPerformed(evt);
            }
        });

        DateTextField.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        DateTextField.setToolTipText("eg:2002-02-20 (YYYY-MM-DD)");
        DateTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DateTextFieldActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        jLabel5.setText("PERSONAL DETAILS");

        jLabel6.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        jLabel6.setText("TEST DRIVE DETAILS");

        jLabel7.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel7.setText("Location");

        jLabel8.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel8.setText("Preffered Showroom");

        jLabel9.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel9.setText("Date*");

        jLabel10.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel10.setText("Time*");

        bookAppointmentButton.setBackground(new java.awt.Color(0, 0, 0));
        bookAppointmentButton.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        bookAppointmentButton.setForeground(new java.awt.Color(255, 255, 255));
        bookAppointmentButton.setText("Book");
        bookAppointmentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bookAppointmentButtonActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Century Gothic", 0, 10)); // NOI18N
        jLabel11.setText("eg:2002-02-20 (YYYY-MM-DD)");
        jLabel11.setToolTipText("");

        jLabel12.setFont(new java.awt.Font("Century Gothic", 0, 10)); // NOI18N
        jLabel12.setText("eg:11:30 (HH:MM)");
        jLabel12.setToolTipText("eg:11:30 (HH:MM)");

        vehicleComboBox.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        vehicleComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        vehicleComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vehicleComboBoxActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        jLabel13.setText("CAR DETAILS");

        nameLabel1.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        nameLabel1.setText("Car Model");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(220, 220, 220)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(nameLabel, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(DateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(TimeTextField)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel12))))
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(vehicleComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 238, Short.MAX_VALUE)
                                .addComponent(showroomComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(locationComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(emailTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(phoneTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(nameTextField, javax.swing.GroupLayout.Alignment.LEADING))
                            .addComponent(jLabel13)
                            .addComponent(bookAppointmentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nameLabel1)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(95, 95, 95)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(69, 69, 69)
                                .addComponent(jLabel4)))))
                .addContainerGap(107, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(nameLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vehicleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(12, 12, 12)
                .addComponent(nameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(phoneTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(emailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(locationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showroomComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(DateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addGap(18, 18, 18)
                .addComponent(bookAppointmentButton)
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(exitButton)
                    .addComponent(backButton))
                .addGap(25, 25, 25))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
 // Load locations into locationComboBox
  

    private void showroomComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showroomComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_showroomComboBoxActionPerformed

    private void phoneTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_phoneTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_phoneTextFieldActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        // TODO add your handling code here:
           int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);  // Exit the application
        }
    }//GEN-LAST:event_exitButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        // TODO add your handling code here:
        this.dispose();  // Close the current frame
        new HomeFrame(userId, username).setVisible(true);   // Navigate back to home
    }//GEN-LAST:event_backButtonActionPerformed



    private void bookAppointmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bookAppointmentButtonActionPerformed
       bookAppointment();
    }//GEN-LAST:event_bookAppointmentButtonActionPerformed

    private void locationComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_locationComboBoxActionPerformed
        // TODO add your handling code here:
           String selectedLocation = (String) locationComboBox.getSelectedItem();
    System.out.println("Selected Location: " + selectedLocation);  // Debugging line

    if (selectedLocation != null && !selectedLocation.equals("Select Location") && !selectedLocation.isEmpty()) {
        // Load the showrooms for the selected location
        loadShowroomsForLocation(selectedLocation);
    } else {
        // If no location is selected or invalid, clear the showroom combo box
        showroomComboBox.removeAllItems();
        showroomComboBox.addItem("Select Showroom");
    }
    }//GEN-LAST:event_locationComboBoxActionPerformed

    private void DateTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DateTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DateTextFieldActionPerformed

    private void TimeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TimeTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TimeTextFieldActionPerformed

    private void vehicleComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vehicleComboBoxActionPerformed

 String selectedCar = (String) vehicleComboBox.getSelectedItem();
    if (selectedCar != null && !selectedCar.isEmpty()) {
        updateCarDetails(selectedCar);
    }
    }//GEN-LAST:event_vehicleComboBoxActionPerformed

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
            java.util.logging.Logger.getLogger(TestDrive.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TestDrive.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TestDrive.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TestDrive.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TestDrive().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField DateTextField;
    private javax.swing.JTextField TimeTextField;
    private javax.swing.JButton backButton;
    private javax.swing.JButton bookAppointmentButton;
    private javax.swing.JTextField emailTextField;
    private javax.swing.JButton exitButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JComboBox<String> locationComboBox;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel nameLabel1;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JTextField phoneTextField;
    private javax.swing.JComboBox<String> showroomComboBox;
    private javax.swing.JComboBox<String> vehicleComboBox;
    // End of variables declaration//GEN-END:variables
}
