package au.edu.rmit.cosc1295.carehome.controller;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

/**
 * Controller for Admin Log View.
 * 
 * Displays the audit log of all actions performed in the system.
 * Each action is logged with timestamp, staff ID, action type, and details.
 * 
 * This satisfies the regulatory requirement that all actions must be
 * logged for audit purposes.
 */
public class AdminLogController {
    @FXML private TextArea logArea;
    
    @FXML 
    public void initialize() {
        // Auto-load log on initialization
        onRefresh();
    }
    
    @FXML 
    public void onRefresh() {
        try {
            var p = Paths.get("actions.log");
            logArea.setText(Files.exists(p) ? Files.readString(p, StandardCharsets.UTF_8) : "(No log yet)");
        } catch (Exception ex) { 
            logArea.setText("Error reading log: " + ex.getMessage()); 
        }
    }
}
