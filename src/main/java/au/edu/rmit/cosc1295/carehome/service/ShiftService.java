package au.edu.rmit.cosc1295.carehome.service;
import au.edu.rmit.cosc1295.carehome.exceptions.*;
import au.edu.rmit.cosc1295.carehome.model.*;
import au.edu.rmit.cosc1295.carehome.repository.*;
import java.util.*;
public class ShiftService {
  private final InMemoryStaffRepository staffRepo;
  private final AuditService audit;
  private final Map<String, List<Shift>> shiftsByStaff = new HashMap<>();
  public ShiftService(InMemoryStaffRepository r, AuditService a)
  {
      staffRepo=r;
      audit=a;
  }
  public List<Shift> getShifts(String staffId)
  {
      return shiftsByStaff.getOrDefault(staffId,new ArrayList<>());
  }
  public void assignShift(String managerId, String staffId, Shift shift)
          throws ShiftViolationException
  {
    staffRepo.findById(staffId).orElseThrow(() -> new NotFoundException("Staff"));
    var list = shiftsByStaff.computeIfAbsent(staffId,k->new ArrayList<>());
    for(Shift e:list) if(e.overlaps(shift)) throw new ShiftViolationException("Overlapping shift: "+e+" vs "+shift);
    if(shift.hours()>Rules.MAX_SHIFT_HOURS) throw new ShiftViolationException("Shift exceeds "+Rules.MAX_SHIFT_HOURS+"h");
    long dailyHours = list.stream().filter(x->x.getDay()==shift.getDay()).mapToLong(Shift::hours).sum() + shift.hours();
    if(dailyHours>Rules.MAX_DAILY_HOURS) throw new ShiftViolationException("Daily hours exceed "+Rules.MAX_DAILY_HOURS+"h");
    long weeklyHours = list.stream().mapToLong(Shift::hours).sum() + shift.hours();
    if(weeklyHours>Rules.MAX_WEEKLY_HOURS) throw new ShiftViolationException("Weekly hours exceed "+Rules.MAX_WEEKLY_HOURS+"h");
    list.add(shift); audit.log(managerId,"ASSIGN_SHIFT", staffId+" -> "+shift);
  }
}
