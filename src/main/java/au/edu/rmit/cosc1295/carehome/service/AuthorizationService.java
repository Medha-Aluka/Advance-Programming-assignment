package au.edu.rmit.cosc1295.carehome.service;

import au.edu.rmit.cosc1295.carehome.exceptions.AuthorizationException;
import au.edu.rmit.cosc1295.carehome.model.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Service to handle authorization checks for staff performing actions.
 * 
 * Design: Implements business rule checking for:
 * - Role-based permissions (DOCTOR can prescribe, NURSE can administer, MANAGER can manage)
 * - Shift roster checking (staff must be on duty for the action time)
 * 
 * This enforces the regulatory requirement that actions must be performed
 * by authorized staff during their assigned shifts.
 */
public class AuthorizationService {
    private final ShiftService shiftService;
    
    public AuthorizationService(ShiftService shiftService) {
        this.shiftService = shiftService;
    }
    
    /**
     * Check if a staff member is authorized to perform an action requiring a specific role.
     * Also checks if the staff is currently on duty (rostered for current day/time).
     */
    public void checkAuthorization(Staff staff, Role requiredRole) throws AuthorizationException {
        // Check role
        if (staff.getRole() != requiredRole && staff.getRole() != Role.MANAGER) {
            throw new AuthorizationException("Staff member " + staff.getId() + 
                " does not have required role: " + requiredRole);
        }
        
        // Managers can always perform actions
        if (staff.getRole() == Role.MANAGER) {
            return;
        }
        
        // Check if staff is rostered for current time
        if (!isStaffRostered(staff)) {
            throw new AuthorizationException("Staff member " + staff.getId() + 
                " (" + staff.getName() + ") is not rostered for current day/time");
        }
    }
    
    /**
     * Check if staff member is currently rostered (on duty).
     * Uses current day of week and time to match against assigned shifts.
     */
    public boolean isStaffRostered(Staff staff) {
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek currentDay = convertJavaDayToCustom(now.getDayOfWeek());
        LocalTime currentTime = now.toLocalTime();
        
        List<Shift> shifts = shiftService.getShifts(staff.getId());
        
        for (Shift shift : shifts) {
            if (shift.getDay() == currentDay &&
                !currentTime.isBefore(shift.getStart()) &&
                !currentTime.isAfter(shift.getEnd())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Converts Java's DayOfWeek enum to our custom DayOfWeek enum.
     */
    private DayOfWeek convertJavaDayToCustom(java.time.DayOfWeek javaDay) {
        return switch (javaDay) {
            case MONDAY -> DayOfWeek.MON;
            case TUESDAY -> DayOfWeek.TUE;
            case WEDNESDAY -> DayOfWeek.WED;
            case THURSDAY -> DayOfWeek.THU;
            case FRIDAY -> DayOfWeek.FRI;
            case SATURDAY -> DayOfWeek.SAT;
            case SUNDAY -> DayOfWeek.SUN;
        };
    }
    
    /**
     * Check if staff can perform a MANAGER-only action.
     */
    public void checkManagerAuthorization(Staff staff) throws AuthorizationException {
        if (staff.getRole() != Role.MANAGER) {
            throw new AuthorizationException("This action requires MANAGER role");
        }
    }
}

