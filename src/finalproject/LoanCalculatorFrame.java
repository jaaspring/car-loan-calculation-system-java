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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

/**
 *
 * @author Ezzah
 */
public class LoanCalculatorFrame extends javax.swing.JFrame {
    private String userId;
    private String username;
    
    private HashMap<String, HashMap<String, HashMap<String, Double>>> PRICING;
    
    public LoanCalculatorFrame() {
        initComponents();
        PRICING = new HashMap<>();
        loadDataFromDatabase();
        setLocationRelativeTo(null); // Center the frame
         
    }
    public LoanCalculatorFrame(String userId, String username) {
        this.userId = userId;
        this.username = username;
        PRICING = new HashMap<>();
        initComponents();  // Initialize components first
        loadDataFromDatabase(); // Load data from DB after components are initialized
        setLocationRelativeTo(null); // Center the frame

    }

private void loadDataFromDatabase() {
    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/loan_calculator", "root", "");
         PreparedStatement pst = con.prepareStatement("SELECT model, variant, price, paint_type FROM car_details");
         ResultSet rs = pst.executeQuery()) {

        // Clear previous data
        PRICING.clear();
        System.out.println("Loading data from database...");

        // Print debug info to see what is being fetched
        while (rs.next()) {
            String model = rs.getString("model");
            String variant = rs.getString("variant");
            double price = Double.parseDouble(rs.getString("price").replace("RM", "").replace(",", "").trim());
            String paintType = rs.getString("paint_type");

            System.out.println("Model: " + model + ", Variant: " + variant + ", Price: " + price + ", Paint Type: " + paintType);

            // Initialize the map for the vehicle model if it doesn't exist
            if (!PRICING.containsKey(model)) {
                PRICING.put(model, new HashMap<>());
            }

            // Initialize the map for the vehicle variant if it doesn't exist
            if (!PRICING.get(model).containsKey(variant)) {
                PRICING.get(model).put(variant, new HashMap<>());
            }

            // Store the price for the vehicle variant and paint type
            PRICING.get(model).get(variant).put(paintType, price);
        }

        // After loading data, populate the combo boxes
        updateComboBoxes();

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading vehicle data: " + e.getMessage());
        e.printStackTrace();
    }
}

private void updateComboBoxes() {
    // Populate vehicleComboBox with vehicle models
    vehicleComboBox.removeAllItems();
    for (String model : PRICING.keySet()) {
        vehicleComboBox.addItem(model);
    }
    System.out.println("Vehicle models loaded: " + PRICING.keySet());

    // Call updateVariants to populate variantComboBox based on the first model selection
    updateVariants();
}
private void updateVariants() {
    String selectedModel = (String) vehicleComboBox.getSelectedItem();
    if (selectedModel != null && PRICING.containsKey(selectedModel)) {
        variantComboBox.removeAllItems();  // Clear previous variants

        HashMap<String, HashMap<String, Double>> variants = PRICING.get(selectedModel);
        
        // Use a set to avoid duplicates
        HashSet<String> variantSet = new HashSet<>(variants.keySet());

        // Add variants to combo box (avoid duplicates)
        for (String variant : variantSet) {
            variantComboBox.addItem(variant);
        }

        // Once variants are loaded, update paint types
        updatePaintTypes();
    }
}

private void updatePaintTypes() {
    String selectedModel = (String) vehicleComboBox.getSelectedItem();
    String selectedVariant = (String) variantComboBox.getSelectedItem();

    if (selectedModel != null && selectedVariant != null &&
        PRICING.containsKey(selectedModel) && PRICING.get(selectedModel).containsKey(selectedVariant)) {

        paintTypeComboBox.removeAllItems();  // Clear previous paint types

        // Get paint types for the selected variant
        HashMap<String, Double> paintTypes = PRICING.get(selectedModel).get(selectedVariant);

        // Debugging: print the paint types
        System.out.println("Available Paint Types for " + selectedModel + " " + selectedVariant + ": " + paintTypes.keySet());

        // Add all available paint types to the combo box
        for (String paintType : paintTypes.keySet()) {
            paintTypeComboBox.addItem(paintType);
        }
    }
}



 private void calculateLoan() {
    try {
        // Get values from UI components
        String vehicle = (String) vehicleComboBox.getSelectedItem();
        String variant = (String) variantComboBox.getSelectedItem();
        String paintType = (String) paintTypeComboBox.getSelectedItem();

        // Check if down payment field is empty
        if (downPaymentTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Down Payment is required!");
            return;
        }

        // Check if interest rate field is empty
        if (interestRateTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Interest Rate is required!");
            return;
        }

        // Parse the down payment and interest rate
        double downPayment = Double.parseDouble(downPaymentTextField.getText().trim());
        double interestRate = Double.parseDouble(interestRateTextField.getText().trim());

        // Check if the vehicle is in the pricing map
        if (!PRICING.containsKey(vehicle)) {
            JOptionPane.showMessageDialog(this, "Vehicle not found in pricing data!");
            return;
        }

        // Get the vehicle pricing data
        HashMap<String, HashMap<String, Double>> vehiclePricing = PRICING.get(vehicle);
        if (!vehiclePricing.containsKey(variant)) {
            JOptionPane.showMessageDialog(this, "Variant not found for selected vehicle!");
            return;
        }

        // Get the pricing for the selected variant
        HashMap<String, Double> variantPricing = vehiclePricing.get(variant);
        if (!variantPricing.containsKey(paintType)) {
            JOptionPane.showMessageDialog(this, "Paint type not found for selected variant!");
            return;
        }

        // Get the price for the selected variant and paint type
        double price = variantPricing.get(paintType);
        double loanAmount = price - downPayment;

        // Check if the down payment exceeds or matches the vehicle price
        if (loanAmount <= 0) {
            JOptionPane.showMessageDialog(this, "Down Payment exceeds or matches the vehicle price!");
            return;
        }

        // Calculate the monthly installment
        double monthlyRate = interestRate / 100 / 12;
        int months = Integer.parseInt(termsComboBox.getSelectedItem().toString()) * 12;
        double monthlyInstallment = (loanAmount * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -months));

        // Display the result
        monthlyInstallmentLabel1.setText(String.format("RM %.2f", monthlyInstallment));
    } catch (NumberFormatException e) {
        // Handle invalid number format in fields
        JOptionPane.showMessageDialog(this, "Invalid input! Please ensure all fields are filled with valid numbers.");
    } catch (NullPointerException e) {
        // Handle case where combo box selection is null
        JOptionPane.showMessageDialog(this, "Please select a valid option from all fields.");
    } catch (RuntimeException e) {
        // Handle any other runtime exceptions that may occur
        JOptionPane.showMessageDialog(this, "Unexpected error: " + e.getMessage());
    } catch (Exception e) {
        // Handle any other general exceptions
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel4 = new Styles.GradientPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel2 = new Styles.RoundedBevelPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        monthlyInstallmentLabel1 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        variantComboBox = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        vehicleComboBox = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        paintTypeComboBox = new javax.swing.JComboBox<>();
        termsComboBox = new javax.swing.JComboBox<>();
        downPaymentTextField = new javax.swing.JTextField();
        interestRateTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        backButton = Styles.createRoundedButton("Back");
        exitButton = Styles.createRoundedButton("Exit");
        historyButton = Styles.createRoundedButton("History");
        resetButton = Styles.createRoundedButton("Reset");
        CalculateButton = Styles.createRoundedButton("Calculate");
        confirmButton = Styles.createRoundedButton("Save");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Loan Calculation System");
        setBackground(java.awt.Color.orange);

        jLabel9.setFont(new java.awt.Font("Century Gothic", 1, 16)); // NOI18N
        jLabel9.setText("SO YOU’VE GOT YOUR EYES SET ON A PROTON ");

        jLabel10.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel10.setText("Use our loan calculator to help plan your finances in owning a Proton Car.");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel2.setText("Variant");

        jPanel3.setBackground(new java.awt.Color(0, 0, 0));

        monthlyInstallmentLabel1.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        monthlyInstallmentLabel1.setForeground(new java.awt.Color(255, 255, 255));

        jLabel8.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Monthly Installment:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(0, 117, Short.MAX_VALUE))
                    .addComponent(monthlyInstallmentLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(monthlyInstallmentLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel3.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel3.setText("Paint");

        jLabel4.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel4.setText("Terms");

        variantComboBox.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        variantComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        variantComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                variantComboBoxActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel5.setText("Down Payment");

        vehicleComboBox.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        vehicleComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        vehicleComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vehicleComboBoxActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel6.setText("Interest Rate");

        paintTypeComboBox.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        paintTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        paintTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paintTypeComboBoxActionPerformed(evt);
            }
        });

        termsComboBox.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        termsComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));

        downPaymentTextField.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        downPaymentTextField.setToolTipText("");

        interestRateTextField.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel1.setText("Vehicle");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(46, 46, 46))
                    .addComponent(vehicleComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(variantComboBox, 0, 267, Short.MAX_VALUE)
                    .addComponent(paintTypeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(termsComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(downPaymentTextField)
                    .addComponent(interestRateTextField))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vehicleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(variantComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paintTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(termsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel5)
                .addGap(7, 7, 7)
                .addComponent(downPaymentTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addGap(9, 9, 9)
                .addComponent(interestRateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        backButton.setBackground(new java.awt.Color(0, 0, 0));
        backButton.setFont(new java.awt.Font("Century Gothic", 1, 12)); // NOI18N
        backButton.setForeground(new java.awt.Color(255, 255, 255));
        backButton.setText("Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        exitButton.setBackground(new java.awt.Color(204, 51, 0));
        exitButton.setFont(new java.awt.Font("Century Gothic", 1, 12)); // NOI18N
        exitButton.setForeground(new java.awt.Color(255, 255, 255));
        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        historyButton.setBackground(new java.awt.Color(0, 0, 0));
        historyButton.setFont(new java.awt.Font("Century Gothic", 1, 12)); // NOI18N
        historyButton.setForeground(new java.awt.Color(255, 255, 255));
        historyButton.setText("History");
        historyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                historyButtonActionPerformed(evt);
            }
        });

        resetButton.setBackground(new java.awt.Color(0, 0, 0));
        resetButton.setFont(new java.awt.Font("Century Gothic", 1, 12)); // NOI18N
        resetButton.setForeground(new java.awt.Color(255, 255, 255));
        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        CalculateButton.setBackground(new java.awt.Color(0, 0, 0));
        CalculateButton.setFont(new java.awt.Font("Century Gothic", 1, 12)); // NOI18N
        CalculateButton.setForeground(new java.awt.Color(255, 255, 255));
        CalculateButton.setText("Calculate");
        CalculateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CalculateButtonActionPerformed(evt);
            }
        });

        confirmButton.setBackground(new java.awt.Color(0, 0, 0));
        confirmButton.setFont(new java.awt.Font("Century Gothic", 1, 12)); // NOI18N
        confirmButton.setForeground(new java.awt.Color(255, 255, 255));
        confirmButton.setText("Save");
        confirmButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(298, 298, 298)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE))
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(34, 34, 34)
                    .addComponent(resetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(historyButton, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(CalculateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(confirmButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(137, 137, 137)
                    .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(45, 45, 45)))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(281, 281, 281)
                        .addComponent(jLabel9))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(202, 202, 202)
                        .addComponent(jLabel10)))
                .addGap(231, 231, 231))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addGap(34, 34, 34)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CalculateButton)
                    .addComponent(confirmButton)
                    .addComponent(backButton)
                    .addComponent(exitButton)
                    .addComponent(historyButton)
                    .addComponent(resetButton))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void confirmButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmButtonActionPerformed
                                                  
    if (monthlyInstallmentLabel1.getText().isEmpty() || !monthlyInstallmentLabel1.getText().contains("RM")) {
        JOptionPane.showMessageDialog(this, "Please calculate the loan before confirming.");
        return;
    }

    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/loan_calculator", "root", "");
         PreparedStatement pst = con.prepareStatement("INSERT INTO loan_history (user_id, vehicle, variant, paint_type, terms, down_payment, interest_rate, monthly_installment) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

        String vehicle = vehicleComboBox.getSelectedItem().toString();
        String variant = variantComboBox.getSelectedItem().toString();
        String paintType = paintTypeComboBox.getSelectedItem().toString();
        int terms = Integer.parseInt(termsComboBox.getSelectedItem().toString());
        double downPayment = Double.parseDouble(downPaymentTextField.getText().trim());
        double interestRate = Double.parseDouble(interestRateTextField.getText().trim());
        double monthlyInstallment = Double.parseDouble(monthlyInstallmentLabel1.getText().replace("RM ", ""));

        pst.setString(1, userId);
        pst.setString(2, vehicle);
        pst.setString(3, variant);
        pst.setString(4, paintType); // Ensure paint type is being correctly inserted
        pst.setInt(5, terms);
        pst.setDouble(6, downPayment);
        pst.setDouble(7, interestRate);
        pst.setDouble(8, monthlyInstallment);

        pst.executeUpdate();
        JOptionPane.showMessageDialog(this, "Loan calculation saved successfully!");
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error saving to database: " + e.getMessage());
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Unexpected error: " + e.getMessage());
    }


    }//GEN-LAST:event_confirmButtonActionPerformed

    private void CalculateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CalculateButtonActionPerformed
       calculateLoan(); // TODO add your handling code here:
    }//GEN-LAST:event_CalculateButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
         // Reset combo boxes to default selections
        vehicleComboBox.setSelectedIndex(0);
        //updateVariants(); // Reset variants based on the default vehicle
        paintTypeComboBox.setSelectedIndex(0);
        termsComboBox.setSelectedIndex(0);

        // Clear text fields
        downPaymentTextField.setText("");
        interestRateTextField.setText("");

        // Reset the monthly installment label
        monthlyInstallmentLabel1.setText("RM 0.00");
    
    JOptionPane.showMessageDialog(this, "Fields reset successfully!");
    }//GEN-LAST:event_resetButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
          // Close the current frame
          this.dispose();

          // Navigate to HomeFrame or another frame
          new HomeFrame(userId, username).setVisible(true); // Pass the userId if needed
    }//GEN-LAST:event_backButtonActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0); // Close the application
        }
    }//GEN-LAST:event_exitButtonActionPerformed

    private void historyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_historyButtonActionPerformed
       this.dispose();

          // Navigate to HomeFrame or another frame
          new HistoryFrame(userId, username).setVisible(true); 
    }//GEN-LAST:event_historyButtonActionPerformed

    private void vehicleComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vehicleComboBoxActionPerformed
          updateVariants();
    }//GEN-LAST:event_vehicleComboBoxActionPerformed

    private void variantComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_variantComboBoxActionPerformed
           updatePaintTypes();
    }//GEN-LAST:event_variantComboBoxActionPerformed

    private void paintTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paintTypeComboBoxActionPerformed
  
    }//GEN-LAST:event_paintTypeComboBoxActionPerformed

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
            java.util.logging.Logger.getLogger(LoanCalculatorFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoanCalculatorFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoanCalculatorFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoanCalculatorFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoanCalculatorFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CalculateButton;
    private javax.swing.JButton backButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton confirmButton;
    private javax.swing.JTextField downPaymentTextField;
    private javax.swing.JButton exitButton;
    private javax.swing.JButton historyButton;
    private javax.swing.JTextField interestRateTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel monthlyInstallmentLabel1;
    private javax.swing.JComboBox<String> paintTypeComboBox;
    private javax.swing.JButton resetButton;
    private javax.swing.JComboBox<String> termsComboBox;
    private javax.swing.JComboBox<String> variantComboBox;
    private javax.swing.JComboBox<String> vehicleComboBox;
    // End of variables declaration//GEN-END:variables

    
}
