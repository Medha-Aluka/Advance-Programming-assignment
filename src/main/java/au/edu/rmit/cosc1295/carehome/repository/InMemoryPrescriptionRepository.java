package au.edu.rmit.cosc1295.carehome.repository;
import au.edu.rmit.cosc1295.carehome.model.Prescription;
import au.edu.rmit.cosc1295.carehome.util.DatabaseManager;
import java.sql.*;
import java.time.LocalDateTime;

public class InMemoryPrescriptionRepository extends InMemoryRepository<Prescription> {
    
    public InMemoryPrescriptionRepository() {
        loadFromDatabase();
    }
    
    private void loadFromDatabase() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM prescriptions")) {
            
            while (rs.next()) {
                String id = String.valueOf(rs.getInt("id"));
                String residentId = rs.getString("resident_id");
                String doctorId = rs.getString("doctor_id");
                String medicine = rs.getString("medicine");
                String dose = rs.getString("dose");
                String schedule = rs.getString("schedule_info");
                Timestamp timestamp = rs.getTimestamp("created_at");
                LocalDateTime createdAt = timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now();
                
                Prescription prescription = new Prescription(id, residentId, doctorId, medicine, dose, schedule, createdAt);
                store.put(id, prescription);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void save(Prescription p) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO prescriptions (resident_id, doctor_id, medicine, dose, schedule_info, created_at) " +
                 "VALUES (?, ?, ?, ?, ?, ?)",
                 Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, p.getResidentId());
            pstmt.setString(2, p.getDoctorId());
            pstmt.setString(3, p.getMedicine());
            pstmt.setString(4, p.getDose());
            pstmt.setString(5, p.getSchedule());
            pstmt.setTimestamp(6, Timestamp.valueOf(p.getCreatedAt()));
            pstmt.executeUpdate();
            
            store.put(p.getId(), p);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
