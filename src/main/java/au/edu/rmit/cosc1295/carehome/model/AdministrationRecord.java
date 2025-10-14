package au.edu.rmit.cosc1295.carehome.model;
import java.time.LocalDateTime;
public class AdministrationRecord {
  private final String residentId, nurseId, medicine;
  private String dose;
  private final LocalDateTime timestamp=LocalDateTime.now();
  public AdministrationRecord(String r,String n,String m,String d){ residentId=r; nurseId=n; medicine=m; dose=d; }
  public String getResidentId()
  {
      return residentId;
  }
  public String getNurseId()
  {
      return nurseId;
  }
  public String getStaffId()
  {
      return nurseId;
  }
  public String getMedicine()
  {
      return medicine;
  }
  public String getDose()
  {
      return dose;
  }
  public void setDose(String d)
  {
      dose=d;
  }
  public LocalDateTime getAdministeredAt()
  {
      return timestamp;
  }
  public LocalDateTime getTimestamp()
  {
      return timestamp;
  }
}
