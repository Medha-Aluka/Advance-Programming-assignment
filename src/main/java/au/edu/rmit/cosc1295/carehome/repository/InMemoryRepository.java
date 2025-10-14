package au.edu.rmit.cosc1295.carehome.repository;
import java.util.*;
public abstract class InMemoryRepository<T>
{
  protected final Map<String,T> store = new HashMap<>();
  public Optional<T> findById(String id)
  {
      return Optional.ofNullable(store.get(id));
  }
  public List<T> findAll()
  {
      return new ArrayList<>(store.values());
  }
  public void delete(String id)
  {
      store.remove(id);
  }
}
