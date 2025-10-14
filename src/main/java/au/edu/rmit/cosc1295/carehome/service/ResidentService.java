package au.edu.rmit.cosc1295.carehome.service;
import au.edu.rmit.cosc1295.carehome.exceptions.*;
import au.edu.rmit.cosc1295.carehome.model.*;
import au.edu.rmit.cosc1295.carehome.repository.*;
import au.edu.rmit.cosc1295.carehome.util.Ids;
public class ResidentService {
  private final InMemoryResidentRepository residents;
  private final InMemoryBedRepository beds;
  private final AuditService audit;
  public ResidentService(InMemoryResidentRepository r, InMemoryBedRepository b, AuditService a)
  {
      residents=r;
      beds=b;
      audit=a;
  }
  public void assignResidentToBed(String staffId, String residentId, String bedId)
          throws BedOccupiedException
  {
    Resident r = residents.findById(residentId).orElseThrow(() -> new NotFoundException("Resident"));
    Bed b = beds.findById(bedId).orElseThrow(() -> new NotFoundException("Bed"));
    if(!b.isVacant())
        throw new BedOccupiedException();
    b.assignResident(r.getId()); r.setBedId(b.getId());
    audit.log(staffId,"ASSIGN_RESIDENT","resident="+residentId+", bed="+bedId);
  }
  public void moveResident(String staffId, String residentId, String toBedId)
          throws BedOccupiedException
  {
    Resident r = residents.findById(residentId).orElseThrow(() -> new NotFoundException("Resident"));
    String fromBedId = r.getBedId();
    if(fromBedId!=null) beds.findById(fromBedId).orElseThrow(() -> new NotFoundException("Bed(from)")).vacate();
    assignResidentToBed(staffId, residentId, toBedId);
    audit.log(staffId,"MOVE_RESIDENT","resident="+residentId+", toBed="+toBedId);
  }
    public Resident addResident(String staffId, String name, Gender gender)
    {
        Resident r = new Resident(Ids.next("R"), name, gender);
        residents.save(r);
        audit.log(staffId, "ADD_RESIDENT", "resident=" + r.getId() + ", name=" + name + ", gender=" + gender);
        return r;
    }
}
