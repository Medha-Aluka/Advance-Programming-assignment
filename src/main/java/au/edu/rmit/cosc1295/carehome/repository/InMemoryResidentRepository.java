package au.edu.rmit.cosc1295.carehome.repository;
import au.edu.rmit.cosc1295.carehome.model.Resident;
public class InMemoryResidentRepository
        extends InMemoryRepository<Resident>
{
    public void save(Resident r)
    {
        store.put(r.getId(), r);
    }
}
