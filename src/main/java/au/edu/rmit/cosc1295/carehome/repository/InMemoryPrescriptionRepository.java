package au.edu.rmit.cosc1295.carehome.repository;
import au.edu.rmit.cosc1295.carehome.model.Prescription;
public class InMemoryPrescriptionRepository
        extends InMemoryRepository<Prescription>
{
    public void save(Prescription p)
    {
        store.put(p.getId(), p);
    }
}
