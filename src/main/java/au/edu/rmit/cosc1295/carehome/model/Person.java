package au.edu.rmit.cosc1295.carehome.model;
import java.io.Serializable;
public abstract class Person implements Serializable {
  private final String id;
  private String name;
  protected Person(String id,String name)
  {
      this.id=id;
      this.name=name; }
  public String getId()
  {
      return id;
  }
  public String getName()
  {
      return name;
  }
  public void setName(String name)
  {
      this.name=name;
  }
}
