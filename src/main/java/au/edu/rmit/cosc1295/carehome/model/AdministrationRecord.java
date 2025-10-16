package au.edu.rmit.cosc1295.carehome.model;
import java.time.LocalDateTime;
public class AdministrationRecord {
  private final String residentId, nurseId, medicine;
  private String dose;
  private String prescriptionTime;
  private final LocalDateTime timestamp;
  public AdministrationRecord(String r,String n,String m,String d){ 
      this(r, n, m, d, null, LocalDateTime.now());
  }
  public AdministrationRecord(String r,String n,String m,String d, LocalDateTime timestamp){ 
      this(r, n, m, d, null, timestamp);
  }
  public AdministrationRecord(String r,String n,String m,String d, String prescriptionTime){ 
      this(r, n, m, d, prescriptionTime, LocalDateTime.now());
  }
  public AdministrationRecord(String r,String n,String m,String d, String prescriptionTime, LocalDateTime timestamp){ 
      residentId=r; nurseId=n; medicine=m; dose=d; this.prescriptionTime=prescriptionTime; this.timestamp=timestamp;
  }
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
  public String getPrescriptionTime()
  {
      return prescriptionTime;
  }
  public void setPrescriptionTime(String prescriptionTime)
  {
      this.prescriptionTime = prescriptionTime;
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
