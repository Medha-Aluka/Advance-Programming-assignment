package au.edu.rmit.cosc1295.carehome.app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import au.edu.rmit.cosc1295.carehome.model.Staff;

/**
 * Main JavaFX Application for Care Home Management System.
 * 
 * Design: Uses MVC pattern with JavaFX FXML for views.
 * Implements Singleton pattern via AppContext for shared state.
 * 
 * Features:
 * - Role-based authentication (Manager, Doctor, Nurse)
 * - Tab-based navigation for different functions
 * - Real-time audit logging of all actions
 * - Authorization checks based on user role and shift roster
 */
public class CareHomeApp extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Show login dialog first
        if (!showLoginDialog()) {
            return; // User cancelled login
        }
        
        try {
            // Load main view with tabs
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            TabPane root = loader.load();
            
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            Staff currentUser = AppContext.get().getCurrentUser();
            primaryStage.setTitle("RMIT Care Home Management System - Logged in as: " + 
                                   currentUser.getName() + " (" + currentUser.getRole() + ")");
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load application", e.getMessage());
        }
    }
    
    private boolean showLoginDialog() {
        Dialog<Staff> dialog = new Dialog<>();
        dialog.setTitle("Care Home - Login");
        dialog.setHeaderText("Please login with your credentials");
        
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        
        // Create form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        
        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);
        
        // Add hint text
        Label hintLabel = new Label("Default users:\nManager: mary/pass\nDoctor: sam/pass\nNurse: kim/pass");
        hintLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 10px;");
        grid.add(hintLabel, 0, 2, 2, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Convert result to Staff when login button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return authenticateUser(username.getText(), password.getText());
            }
            return null;
        });
        
        var result = dialog.showAndWait();
        if (result.isPresent()) {
            Staff staff = result.get();
            if (staff != null) {
                AppContext.get().setCurrentUser(staff);
                return true;
            } else {
                showError("Login Failed", "Invalid username or password");
                return showLoginDialog(); // Try again
            }
        }
        return false; // User cancelled
    }
    
    private Staff authenticateUser(String username, String password) {
        var ctx = AppContext.get();
        for (Staff staff : ctx.staffRepo.findAll()) {
            if (staff.getUsername().equals(username) && 
                staff.getPasswordHash().equals(password)) {
                return staff;
            }
        }
        return null;
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

