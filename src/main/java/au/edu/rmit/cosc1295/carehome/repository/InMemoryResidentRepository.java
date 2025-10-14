package au.edu.rmit.cosc1295.carehome.repository;
import au.edu.rmit.cosc1295.carehome.model.*;
import au.edu.rmit.cosc1295.carehome.util.DatabaseManager;
import java.sql.*;

public class InMemoryResidentRepository extends InMemoryRepository<Resident> {
    
    public InMemoryResidentRepository() {
        loadFromDatabase();
    }
    
    private void loadFromDatabase() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM residents")) {
            
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                Gender gender = Gender.valueOf(rs.getString("gender"));
                String bedId = rs.getString("bed_id");
                
                Resident resident = new Resident(id, name, gender);
                if (bedId != null) {
                    resident.setBedId(bedId);
                }
                store.put(id, resident);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void save(Resident r) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "MERGE INTO residents (id, name, gender, bed_id) " +
                 "KEY (id) VALUES (?, ?, ?, ?)")) {
            
            pstmt.setString(1, r.getId());
            pstmt.setString(2, r.getName());
            pstmt.setString(3, r.getGender().toString());
            pstmt.setString(4, r.getBedId());
            pstmt.executeUpdate();
            
            store.put(r.getId(), r);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
