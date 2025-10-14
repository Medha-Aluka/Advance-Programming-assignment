package au.edu.rmit.cosc1295.carehome.repository;
import au.edu.rmit.cosc1295.carehome.model.Bed;
public class InMemoryBedRepository extends InMemoryRepository<Bed>
{
    public void save(Bed b)
    {
        store.put(b.getId(), b);
    }
}
