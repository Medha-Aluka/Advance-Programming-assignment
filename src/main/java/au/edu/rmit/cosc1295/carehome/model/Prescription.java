package au.edu.rmit.cosc1295.carehome.model;
import java.time.LocalDateTime;
public class Prescription {
  private final String id,residentId,doctorId,medicine,dose,schedule;
  private final LocalDateTime createdAt=LocalDateTime.now();
  public Prescription(String id,String r,String d,String m,String dose,String sched)
  {
      this.id=id; residentId=r; doctorId=d; medicine=m;
      this.dose=dose; schedule=sched;
  }
  public String getId()
  {

      return id;
  }
  public String getResidentId()
  {
      return residentId;
  }
  public String getDoctorId()
  {
      return doctorId;
  }
  public String getMedicine()
  {
      return medicine;
  }
  public String getDose()
  {
      return dose;
  }
  public String getSchedule()
  {
      return schedule;
  }
  public LocalDateTime getCreatedAt()
  {
      return createdAt;
  }
  @Override public String toString()
  {
      return medicine+" "+dose+" @ "+schedule;
  }
}
