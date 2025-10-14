package au.edu.rmit.cosc1295.carehome.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import au.edu.rmit.cosc1295.carehome.app.AppContext;
import au.edu.rmit.cosc1295.carehome.model.*;
import au.edu.rmit.cosc1295.carehome.exceptions.AuthorizationException;

/**
 * Controller for Staff View.
 * 
 * Handles:
 * - Adding new staff members (Manager only)
 * - Modifying staff credentials (Manager only)
 * - Displaying all staff in a table view
 * 
 * Design: Implements authorization checks to ensure only managers
 * can add or modify staff. Uses TableView for clean data display.
 */
public class StaffController {
    // Add new staff fields
    @FXML private TextField newStaffName;
    @FXML private ComboBox<Role> newStaffRole;
    @FXML private TextField newStaffUsername;
    @FXML private PasswordField newStaffPassword;
    
    // Modify credentials fields
    @FXML private ComboBox<Staff> staffBox;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;
    
    // Staff table
    @FXML private TableView<Staff> staffTable;
    @FXML private TableColumn<Staff, String> idColumn;
    @FXML private TableColumn<Staff, String> nameColumn;
    @FXML private TableColumn<Staff, String> roleColumn;
    @FXML private TableColumn<Staff, String> usernameColumn;
    
    @FXML 
    public void initialize() {
        // Initialize role combo box
        newStaffRole.getItems().setAll(Role.values());
        
        // Setup table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        roleColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getRole().toString()
            )
        );
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        
        // Load data
        refreshAll();
    }
    
    private void refreshAll() {
        var ctx = AppContext.get();
        var allStaff = ctx.staffRepo.findAll();
        
        // Update combo box
        staffBox.getItems().setAll(allStaff);
        
        // Update table
        staffTable.setItems(FXCollections.observableArrayList(allStaff));
    }
    
    @FXML 
    public void onAddStaff() {
        var ctx = AppContext.get();
        
        // Check authorization - only managers can add staff
        if (ctx.getCurrentUser().getRole() != Role.MANAGER) {
            showError("Authorization Error", 
                "Only managers can add new staff members");
            return;
        }
        
        // Validate input
        String name = newStaffName.getText().trim();
        Role role = newStaffRole.getValue();
        String username = newStaffUsername.getText().trim();
        String password = newStaffPassword.getText();
        
        if (name.isEmpty()) {
            statusLabel.setText("Please enter staff name");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        if (role == null) {
            statusLabel.setText("Please select role");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        if (username.isEmpty()) {
            statusLabel.setText("Please enter username");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        if (password.isEmpty()) {
            statusLabel.setText("Please enter password");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        // Check if username already exists
        for (Staff s : ctx.staffRepo.findAll()) {
            if (s.getUsername().equals(username)) {
                statusLabel.setText("Error: Username already exists");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }
        }
        
        try {
            Staff newStaff = ctx.staffService.addStaff(
                ctx.getCurrentUser().getId(),
                name,
                role,
                username,
                password
            );
            
            statusLabel.setText("Added staff: " + newStaff.getName() + 
                " [" + newStaff.getId() + "]");
            statusLabel.setStyle("-fx-text-fill: green;");
            
            // Clear fields
            newStaffName.clear();
            newStaffRole.setValue(null);
            newStaffUsername.clear();
            newStaffPassword.clear();
            
            // Refresh
            refreshAll();
            
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }
    
    @FXML 
    public void onSave() {
        var ctx = AppContext.get();
        
        // Check authorization - only managers can modify credentials
        if (ctx.getCurrentUser().getRole() != Role.MANAGER) {
            showError("Authorization Error", 
                "Only managers can modify staff credentials");
            return;
        }
        
        Staff s = staffBox.getValue();
        if (s == null) {
            statusLabel.setText("Please select staff member");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        String newUsername = usernameField.getText().trim();
        String newPassword = passwordField.getText();
        
        if (newUsername.isEmpty()) {
            statusLabel.setText("Please enter username");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        if (newPassword.isEmpty()) {
            statusLabel.setText("Please enter password");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        // Check if new username conflicts with another user
        for (Staff staff : ctx.staffRepo.findAll()) {
            if (!staff.getId().equals(s.getId()) && 
                staff.getUsername().equals(newUsername)) {
                statusLabel.setText("Error: Username already taken by another staff member");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }
        }
        
        try {
            ctx.staffService.saveOrUpdateCredentials(
                ctx.getCurrentUser().getId(), 
                s, 
                newUsername, 
                newPassword
            );
            
            statusLabel.setText("Updated credentials for " + s.getName());
            statusLabel.setStyle("-fx-text-fill: green;");
            
            // Clear fields
            usernameField.clear();
            passwordField.clear();
            
            // Refresh
            refreshAll();
            
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
