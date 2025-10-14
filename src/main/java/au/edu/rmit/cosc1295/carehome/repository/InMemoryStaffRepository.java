package au.edu.rmit.cosc1295.carehome.repository;
import au.edu.rmit.cosc1295.carehome.model.Staff;
public class InMemoryStaffRepository
        extends InMemoryRepository<Staff>
{
    public void save(Staff s)
    {
        store.put(s.getId(), s);
    }
}
