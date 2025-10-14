package au.edu.rmit.cosc1295.carehome.service;
import java.io.*;
import java.time.LocalDateTime;
public class AuditService {
  private final File file = new File("actions.log");
  public synchronized void log(String staffId, String action, String details)
  {
    String line = String.format("[%s] staff=%s action=%s details=%s%n", LocalDateTime.now(), staffId, action, details);
    System.out.print(line);
    try (FileWriter fw = new FileWriter(file, true))
    {
        fw.write(line);
    }
    catch(IOException ignored) {
    }
  }
}
