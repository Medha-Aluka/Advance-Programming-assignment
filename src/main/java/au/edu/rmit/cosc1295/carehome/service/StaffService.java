package au.edu.rmit.cosc1295.carehome.service;
import au.edu.rmit.cosc1295.carehome.util.Ids;
import au.edu.rmit.cosc1295.carehome.model.*;
import au.edu.rmit.cosc1295.carehome.repository.*;
public class StaffService {
  private final InMemoryStaffRepository staffRepo;
  private final AuditService audit;
  public StaffService(InMemoryStaffRepository r, AuditService a)
  {
      staffRepo=r;
      audit=a;
  }
  public void saveOrUpdateCredentials(String managerId, Staff staff, String username, String password){
    staff.setUsername(username);
    staff.setPassword(password);
    staffRepo.save(staff);
    audit.log(managerId,"UPSERT_STAFF", staff.getId());
  }
    public Staff addStaff(String managerId, String name, Role role, String username, String password) {
        String id = switch (role)
        {
            case MANAGER -> Ids.next("M");
            case DOCTOR  -> Ids.next("D");
            case NURSE   -> Ids.next("N");
        };
        Staff s = switch (role)
        {
            case MANAGER -> new Manager(id, name, username, password);
            case DOCTOR  -> new Doctor(id, name, username, password);
            case NURSE   -> new Nurse (id, name, username, password);
        };
        staffRepo.save(s);
        audit.log(managerId, "ADD_STAFF", s.getId() + " role=" + role + " user=" + username);
        return s;
    }
}
