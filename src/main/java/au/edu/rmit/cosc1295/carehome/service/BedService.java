package au.edu.rmit.cosc1295.carehome.service;
import au.edu.rmit.cosc1295.carehome.model.Bed;
import au.edu.rmit.cosc1295.carehome.repository.InMemoryBedRepository;
public class BedService {
    private final InMemoryBedRepository bedRepo;
    private final AuditService audit;
    public BedService(InMemoryBedRepository beds, AuditService audit) {
        this.bedRepo = beds;
        this.audit = audit;
    }
    public Bed addBed(String staffId, String bedId) {
        if (bedId == null || bedId.isBlank()) {
            throw new IllegalArgumentException("Bed ID must not be blank");
        }
        if (bedRepo.findById(bedId).isPresent()) {
            throw new IllegalArgumentException("Bed ID already exists: " + bedId);
        }
        Bed b = new Bed(bedId);
        bedRepo.save(b);
        audit.log(staffId, "ADD_BED", bedId);
        return b;
    }
}
