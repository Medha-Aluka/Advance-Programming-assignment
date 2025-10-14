package au.edu.rmit.cosc1295.carehome.service;
import au.edu.rmit.cosc1295.carehome.model.*;
import au.edu.rmit.cosc1295.carehome.exceptions.*;
import au.edu.rmit.cosc1295.carehome.repository.*;
public class PrescriptionService {
  private final AuditService audit;
  private final InMemoryPrescriptionRepository repo;
  public PrescriptionService(AuditService a, InMemoryPrescriptionRepository r)
  {
      audit=a; repo=r;
  }
  public Prescription add(String doctorId, Staff actor, String residentId, String med, String dose, String schedule)
          throws AuthorizationException {
    if(actor.getRole()!=Role.DOCTOR)
        throw new AuthorizationException();
    Prescription p = new Prescription("P-"+System.nanoTime(), residentId, doctorId, med, dose, schedule);
    repo.save(p);
    audit.log(doctorId,"ADD_PRESCRIPTION","resident="+residentId+", med="+med+", dose="+dose+", sched="+schedule);
    return p;
  }
}
