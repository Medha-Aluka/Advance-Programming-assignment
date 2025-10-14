package au.edu.rmit.cosc1295.carehome.repository;
import au.edu.rmit.cosc1295.carehome.model.*;
import au.edu.rmit.cosc1295.carehome.util.DatabaseManager;
import java.sql.*;
import java.util.*;

public class InMemoryStaffRepository extends InMemoryRepository<Staff> {
    
    public InMemoryStaffRepository() {
        loadFromDatabase();
    }
    
    private void loadFromDatabase() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM staff")) {
            
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String role = rs.getString("role");
                String username = rs.getString("username");
                String passwordHash = rs.getString("password_hash");
                
                Staff staff = switch (role) {
                    case "MANAGER" -> new Manager(id, name, username, passwordHash);
                    case "DOCTOR" -> new Doctor(id, name, username, passwordHash);
                    case "NURSE" -> new Nurse(id, name, username, passwordHash);
                    default -> null;
                };
                
                if (staff != null) {
                    store.put(id, staff);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void save(Staff s) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "MERGE INTO staff (id, name, role, username, password_hash) " +
                 "KEY (id) VALUES (?, ?, ?, ?, ?)")) {
            
            pstmt.setString(1, s.getId());
            pstmt.setString(2, s.getName());
            pstmt.setString(3, s.getRole().toString());
            pstmt.setString(4, s.getUsername());
            pstmt.setString(5, s.getPasswordHash());
            pstmt.executeUpdate();
            
            store.put(s.getId(), s);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
