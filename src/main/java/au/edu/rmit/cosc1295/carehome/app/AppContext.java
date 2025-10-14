package au.edu.rmit.cosc1295.carehome.app;
import au.edu.rmit.cosc1295.carehome.model.*;
import au.edu.rmit.cosc1295.carehome.repository.*;
import au.edu.rmit.cosc1295.carehome.service.*;
public final class AppContext {
    // 1) Repos & audit FIRST
    public final InMemoryResidentRepository residentRepo = new InMemoryResidentRepository();
    public final InMemoryStaffRepository    staffRepo    = new InMemoryStaffRepository();
    public final InMemoryBedRepository      bedRepo      = new InMemoryBedRepository();
    public final InMemoryPrescriptionRepository prescriptionRepo = new InMemoryPrescriptionRepository();
    public final InMemoryAdministrationRepository adminRepo      = new InMemoryAdministrationRepository();
    public final AuditService audit = new AuditService();
    // 2) Services AFTER dependencies exist
    public final ShiftService          shiftService          = new ShiftService(staffRepo, audit);
    public final AuthorizationService  authorizationService  = new AuthorizationService(shiftService);
    public final ResidentService       residentService       = new ResidentService(residentRepo, bedRepo, audit);
    public final StaffService          staffService          = new StaffService(staffRepo, audit);
    public final PrescriptionService   prescriptionService   = new PrescriptionService(audit, prescriptionRepo);
    public final AdministrationService administrationService = new AdministrationService(audit, adminRepo);
    public final BedService            bedService            = new BedService(bedRepo, audit);
    // Track current logged-in user
    private Staff currentUser;
    
    private AppContext() {
        // No seed data - all data comes from database
    }
    
    public Staff getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(Staff user) {
        this.currentUser = user;
    }
    
    public String getPasswordHash() {
        return currentUser != null ? currentUser.getPasswordHash() : null;
    }
    
    private static final AppContext INSTANCE = new AppContext();
    public static AppContext get() { return INSTANCE; }
}
