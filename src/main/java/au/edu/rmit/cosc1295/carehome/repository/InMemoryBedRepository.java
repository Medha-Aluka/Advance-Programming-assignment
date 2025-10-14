package au.edu.rmit.cosc1295.carehome.repository;
import au.edu.rmit.cosc1295.carehome.model.Bed;
import au.edu.rmit.cosc1295.carehome.util.DatabaseManager;
import java.sql.*;

public class InMemoryBedRepository extends InMemoryRepository<Bed> {
    
    public InMemoryBedRepository() {
        loadFromDatabase();
    }
    
    private void loadFromDatabase() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM beds")) {
            
            while (rs.next()) {
                String id = rs.getString("id");
                String residentId = rs.getString("resident_id");
                
                Bed bed = new Bed(id);
                if (residentId != null) {
                    bed.assignResident(residentId);
                }
                store.put(id, bed);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void save(Bed b) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "MERGE INTO beds (id, resident_id) KEY (id) VALUES (?, ?)")) {
            
            pstmt.setString(1, b.getId());
            pstmt.setString(2, b.getResidentId());
            pstmt.executeUpdate();
            
            store.put(b.getId(), b);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
