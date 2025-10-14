package au.edu.rmit.cosc1295.carehome.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import au.edu.rmit.cosc1295.carehome.app.AppContext;
import au.edu.rmit.cosc1295.carehome.model.*;
import au.edu.rmit.cosc1295.carehome.service.Rules;
import au.edu.rmit.cosc1295.carehome.exceptions.ShiftViolationException;
import java.time.LocalTime;
import java.util.List;

/**
 * Controller for Shifts View.
 * 
 * Handles:
 * - Assigning shifts to staff members (Manager only)
 * - Viewing shifts for individual staff
 * - Displaying shift compliance information
 * - Visual weekly schedule grid
 * 
 * Design: Enforces business rules for shift allocation:
 * - Max 8 hours per shift
 * - Max 12 hours per day
 * - Max 48 hours per week
 * - No overlapping shifts
 */
public class ShiftsController {
    // Assign shift fields
    @FXML private ComboBox<Staff> staffBox;
    @FXML private ComboBox<DayOfWeek> dayBox;
    @FXML private TextField startField;
    @FXML private TextField endField;
    @FXML private Label statusLabel;
    
    // View shifts fields
    @FXML private ComboBox<Staff> viewStaffBox;
    @FXML private ListView<String> shiftsList;
    @FXML private Label totalShiftsLabel;
    @FXML private Label weeklyHoursLabel;
    @FXML private Label complianceLabel;
    @FXML private GridPane scheduleGrid;
    
    @FXML 
    public void initialize() {
        var ctx = AppContext.get();
        
        // Initialize combo boxes
        staffBox.getItems().setAll(ctx.staffRepo.findAll());
        dayBox.getItems().setAll(DayOfWeek.values());
        viewStaffBox.getItems().setAll(ctx.staffRepo.findAll());
        
        // Add listeners to refresh staff lists when combo boxes are opened
        staffBox.setOnShowing(event -> refreshStaffLists());
        viewStaffBox.setOnShowing(event -> refreshStaffLists());
        
        // Add listener to refresh when staff is selected
        staffBox.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv != null) {
                viewStaffBox.setValue(nv);
                refresh();
            }
        });
        
        viewStaffBox.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> refresh());
    }
    
    /**
     * Refresh staff lists in combo boxes to include newly added staff.
     */
    private void refreshStaffLists() {
        var ctx = AppContext.get();
        Staff currentStaffSelection = staffBox.getValue();
        Staff currentViewSelection = viewStaffBox.getValue();
        
        staffBox.getItems().setAll(ctx.staffRepo.findAll());
        viewStaffBox.getItems().setAll(ctx.staffRepo.findAll());
        
        // Restore previous selections if they still exist
        if (currentStaffSelection != null) {
            staffBox.setValue(currentStaffSelection);
        }
        if (currentViewSelection != null) {
            viewStaffBox.setValue(currentViewSelection);
        }
    }
    
    @FXML 
    public void onAssign() {
        var ctx = AppContext.get();
        
        // Check authorization - only managers can assign shifts
        if (ctx.getCurrentUser().getRole() != Role.MANAGER) {
            alert("Authorization Error", 
                "Only managers can assign shifts");
            return;
        }
        
        Staff s = staffBox.getValue();
        DayOfWeek d = dayBox.getValue();
        
        if (s == null || d == null) {
            statusLabel.setText("Please select staff and day");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        try {
            LocalTime start = LocalTime.parse(startField.getText().trim());
            LocalTime end = LocalTime.parse(endField.getText().trim());
            
            ctx.shiftService.assignShift(
                ctx.getCurrentUser().getId(), 
                s.getId(),
                new Shift(d, start, end)
            );
            
            statusLabel.setText("Shift assigned to " + s.getName());
            statusLabel.setStyle("-fx-text-fill: green;");
            
            // Clear fields
            startField.clear();
            endField.clear();
            
            // Refresh view
            viewStaffBox.setValue(s);
            refresh();
            
        } catch (ShiftViolationException ex) {
            alert("Shift Rule Violation", ex.getMessage());
            statusLabel.setText("Rule violation: " + ex.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        } catch (Exception ex) {
            alert("Invalid Input", "Please use HH:MM format for times (e.g., 08:00, 16:00)");
            statusLabel.setText("Invalid time format");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }
    
    @FXML 
    public void onRefresh() {
        refresh();
    }
    
    private void refresh() {
        var ctx = AppContext.get();
        Staff s = viewStaffBox.getValue();
        
        shiftsList.getItems().clear();
        
        if (s == null) {
            totalShiftsLabel.setText("0");
            weeklyHoursLabel.setText("0");
            complianceLabel.setText("-");
            scheduleGrid.getChildren().clear();
            return;
        }
        
        List<Shift> shifts = ctx.shiftService.getShifts(s.getId());
        
        // Update list
        for (Shift sh : shifts) {
            shiftsList.getItems().add(sh.toString() + " (" + sh.hours() + "h)");
        }
        
        // Calculate statistics
        long totalHours = shifts.stream().mapToLong(Shift::hours).sum();
        totalShiftsLabel.setText(String.valueOf(shifts.size()));
        weeklyHoursLabel.setText(totalHours + " / " + Rules.MAX_WEEKLY_HOURS);
        
        // Check compliance
        boolean compliant = checkCompliance(shifts);
        if (compliant) {
            complianceLabel.setText("✓ Compliant");
            complianceLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else {
            complianceLabel.setText("✗ Non-Compliant");
            complianceLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }
        
        // Build weekly schedule grid
        buildScheduleGrid(shifts);
    }
    
    /**
     * Check if shifts comply with all business rules.
     */
    private boolean checkCompliance(List<Shift> shifts) {
        // Check weekly hours
        long totalHours = shifts.stream().mapToLong(Shift::hours).sum();
        if (totalHours > Rules.MAX_WEEKLY_HOURS) {
            return false;
        }
        
        // Check daily hours for each day
        for (DayOfWeek day : DayOfWeek.values()) {
            long dailyHours = shifts.stream()
                .filter(s -> s.getDay() == day)
                .mapToLong(Shift::hours)
                .sum();
            if (dailyHours > Rules.MAX_DAILY_HOURS) {
                return false;
            }
        }
        
        // Check individual shift hours
        for (Shift shift : shifts) {
            if (shift.hours() > Rules.MAX_SHIFT_HOURS) {
                return false;
            }
        }
        
        // Check for overlaps
        for (int i = 0; i < shifts.size(); i++) {
            for (int j = i + 1; j < shifts.size(); j++) {
                if (shifts.get(i).overlaps(shifts.get(j))) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Build a visual grid showing shifts throughout the week.
     */
    private void buildScheduleGrid(List<Shift> shifts) {
        scheduleGrid.getChildren().clear();
        
        // Headers
        scheduleGrid.add(createHeaderLabel("Day"), 0, 0);
        scheduleGrid.add(createHeaderLabel("Shifts"), 1, 0);
        scheduleGrid.add(createHeaderLabel("Hours"), 2, 0);
        
        int row = 1;
        for (DayOfWeek day : DayOfWeek.values()) {
            // Day label
            Label dayLabel = new Label(day.toString());
            dayLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 60px;");
            scheduleGrid.add(dayLabel, 0, row);
            
            // Shifts for this day
            List<Shift> dayShifts = shifts.stream()
                .filter(s -> s.getDay() == day)
                .toList();
            
            if (dayShifts.isEmpty()) {
                Label noShiftLabel = new Label("No shifts");
                noShiftLabel.setStyle("-fx-text-fill: #999;");
                scheduleGrid.add(noShiftLabel, 1, row);
                scheduleGrid.add(new Label("-"), 2, row);
            } else {
                VBox shiftsBox = new VBox(3);
                long totalDayHours = 0;
                
                for (Shift shift : dayShifts) {
                    Label shiftLabel = new Label(
                        shift.getStart() + " - " + shift.getEnd()
                    );
                    shiftLabel.setStyle("-fx-font-size: 11px;");
                    shiftsBox.getChildren().add(shiftLabel);
                    totalDayHours += shift.hours();
                }
                
                scheduleGrid.add(shiftsBox, 1, row);
                
                Label hoursLabel = new Label(totalDayHours + "h");
                if (totalDayHours > Rules.MAX_DAILY_HOURS) {
                    hoursLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                }
                scheduleGrid.add(hoursLabel, 2, row);
            }
            
            row++;
        }
    }
    
    private Label createHeaderLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold; -fx-background-color: #ddd; " +
                      "-fx-padding: 5; -fx-min-width: 100px;");
        return label;
    }
    
    private void alert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
