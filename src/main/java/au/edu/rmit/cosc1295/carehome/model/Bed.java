package au.edu.rmit.cosc1295.carehome.model;
public class Bed {
  private final String id;
  private String residentId;
  public Bed(String id)
  {
      this.id=id;
  }
  public String getId()
  {
      return id;
  }
  public boolean isVacant()
  {
      return residentId==null;
  }
  public String getResidentId()
  {
      return residentId;
  }
  public void assignResident(String r)
  {
      this.residentId=r;
  }
  public void vacate()
  {
      this.residentId=null;
  }
  @Override public String toString()
  {
      return id + (isVacant()? " (vacant)" : " [R="+residentId+"]");
  }
}
