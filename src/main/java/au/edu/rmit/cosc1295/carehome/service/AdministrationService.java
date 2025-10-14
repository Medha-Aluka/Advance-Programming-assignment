package au.edu.rmit.cosc1295.carehome.service;
import au.edu.rmit.cosc1295.carehome.model.*;
import au.edu.rmit.cosc1295.carehome.exceptions.*;
import au.edu.rmit.cosc1295.carehome.repository.*;
public class AdministrationService {
  private final AuditService audit;
  private final InMemoryAdministrationRepository repo;
  public AdministrationService(AuditService a, InMemoryAdministrationRepository r)
  {
      audit=a; repo=r;
  }
  public AdministrationRecord administerDose(String nurseId, Staff actor, String residentId, String med, String dose)
          throws AuthorizationException {
    if(actor.getRole()!=Role.NURSE && actor.getRole()!=Role.MANAGER) throw new AuthorizationException();
    AdministrationRecord rec = new AdministrationRecord(residentId, nurseId, med, dose);
    repo.save("AR-"+System.nanoTime(), rec);
    audit.log(nurseId,"ADMINISTER_DOSE","resident="+residentId+", med="+med+", dose="+dose);
    return rec;
  }
}
