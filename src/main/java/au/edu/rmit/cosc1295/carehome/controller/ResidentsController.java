package au.edu.rmit.cosc1295.carehome.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import au.edu.rmit.cosc1295.carehome.app.AppContext;
import au.edu.rmit.cosc1295.carehome.model.*;
import au.edu.rmit.cosc1295.carehome.exceptions.BedOccupiedException;

/**
 * Controller for Residents View.
 * 
 * Handles:
 * - Adding new residents to the system
 * - Assigning residents to beds
 * - Moving residents between beds
 * - Visual display of ward/room/bed layout with color-coded gender indicators
 * 
 * Design: Uses observer pattern through JavaFX property bindings.
 * Updates UI in real-time when beds are occupied/vacated.
 */
public class ResidentsController {
    @FXML private TextField newResidentName;
    @FXML private ComboBox<Gender> newResidentGender;
    @FXML private ComboBox<Resident> residentBox;
    @FXML private ComboBox<Bed> bedBox;
    @FXML private Label statusLabel;
    
    // Ward/Room grids
    @FXML private GridPane ward1Room1Grid;
    @FXML private GridPane ward1Room2Grid;
    @FXML private GridPane ward1Room3Grid;
    @FXML private GridPane ward2Room1Grid;
    @FXML private GridPane ward2Room2Grid;
    
    @FXML 
    public void initialize() {
        // Initialize gender combo box
        newResidentGender.getItems().setAll(Gender.values());
        
        // Load initial data
        refreshAll();
    }
    
    private void refreshAll() {
        var ctx = AppContext.get();
        residentBox.getItems().setAll(ctx.residentRepo.findAll());
        bedBox.getItems().setAll(ctx.bedRepo.findAll());
        
        // Refresh bed visualization
        refreshBedLayout();
    }
    
    /**
     * Refresh the visual bed layout showing occupancy and gender.
     */
    private void refreshBedLayout() {
        var ctx = AppContext.get();
        
        // Clear all grids
        ward1Room1Grid.getChildren().clear();
        ward1Room2Grid.getChildren().clear();
        ward1Room3Grid.getChildren().clear();
        ward2Room1Grid.getChildren().clear();
        ward2Room2Grid.getChildren().clear();
        
        // Populate beds
        for (Bed bed : ctx.bedRepo.findAll()) {
            VBox bedBox = createBedVisual(bed);
            addBedToGrid(bed.getId(), bedBox);
        }
    }
    
    /**
     * Create a visual representation of a bed with occupant info.
     */
    private VBox createBedVisual(Bed bed) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setMinSize(120, 100);
        box.setMaxSize(120, 100);
        box.setStyle("-fx-border-color: #666; -fx-border-width: 1; -fx-padding: 5; -fx-background-radius: 5;");
        
        // Bed ID label
        Label bedIdLabel = new Label(bed.getId());
        bedIdLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
        
        if (bed.isVacant()) {
            // Vacant bed
            box.setStyle(box.getStyle() + "-fx-background-color: #e0e0e0;");
            Label vacantLabel = new Label("VACANT");
            vacantLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
            box.getChildren().addAll(bedIdLabel, vacantLabel);
        } else {
            // Occupied bed
            var ctx = AppContext.get();
            var residentOpt = ctx.residentRepo.findById(bed.getResidentId());
            if (residentOpt.isPresent()) {
                Resident resident = residentOpt.get();
                
                // Color code by gender: Blue for Male, Red for Female
                String bgColor = switch (resident.getGender()) {
                    case MALE -> "#b3d9ff"; // Light blue
                    case FEMALE -> "#ffb3b3"; // Light red
                    case OTHER -> "#d9b3ff"; // Light purple
                };
                box.setStyle(box.getStyle() + "-fx-background-color: " + bgColor + ";");
                
                Label residentLabel = new Label(resident.getName());
                residentLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
                residentLabel.setWrapText(true);
                
                Label idLabel = new Label("[" + resident.getId() + "]");
                idLabel.setStyle("-fx-font-size: 9px;");
                
                Label genderLabel = new Label(resident.getGender().toString());
                genderLabel.setStyle("-fx-font-size: 9px; -fx-font-style: italic;");
                
                box.getChildren().addAll(bedIdLabel, residentLabel, idLabel, genderLabel);
                
                // Make clickable to show details
                box.setOnMouseClicked(e -> showResidentDetails(resident, bed));
                box.setStyle(box.getStyle() + "-fx-cursor: hand;");
            }
        }
        
        return box;
    }
    
    /**
     * Add a bed visual to the appropriate grid based on ward/room/bed ID.
     */
    private void addBedToGrid(String bedId, VBox bedBox) {
        // Parse bed ID format: W#-R#-B#
        String[] parts = bedId.split("-");
        if (parts.length != 3) return;
        
        int ward = Integer.parseInt(parts[0].substring(1));
        int room = Integer.parseInt(parts[1].substring(1));
        int bedNum = Integer.parseInt(parts[2].substring(1));
        
        GridPane targetGrid = null;
        if (ward == 1) {
            targetGrid = switch (room) {
                case 1 -> ward1Room1Grid;
                case 2 -> ward1Room2Grid;
                case 3 -> ward1Room3Grid;
                default -> null;
            };
        } else if (ward == 2) {
            targetGrid = switch (room) {
                case 1 -> ward2Room1Grid;
                case 2 -> ward2Room2Grid;
                default -> null;
            };
        }
        
        if (targetGrid != null) {
            // Add to grid (arrange beds in a row)
            targetGrid.add(bedBox, bedNum - 1, 0);
        }
    }
    
    /**
     * Show detailed information about a resident.
     */
    private void showResidentDetails(Resident resident, Bed bed) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Resident Details");
        alert.setHeaderText(resident.getName() + " [" + resident.getId() + "]");
        alert.setContentText(
            "Gender: " + resident.getGender() + "\n" +
            "Bed: " + bed.getId() + "\n" +
            "\nClick 'Move to Bed' above to relocate this resident."
        );
        alert.showAndWait();
    }
    
    @FXML 
    public void onAddResident() {
        var ctx = AppContext.get();
        String name = newResidentName.getText().trim();
        Gender gender = newResidentGender.getValue();
        
        if (name.isEmpty()) {
            statusLabel.setText("Please enter resident name");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        if (gender == null) {
            statusLabel.setText("Please select gender");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        try {
            Resident r = ctx.residentService.addResident(
                ctx.getCurrentUser().getId(), 
                name, 
                gender
            );
            statusLabel.setText("Added resident: " + r.getName() + " [" + r.getId() + "]");
            statusLabel.setStyle("-fx-text-fill: green;");
            
            // Clear fields
            newResidentName.clear();
            newResidentGender.setValue(null);
            
            // Refresh
            refreshAll();
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }
    
    @FXML 
    public void onAssign() {
        var ctx = AppContext.get();
        var r = residentBox.getValue();
        var b = bedBox.getValue();
        
        if (r == null || b == null) {
            statusLabel.setText("Please select both resident and bed");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        try {
            ctx.residentService.assignResidentToBed(
                ctx.getCurrentUser().getId(), 
                r.getId(), 
                b.getId()
            );
            statusLabel.setText("Assigned " + r.getName() + " to " + b.getId());
            statusLabel.setStyle("-fx-text-fill: green;");
            refreshAll();
        } catch (BedOccupiedException ex) {
            statusLabel.setText("Error: Bed is already occupied!");
            statusLabel.setStyle("-fx-text-fill: red;");
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }
    
    @FXML 
    public void onMove() {
        var ctx = AppContext.get();
        var r = residentBox.getValue();
        var b = bedBox.getValue();
        
        if (r == null || b == null) {
            statusLabel.setText("Please select both resident and bed");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        try {
            ctx.residentService.moveResident(
                ctx.getCurrentUser().getId(), 
                r.getId(), 
                b.getId()
            );
            statusLabel.setText("Moved " + r.getName() + " to " + b.getId());
            statusLabel.setStyle("-fx-text-fill: green;");
            refreshAll();
        } catch (BedOccupiedException ex) {
            statusLabel.setText("Error: Target bed is already occupied!");
            statusLabel.setStyle("-fx-text-fill: red;");
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
