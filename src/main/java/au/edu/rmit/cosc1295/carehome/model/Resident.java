package au.edu.rmit.cosc1295.carehome.model;
public class Resident {
  private final String id;
  private String name;
  private Gender gender;
  private String bedId;
  public Resident(String id,String name,Gender gender)
  {
      this.id=id;
      this.name=name;
      this.gender=gender; }
  public String getId()
  {
      return id;
  }
  public String getName()
  {
      return name;
  }
  public void setName(String n)
  {
      this.name=n;
  }
  public Gender getGender()
  {
      return gender;
  }
  public void setGender(Gender g)
  {
      this.gender=g;
  }
  public String getBedId()
  {
      return bedId;
  }
  public void setBedId(String b)
  {
      this.bedId=b;
  }
  @Override public String toString()
  {
      return name + " ["+id+"]";
  }
}
