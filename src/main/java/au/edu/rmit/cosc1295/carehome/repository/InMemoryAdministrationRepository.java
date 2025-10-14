package au.edu.rmit.cosc1295.carehome.repository;
import au.edu.rmit.cosc1295.carehome.model.AdministrationRecord;
import au.edu.rmit.cosc1295.carehome.util.DatabaseManager;
import java.sql.*;
import java.time.LocalDateTime;

public class InMemoryAdministrationRepository extends InMemoryRepository<AdministrationRecord> {
    
    public InMemoryAdministrationRepository() {
        loadFromDatabase();
    }
    
    private void loadFromDatabase() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM administration_records")) {
            
            while (rs.next()) {
                String id = String.valueOf(rs.getInt("id"));
                String residentId = rs.getString("resident_id");
                String nurseId = rs.getString("nurse_id");
                String medicine = rs.getString("medicine");
                String dose = rs.getString("dose");
                Timestamp timestamp = rs.getTimestamp("timestamp");
                LocalDateTime administeredAt = timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now();
                
                AdministrationRecord record = new AdministrationRecord(residentId, nurseId, medicine, dose, administeredAt);
                store.put(id, record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void save(String id, AdministrationRecord r) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO administration_records (resident_id, nurse_id, medicine, dose, timestamp) " +
                 "VALUES (?, ?, ?, ?, ?)",
                 Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, r.getResidentId());
            pstmt.setString(2, r.getNurseId());
            pstmt.setString(3, r.getMedicine());
            pstmt.setString(4, r.getDose());
            pstmt.setTimestamp(5, Timestamp.valueOf(r.getTimestamp()));
            pstmt.executeUpdate();
            
            store.put(id, r);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
