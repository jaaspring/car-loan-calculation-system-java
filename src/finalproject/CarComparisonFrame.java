/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalproject;

import java.awt.Image;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Afifah
 */
public class CarComparisonFrame extends javax.swing.JFrame {
     private String userId;
    private String username;
    private String selectedModel; 
    private String selectedVariant; // Store the selected model and variant
    private LinkedHashMap<String, LinkedHashMap<String, String>> carDetails; // Preserve order

    // Constructor for non-logged-in users
    public CarComparisonFrame() {
        initComponents();
        carDetails = fetchCarDetailsFromDatabase(); // Fetch car data from DB
        updateComboBoxModels(); // Update combo boxes with available models
        setLocationRelativeTo(null); // Center window
    }
   // Constructor for logged-in users (from HomeFrame, no pre-selected car)
    public CarComparisonFrame(String userId, String username) {
        this.userId = userId;
        this.username = username;

        initComponents();
        setLocationRelativeTo(null);

        carDetails = fetchCarDetailsFromDatabase();
        updateComboBoxModels();

        resetCar1Fields();
        resetCar2Fields();
    }

    // Constructor for logged-in users (from CarDetailsFrame, Car 1 should be pre-selected)
public CarComparisonFrame(String userId, String username, String selectedModelWithVariant) {
    this.userId = userId;
    this.username = username;

    // Split the passed combined value into model and variant
    String[] parts = selectedModelWithVariant.trim().split(" - ");
    if (parts.length == 2) {
        this.selectedModel = parts[0];
        this.selectedVariant = parts[1];
    } else {
        JOptionPane.showMessageDialog(this, "Invalid model format. Please select a valid model.");
        return;
    }

    initComponents();
    setLocationRelativeTo(null);

    // Fetch car details from the database
    carDetails = fetchCarDetailsFromDatabase();
    updateComboBoxModels(); // Ensure combo boxes get updated

    // Set Car 1's combo box correctly
    String selectedItem = selectedModel + " - " + selectedVariant;
    vehicleComboBox1.setSelectedItem(selectedItem);
    vehicleComboBox1.repaint();
    
    // Force trigger the update
    updateDetails(vehicleComboBox1, lblPrice1, lblEngine1, lblTrans1, lblChassis1, lblPerf1, carImageLabel1);
}


    // Fetch car details from MySQL
 private LinkedHashMap<String, LinkedHashMap<String, String>> fetchCarDetailsFromDatabase() {
    LinkedHashMap<String, LinkedHashMap<String, String>> details = new LinkedHashMap<>();
    String query = "SELECT model, variant, price, engine, transmission, chassis, performance, image_path FROM car_details ORDER BY id ASC"; 

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            LinkedHashMap<String, String> carData = new LinkedHashMap<>();
            carData.put("Model", rs.getString("model"));
            carData.put("Price", rs.getString("price"));
            carData.put("Engine", rs.getString("engine"));
            carData.put("Transmission", rs.getString("transmission"));
            carData.put("Chassis", rs.getString("chassis"));
            carData.put("Performance", rs.getString("performance"));
            carData.put("ImagePath", rs.getString("image_path"));

            // Create key with model + variant
            String key = rs.getString("model") + " - " + rs.getString("variant");
            details.put(key, carData);

            // ✅ Debugging output
            System.out.println("Loaded into carDetails: " + key);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage());
    }

    return details;
}

    // Update combo box with available car models
    private void updateComboBoxModels() {
        if (carDetails == null || carDetails.isEmpty()) {
            return;
        }

        ArrayList<String> variants = new ArrayList<>(carDetails.keySet());

        vehicleComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(variants.toArray(new String[0])));
        vehicleComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(variants.toArray(new String[0])));

        vehicleComboBox1.repaint();
        vehicleComboBox2.repaint();
    }

    // Update car details based on selected variant
    private void updateDetails(JComboBox<String> dropdown, JLabel price, JLabel engine, JLabel transmission, JLabel chassis, JLabel performance, JLabel carImageLabel) {
        String selectedItem = (String) dropdown.getSelectedItem();
        if (selectedItem == null || !carDetails.containsKey(selectedItem)) {
            return;
        }

        LinkedHashMap<String, String> details = carDetails.get(selectedItem);
        price.setText(details.get("Price"));
        engine.setText(details.get("Engine"));
        transmission.setText(details.get("Transmission"));
        chassis.setText(details.get("Chassis"));
        performance.setText(details.get("Performance"));

        setCarImage(carImageLabel, details.get("ImagePath"));
    }

    // Set car image
    private void setCarImage(JLabel carImageLabel, String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                ImageIcon originalIcon = new ImageIcon(getClass().getResource("/" + imagePath));
                Image resizedImage = originalIcon.getImage().getScaledInstance(carImageLabel.getWidth(), carImageLabel.getHeight(), Image.SCALE_SMOOTH);
                carImageLabel.setIcon(new ImageIcon(resizedImage));
            } catch (Exception e) {
                carImageLabel.setIcon(null);
            }
        } else {
            carImageLabel.setIcon(null);
        }
    }

    // Reset Car 1 fields
    private void resetCar1Fields() {
        lblPrice1.setText("");
        lblEngine1.setText("");
        lblTrans1.setText("");
        lblChassis1.setText("");
        lblPerf1.setText("");
        carImageLabel1.setIcon(null);
        vehicleComboBox1.setSelectedIndex(-1);
    }

    // Reset Car 2 fields
    private void resetCar2Fields() {
        lblPrice2.setText("");
        lblEngine2.setText("");
        lblTrans2.setText("");
        lblChassis2.setText("");
        lblPerf2.setText("");
        carImageLabel2.setIcon(null);
        vehicleComboBox2.setSelectedIndex(-1);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel1 = new Styles.GradientPanel();
        car2 = new Styles.RoundedBevelPanel();
        titlePrice2 = new javax.swing.JLabel();
        titleEngine2 = new javax.swing.JLabel();
        titleTrans2 = new javax.swing.JLabel();
        titleChassis2 = new javax.swing.JLabel();
        vehicleComboBox2 = new javax.swing.JComboBox<>();
        titlePerf2 = new javax.swing.JLabel();
        titleModel2 = new javax.swing.JLabel();
        lblEngine2 = new javax.swing.JLabel();
        lblTrans2 = new javax.swing.JLabel();
        lblPrice2 = new javax.swing.JLabel();
        lblPerf2 = new javax.swing.JLabel();
        lblChassis2 = new javax.swing.JLabel();
        btnTestDrive2 = Styles.createRoundedButton("Test Drive");
        carImageLabel2 = new javax.swing.JLabel();
        car1 = new Styles.RoundedBevelPanel();
        titlePrice1 = new javax.swing.JLabel();
        titleEngine1 = new javax.swing.JLabel();
        titleTrans1 = new javax.swing.JLabel();
        titleChassis1 = new javax.swing.JLabel();
        vehicleComboBox1 = new javax.swing.JComboBox<>();
        titlePerf1 = new javax.swing.JLabel();
        titleModel1 = new javax.swing.JLabel();
        lblEngine1 = new javax.swing.JLabel();
        lblTrans1 = new javax.swing.JLabel();
        lblPrice1 = new javax.swing.JLabel();
        lblPerf1 = new javax.swing.JLabel();
        lblChassis1 = new javax.swing.JLabel();
        btnTestDrive1 = Styles.createRoundedButton("Test Drive");
        carImageLabel1 = new javax.swing.JLabel();
        exitButton = Styles.createRoundedButton("Exit");
        backButton = Styles.createRoundedButton("Back");
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Loan Calculation System");
        setBackground(new java.awt.Color(255, 204, 0));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setForeground(new java.awt.Color(255, 255, 255));

        car2.setBackground(new java.awt.Color(255, 255, 255));

        titlePrice2.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        titlePrice2.setText("PRICE");

        titleEngine2.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        titleEngine2.setText("ENGINE");

        titleTrans2.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        titleTrans2.setText("TRANSMISSIONS");

        titleChassis2.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        titleChassis2.setText("CHASSIS");

        vehicleComboBox2.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        vehicleComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "X50 1.5T Standard", "X50 1.5T Executive", "X50 1.5T Premium", "X50 1.5 TGDi Flagship",
            "X70 1.5 TGDi Standard", "X70 1.5 TGDi Executive", "X70 1.5 TGDi Premium", "X70 1.8 TGDi Premium X",
            "S70 1.5T Executive", "S70 1.5T Premium", "S70 1.5T Flagship",
            "Saga 1.3 Standard MT", "Saga 1.3 Standard AT", "Saga 1.3 Premium AT", "Saga 1.3 Premium S AT",
            "Persona 1.6 Standard", "Persona 1.6 Executive", "Persona 1.6 Premium",
            "Iriz 1.3 Standard", "Iriz 1.6 Executive", "Iriz 1.6 Active" }));
vehicleComboBox2.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        vehicleComboBox2ActionPerformed(evt);
    }
    });

    titlePerf2.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
    titlePerf2.setText("PERFORMANCE");

    titleModel2.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
    titleModel2.setText("SELECT CAR MODEL");

    lblEngine2.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

    lblTrans2.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

    lblPrice2.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

    lblPerf2.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

    lblChassis2.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

    btnTestDrive2.setBackground(new java.awt.Color(0, 0, 0));
    btnTestDrive2.setFont(new java.awt.Font("Century Gothic", 1, 12)); // NOI18N
    btnTestDrive2.setForeground(new java.awt.Color(255, 255, 255));
    btnTestDrive2.setText("Test Drive");
    btnTestDrive2.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnTestDrive2ActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout car2Layout = new javax.swing.GroupLayout(car2);
    car2.setLayout(car2Layout);
    car2Layout.setHorizontalGroup(
        car2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(carImageLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, car2Layout.createSequentialGroup()
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(car2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(car2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblPerf2, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblChassis2, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTrans2, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEngine2, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPrice2, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(car2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(titlePerf2)
                        .addComponent(titleChassis2)
                        .addComponent(titleTrans2)
                        .addComponent(titleEngine2)
                        .addComponent(titleModel2)
                        .addComponent(titlePrice2)
                        .addComponent(vehicleComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, car2Layout.createSequentialGroup()
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTestDrive2)
                    .addGap(113, 113, 113)))
            .addGap(42, 42, 42))
    );
    car2Layout.setVerticalGroup(
        car2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, car2Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(carImageLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(titleModel2)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(vehicleComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(titlePrice2)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(lblPrice2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(14, 14, 14)
            .addComponent(titleEngine2)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(lblEngine2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(17, 17, 17)
            .addComponent(titleTrans2)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(lblTrans2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(12, 12, 12)
            .addComponent(titleChassis2)
            .addGap(7, 7, 7)
            .addComponent(lblChassis2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(titlePerf2)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(lblPerf2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addComponent(btnTestDrive2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(23, Short.MAX_VALUE))
    );

    car1.setBackground(new java.awt.Color(255, 255, 255));

    titlePrice1.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
    titlePrice1.setText("PRICE");

    titleEngine1.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
    titleEngine1.setText("ENGINE");

    titleTrans1.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
    titleTrans1.setText("TRANSMISSIONS");

    titleChassis1.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
    titleChassis1.setText("CHASSIS");

    vehicleComboBox1.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
    vehicleComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "X50 1.5T Standard", "X50 1.5T Executive", "X50 1.5T Premium", "X50 1.5 TGDi Flagship",
        "X70 1.5 TGDi Standard", "X70 1.5 TGDi Executive", "X70 1.5 TGDi Premium", "X70 1.8 TGDi Premium X",
        "S70 1.5T Executive", "S70 1.5T Premium", "S70 1.5T Flagship",
        "Saga 1.3 Standard MT", "Saga 1.3 Standard AT", "Saga 1.3 Premium AT", "Saga 1.3 Premium S AT",
        "Persona 1.6 Standard", "Persona 1.6 Executive", "Persona 1.6 Premium",
        "Iriz 1.3 Standard", "Iriz 1.6 Executive", "Iriz 1.6 Active" }));
vehicleComboBox1.addActionListener(new java.awt.event.ActionListener() {
public void actionPerformed(java.awt.event.ActionEvent evt) {
    vehicleComboBox1ActionPerformed(evt);
    }
    });

    titlePerf1.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
    titlePerf1.setText("PERFORMANCE");

    titleModel1.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
    titleModel1.setText("SELECT CAR MODEL");

    lblEngine1.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

    lblTrans1.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

    lblPrice1.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

    lblPerf1.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

    lblChassis1.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

    btnTestDrive1.setBackground(new java.awt.Color(0, 0, 0));
    btnTestDrive1.setFont(new java.awt.Font("Century Gothic", 1, 12)); // NOI18N
    btnTestDrive1.setForeground(new java.awt.Color(255, 255, 255));
    btnTestDrive1.setText("Test Drive");
    btnTestDrive1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnTestDrive1ActionPerformed(evt);
        }
    });

    carImageLabel1.setMaximumSize(new java.awt.Dimension(150, 100));
    carImageLabel1.setMinimumSize(new java.awt.Dimension(150, 100));

    javax.swing.GroupLayout car1Layout = new javax.swing.GroupLayout(car1);
    car1.setLayout(car1Layout);
    car1Layout.setHorizontalGroup(
        car1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(car1Layout.createSequentialGroup()
            .addGroup(car1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(carImageLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(car1Layout.createSequentialGroup()
                    .addGap(46, 46, 46)
                    .addGroup(car1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(car1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblPerf1, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblChassis1, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTrans1, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblEngine1, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPrice1, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(car1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(titlePerf1)
                                .addComponent(titleChassis1)
                                .addComponent(titleTrans1)
                                .addComponent(titleEngine1)
                                .addComponent(titleModel1)
                                .addComponent(titlePrice1)
                                .addComponent(vehicleComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(car1Layout.createSequentialGroup()
                            .addGap(105, 105, 105)
                            .addComponent(btnTestDrive1)))))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    car1Layout.setVerticalGroup(
        car1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, car1Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(carImageLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(titleModel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(vehicleComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(titlePrice1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(lblPrice1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(14, 14, 14)
            .addComponent(titleEngine1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(lblEngine1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(17, 17, 17)
            .addComponent(titleTrans1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(lblTrans1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(12, 12, 12)
            .addComponent(titleChassis1)
            .addGap(7, 7, 7)
            .addComponent(lblChassis1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(titlePerf1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(lblPerf1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addComponent(btnTestDrive1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    exitButton.setBackground(new java.awt.Color(204, 51, 0));
    exitButton.setFont(new java.awt.Font("Century Gothic", 1, 12)); // NOI18N
    exitButton.setForeground(new java.awt.Color(255, 255, 255));
    exitButton.setText("Exit");
    exitButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            exitButtonActionPerformed(evt);
        }
    });

    backButton.setBackground(new java.awt.Color(0, 0, 0));
    backButton.setFont(new java.awt.Font("Century Gothic", 1, 12)); // NOI18N
    backButton.setForeground(new java.awt.Color(255, 255, 255));
    backButton.setText("Back");
    backButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            backButtonActionPerformed(evt);
        }
    });

    jLabel9.setFont(new java.awt.Font("Century Gothic", 1, 20)); // NOI18N
    jLabel9.setText("COMPARE CAR MODELS");

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
            .addContainerGap(66, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addComponent(car1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(50, 50, 50)
                    .addComponent(car2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGap(74, 74, 74))
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addGap(373, 373, 373)
            .addComponent(jLabel9)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
            .addGap(48, 48, 48)
            .addComponent(jLabel9)
            .addGap(26, 26, 26)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(car1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(car2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(29, 29, 29)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(backButton)
                .addComponent(exitButton))
            .addGap(28, 28, 28))
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

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        // Close the current frame
        this.dispose();
        // Navigate to HomeFrame or another frame
        new HomeFrame(userId, username).setVisible(true); // Pass the userId if needed
    }//GEN-LAST:event_backButtonActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_exitButtonActionPerformed

    private void vehicleComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vehicleComboBox2ActionPerformed
   if (vehicleComboBox2.getSelectedItem() != null) {
            updateDetails(vehicleComboBox2, lblPrice2, lblEngine2, lblTrans2, lblChassis2, lblPerf2, carImageLabel2);
    }
    }//GEN-LAST:event_vehicleComboBox2ActionPerformed

    private void vehicleComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vehicleComboBox1ActionPerformed
        if (vehicleComboBox1.getSelectedItem() != null) {
            updateDetails(vehicleComboBox1, lblPrice1, lblEngine1, lblTrans1, lblChassis1, lblPerf1, carImageLabel1);
        }
    }//GEN-LAST:event_vehicleComboBox1ActionPerformed

    private void btnTestDrive1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTestDrive1ActionPerformed
       String selectedCar1 = (String) vehicleComboBox1.getSelectedItem();
    this.dispose();
    if (selectedCar1 == null || selectedCar1.isEmpty()) {
        new TestDrive(userId, username).setVisible(true);  // Go to TestDrive without a car
    } else {
        new TestDrive(userId, username, selectedCar1).setVisible(true);  // Pass the selected car
    }
    }//GEN-LAST:event_btnTestDrive1ActionPerformed

    private void btnTestDrive2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTestDrive2ActionPerformed
       String selectedCar2 = (String) vehicleComboBox2.getSelectedItem();
    this.dispose();
    if (selectedCar2 == null || selectedCar2.isEmpty()) {
        new TestDrive(userId, username).setVisible(true);  // User must select a car
    } else {
        new TestDrive(userId, username, selectedCar2).setVisible(true);  // Pass selected car
    }
    }//GEN-LAST:event_btnTestDrive2ActionPerformed
    // Fetch car details from MySQL

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
            java.util.logging.Logger.getLogger(CarComparisonFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CarComparisonFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CarComparisonFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CarComparisonFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CarComparisonFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JButton btnTestDrive1;
    private javax.swing.JButton btnTestDrive2;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel car1;
    private javax.swing.JPanel car2;
    private javax.swing.JLabel carImageLabel1;
    private javax.swing.JLabel carImageLabel2;
    private javax.swing.JButton exitButton;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblChassis1;
    private javax.swing.JLabel lblChassis2;
    private javax.swing.JLabel lblEngine1;
    private javax.swing.JLabel lblEngine2;
    private javax.swing.JLabel lblPerf1;
    private javax.swing.JLabel lblPerf2;
    private javax.swing.JLabel lblPrice1;
    private javax.swing.JLabel lblPrice2;
    private javax.swing.JLabel lblTrans1;
    private javax.swing.JLabel lblTrans2;
    private javax.swing.JLabel titleChassis1;
    private javax.swing.JLabel titleChassis2;
    private javax.swing.JLabel titleEngine1;
    private javax.swing.JLabel titleEngine2;
    private javax.swing.JLabel titleModel1;
    private javax.swing.JLabel titleModel2;
    private javax.swing.JLabel titlePerf1;
    private javax.swing.JLabel titlePerf2;
    private javax.swing.JLabel titlePrice1;
    private javax.swing.JLabel titlePrice2;
    private javax.swing.JLabel titleTrans1;
    private javax.swing.JLabel titleTrans2;
    private javax.swing.JComboBox<String> vehicleComboBox1;
    private javax.swing.JComboBox<String> vehicleComboBox2;
    // End of variables declaration//GEN-END:variables

}
