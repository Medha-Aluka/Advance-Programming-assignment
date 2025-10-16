package au.edu.rmit.cosc1295.carehome.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import au.edu.rmit.cosc1295.carehome.app.AppContext;
import au.edu.rmit.cosc1295.carehome.model.*;
import au.edu.rmit.cosc1295.carehome.exceptions.AuthorizationException;
import java.time.format.DateTimeFormatter;

/**
 * Controller for Prescriptions View.
 * 
 * Handles:
 * - Doctors adding prescriptions (with authorization and roster check)
 * - Nurses administering medications (with authorization and roster check)
 * - Viewing prescriptions and administration records for residents
 * 
 * Design: Enforces regulatory compliance by checking:
 * - User role (DOCTOR for prescriptions, NURSE for administration)
 * - User is rostered (on duty) at time of action
 * All actions are logged with timestamp and staff ID.
 */
public class PrescriptionsController {
    // Doctor: Add prescription
    @FXML private ComboBox<Resident> residentBox;
    @FXML private TextField medicineField;
    @FXML private TextField doseField;
    @FXML private TextField scheduleField;
    
    // Nurse: Administer dose
    @FXML private ComboBox<Resident> residentBox2;
    @FXML private TextField medicineField2;
    @FXML private TextField doseField2;
    @FXML private TextField prescriptionTimeField;
    
    @FXML private Label statusLabel;
    
    // View prescriptions
    @FXML private ComboBox<Resident> viewResidentBox;
    @FXML private TableView<Prescription> prescriptionsTable;
    @FXML private TableColumn<Prescription, String> medicineColumn;
    @FXML private TableColumn<Prescription, String> doseColumn;
    @FXML private TableColumn<Prescription, String> scheduleColumn;
    @FXML private TableColumn<Prescription, String> doctorColumn;
    @FXML private TableColumn<Prescription, String> dateColumn;
    
    @FXML private TableView<AdministrationRecord> adminTable;
    @FXML private TableColumn<AdministrationRecord, String> adminMedicineColumn;
    @FXML private TableColumn<AdministrationRecord, String> adminDoseColumn;
    @FXML private TableColumn<AdministrationRecord, String> adminPrescriptionTimeColumn;
    @FXML private TableColumn<AdministrationRecord, String> nurseColumn;
    @FXML private TableColumn<AdministrationRecord, String> adminDateColumn;
    
    private static final DateTimeFormatter DATE_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    @FXML 
    public void initialize() {
        var ctx = AppContext.get();
        var residents = ctx.residentRepo.findAll();
        
        residentBox.getItems().setAll(residents);
        residentBox2.getItems().setAll(residents);
        viewResidentBox.getItems().setAll(residents);
        
        // Add listeners to refresh resident lists when combo boxes are opened
        residentBox.setOnShowing(event -> refreshResidentLists());
        residentBox2.setOnShowing(event -> refreshResidentLists());
        viewResidentBox.setOnShowing(event -> refreshResidentLists());
        
        // Setup prescription table columns
        medicineColumn.setCellValueFactory(new PropertyValueFactory<>("medicine"));
        doseColumn.setCellValueFactory(new PropertyValueFactory<>("dose"));
        scheduleColumn.setCellValueFactory(new PropertyValueFactory<>("schedule"));
        doctorColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDoctorId())
        );
        dateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCreatedAt().format(DATE_FORMAT)
            )
        );
        
        // Setup administration table columns
        adminMedicineColumn.setCellValueFactory(new PropertyValueFactory<>("medicine"));
        adminDoseColumn.setCellValueFactory(new PropertyValueFactory<>("dose"));
        adminPrescriptionTimeColumn.setCellValueFactory(new PropertyValueFactory<>("prescriptionTime"));
        nurseColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNurseId())
        );
        adminDateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getTimestamp().format(DATE_FORMAT)
            )
        );
        
        // Add listener to refresh when resident is selected
        viewResidentBox.getSelectionModel().selectedItemProperty()
            .addListener((o, ov, nv) -> refreshPrescriptions());
    }
    
    /**
     * Refresh resident lists in combo boxes to include newly added residents.
     */
    private void refreshResidentLists() {
        var ctx = AppContext.get();
        Resident currentSelection1 = residentBox.getValue();
        Resident currentSelection2 = residentBox2.getValue();
        Resident currentViewSelection = viewResidentBox.getValue();
        
        var residents = ctx.residentRepo.findAll();
        residentBox.getItems().setAll(residents);
        residentBox2.getItems().setAll(residents);
        viewResidentBox.getItems().setAll(residents);
        
        // Restore previous selections if they still exist
        if (currentSelection1 != null) {
            residentBox.setValue(currentSelection1);
        }
        if (currentSelection2 != null) {
            residentBox2.setValue(currentSelection2);
        }
        if (currentViewSelection != null) {
            viewResidentBox.setValue(currentViewSelection);
        }
    }
    
    @FXML 
    public void onAddPrescription() {
        var ctx = AppContext.get();
        var currentUser = ctx.getCurrentUser();
        
        Resident r = residentBox.getValue();
        if (r == null) {
            showStatus("Please select a resident", "red");
            return;
        }
        
        String medicine = medicineField.getText().trim();
        String dose = doseField.getText().trim();
        String schedule = scheduleField.getText().trim();
        
        if (medicine.isEmpty() || dose.isEmpty() || schedule.isEmpty()) {
            showStatus("Please fill in all fields", "red");
            return;
        }
        
        try {
            // Check authorization (role + roster)
            ctx.authorizationService.checkAuthorization(currentUser, Role.DOCTOR);
            
            // Add prescription
            ctx.prescriptionService.add(
                currentUser.getId(), 
                currentUser, 
                r.getId(),
                medicine, 
                dose, 
                schedule
            );
            
            showStatus("Prescription added for " + r.getName(), "green");
            
            // Clear fields
            medicineField.clear();
            doseField.clear();
            scheduleField.clear();
            
            // Refresh view
            viewResidentBox.setValue(r);
            refreshPrescriptions();
            
        } catch (AuthorizationException e) {
            showError("Authorization Error", e.getMessage());
            showStatus("Authorization failed: " + e.getMessage(), "red");
        } catch (Exception e) {
            showError("Error", e.getMessage());
            showStatus("Error: " + e.getMessage(), "red");
        }
    }
    
    @FXML 
    public void onAdminister() {
        var ctx = AppContext.get();
        var currentUser = ctx.getCurrentUser();
        
        Resident r = residentBox2.getValue();
        if (r == null) {
            showStatus("Please select a resident", "red");
            return;
        }
        
        String medicine = medicineField2.getText().trim();
        String dose = doseField2.getText().trim();
        String prescriptionTime = prescriptionTimeField.getText().trim();
        
        if (medicine.isEmpty() || dose.isEmpty()) {
            showStatus("Please fill in medicine and dose fields", "red");
            return;
        }
        
        try {
            // Check authorization (role + roster)
            ctx.authorizationService.checkAuthorization(currentUser, Role.NURSE);
            
            // Administer dose
            ctx.administrationService.administerDose(
                currentUser.getId(), 
                currentUser, 
                r.getId(),
                medicine, 
                dose,
                prescriptionTime.isEmpty() ? null : prescriptionTime
            );
            
            showStatus("Medication administered to " + r.getName(), "green");
            
            // Clear fields
            medicineField2.clear();
            doseField2.clear();
            prescriptionTimeField.clear();
            
            // Refresh view
            viewResidentBox.setValue(r);
            refreshPrescriptions();
            
        } catch (AuthorizationException e) {
            showError("Authorization Error", e.getMessage());
            showStatus("Authorization failed: " + e.getMessage(), "red");
        } catch (Exception e) {
            showError("Error", e.getMessage());
            showStatus("Error: " + e.getMessage(), "red");
        }
    }
    
    @FXML 
    public void onRefreshPrescriptions() {
        refreshPrescriptions();
    }
    
    private void refreshPrescriptions() {
        Resident r = viewResidentBox.getValue();
        
        if (r == null) {
            prescriptionsTable.getItems().clear();
            adminTable.getItems().clear();
            return;
        }
        
        var ctx = AppContext.get();
        
        // Get prescriptions for this resident
        ObservableList<Prescription> prescriptions = FXCollections.observableArrayList();
        for (Prescription p : ctx.prescriptionRepo.findAll()) {
            if (p.getResidentId().equals(r.getId())) {
                prescriptions.add(p);
            }
        }
        prescriptionsTable.setItems(prescriptions);
        
        // Get administration records for this resident
        ObservableList<AdministrationRecord> adminRecords = FXCollections.observableArrayList();
        for (AdministrationRecord rec : ctx.adminRepo.findAll()) {
            if (rec.getResidentId().equals(r.getId())) {
                adminRecords.add(rec);
            }
        }
        adminTable.setItems(adminRecords);
    }
    
    private void showStatus(String message, String color) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + color + ";");
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
