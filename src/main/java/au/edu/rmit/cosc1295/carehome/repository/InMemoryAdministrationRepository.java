package au.edu.rmit.cosc1295.carehome.repository;
import au.edu.rmit.cosc1295.carehome.model.AdministrationRecord;
public class InMemoryAdministrationRepository extends InMemoryRepository<AdministrationRecord>
{
    public void save(String id, AdministrationRecord r)
    {
        store.put(id, r);

    }
}
