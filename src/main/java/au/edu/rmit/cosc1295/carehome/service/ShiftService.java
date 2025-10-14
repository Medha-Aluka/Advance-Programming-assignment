package au.edu.rmit.cosc1295.carehome.service;
import au.edu.rmit.cosc1295.carehome.exceptions.*;
import au.edu.rmit.cosc1295.carehome.model.*;
import au.edu.rmit.cosc1295.carehome.repository.*;
import au.edu.rmit.cosc1295.carehome.util.DatabaseManager;
import java.util.*;
import java.sql.*;
import java.time.LocalTime;

public class ShiftService {
    private final InMemoryStaffRepository staffRepo;
    private final AuditService audit;
    private final Map<String, List<Shift>> shiftsByStaff = new HashMap<>();
    
    public ShiftService(InMemoryStaffRepository r, AuditService a) {
        staffRepo = r;
        audit = a;
        loadShiftsFromDatabase();
    }
    
    private void loadShiftsFromDatabase() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM shifts")) {
            
            while (rs.next()) {
                String staffId = rs.getString("staff_id");
                String dayStr = rs.getString("day_of_week");
                Time startTime = rs.getTime("start_time");
                Time endTime = rs.getTime("end_time");
                
                DayOfWeek day = DayOfWeek.valueOf(dayStr);
                LocalTime start = startTime.toLocalTime();
                LocalTime end = endTime.toLocalTime();
                
                Shift shift = new Shift(day, start, end);
                shiftsByStaff.computeIfAbsent(staffId, k -> new ArrayList<>()).add(shift);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<Shift> getShifts(String staffId) {
        return shiftsByStaff.getOrDefault(staffId, new ArrayList<>());
    }
    
    public void assignShift(String managerId, String staffId, Shift shift)
            throws ShiftViolationException {
        staffRepo.findById(staffId).orElseThrow(() -> new NotFoundException("Staff"));
        var list = shiftsByStaff.computeIfAbsent(staffId, k -> new ArrayList<>());
        
        for (Shift e : list) {
            if (e.overlaps(shift)) {
                throw new ShiftViolationException("Overlapping shift: " + e + " vs " + shift);
            }
        }
        
        if (shift.hours() > Rules.MAX_SHIFT_HOURS) {
            throw new ShiftViolationException("Shift exceeds " + Rules.MAX_SHIFT_HOURS + "h");
        }
        
        long dailyHours = list.stream()
            .filter(x -> x.getDay() == shift.getDay())
            .mapToLong(Shift::hours).sum() + shift.hours();
        if (dailyHours > Rules.MAX_DAILY_HOURS) {
            throw new ShiftViolationException("Daily hours exceed " + Rules.MAX_DAILY_HOURS + "h");
        }
        
        long weeklyHours = list.stream().mapToLong(Shift::hours).sum() + shift.hours();
        if (weeklyHours > Rules.MAX_WEEKLY_HOURS) {
            throw new ShiftViolationException("Weekly hours exceed " + Rules.MAX_WEEKLY_HOURS + "h");
        }
        
        // Save to database
        saveShiftToDatabase(staffId, shift);
        
        list.add(shift);
        audit.log(managerId, "ASSIGN_SHIFT", staffId + " -> " + shift);
    }
    
    private void saveShiftToDatabase(String staffId, Shift shift) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO shifts (staff_id, day_of_week, start_time, end_time) VALUES (?, ?, ?, ?)")) {
            
            pstmt.setString(1, staffId);
            pstmt.setString(2, shift.getDay().toString());
            pstmt.setTime(3, Time.valueOf(shift.getStart()));
            pstmt.setTime(4, Time.valueOf(shift.getEnd()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
