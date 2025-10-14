package au.edu.rmit.cosc1295.carehome.util;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:h2:./carehome_db";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    private static Connection connection;
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            initializeTables();
        }
        return connection;
    }
    
    private static void initializeTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Staff table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS staff (
                    id VARCHAR(50) PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    role VARCHAR(20) NOT NULL,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password_hash VARCHAR(255) NOT NULL
                )
            """);
            
            // Residents table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS residents (
                    id VARCHAR(50) PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    gender VARCHAR(10) NOT NULL,
                    bed_id VARCHAR(50)
                )
            """);
            
            // Beds table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS beds (
                    id VARCHAR(50) PRIMARY KEY,
                    resident_id VARCHAR(50)
                )
            """);
            
            // Shifts table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS shifts (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    staff_id VARCHAR(50) NOT NULL,
                    day_of_week VARCHAR(10) NOT NULL,
                    start_time TIME NOT NULL,
                    end_time TIME NOT NULL,
                    FOREIGN KEY (staff_id) REFERENCES staff(id)
                )
            """);
            
            // Prescriptions table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS prescriptions (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    resident_id VARCHAR(50) NOT NULL,
                    doctor_id VARCHAR(50) NOT NULL,
                    medicine VARCHAR(255) NOT NULL,
                    dose VARCHAR(100) NOT NULL,
                    schedule_info VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (resident_id) REFERENCES residents(id),
                    FOREIGN KEY (doctor_id) REFERENCES staff(id)
                )
            """);
            
            // Administration records table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS administration_records (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    resident_id VARCHAR(50) NOT NULL,
                    nurse_id VARCHAR(50) NOT NULL,
                    medicine VARCHAR(255) NOT NULL,
                    dose VARCHAR(100) NOT NULL,
                    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (resident_id) REFERENCES residents(id),
                    FOREIGN KEY (nurse_id) REFERENCES staff(id)
                )
            """);
            
            // Initialize default beds if table is empty
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM beds");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO beds (id) VALUES " +
                    "('W1-R1-B1'), ('W1-R1-B2'), ('W1-R2-B1'), ('W1-R2-B2'), " +
                    "('W1-R2-B3'), ('W1-R2-B4'), ('W1-R3-B1'), " +
                    "('W2-R1-B1'), ('W2-R1-B2'), ('W2-R2-B1')");
            }
            
            // Initialize default admin user if no users exist
            rs = stmt.executeQuery("SELECT COUNT(*) FROM staff");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO staff (id, name, role, username, password_hash) " +
                    "VALUES ('M-ADMIN', 'Admin', 'MANAGER', 'admin', 'admin')");
            }
        }
    }
    
    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

