package au.edu.rmit.cosc1295.carehome.model;
import java.time.LocalTime;
import java.time.Duration;
public class Shift {
  private final DayOfWeek day;
  private final LocalTime start;
  private final LocalTime end;
  public Shift(DayOfWeek d, LocalTime s, LocalTime e)
  {
      if(!s.isBefore(e)) throw new IllegalArgumentException("start<end");
      day=d; start=s; end=e;
  }
  public DayOfWeek getDay()
  {
      return day;
  }
  public LocalTime getStart()
  {
      return start;
  }
  public LocalTime getEnd()
  {
      return end;
  }
  public long hours()
  {
      return Duration.between(start,end).toHours();
  }
  public boolean overlaps(Shift o)
  {
      if(day!=o.day) return false;
      return !(end.compareTo(o.start)<=0 || o.end.compareTo(start)<=0); }
  @Override public String toString()
  {
      return day + " " + start + "-" + end;
  }
}
