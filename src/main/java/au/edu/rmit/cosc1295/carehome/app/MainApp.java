package au.edu.rmit.cosc1295.carehome.app;
import au.edu.rmit.cosc1295.carehome.exceptions.*;
import au.edu.rmit.cosc1295.carehome.model.*;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalTime;
import java.util.*;
public class MainApp {
    private final AppContext ctx = AppContext.get();
    private final Scanner in = new Scanner(System.in);
    public static void main(String[] args) {
        new MainApp().run();
    }
    private void run() {
        splash();
        while (true) {
            switch (menu("""
        1) Residents  2) Staff  3) Shifts  4) Prescriptions  5) Admin Log  0) Exit
        Choose option"""))
            {
                case "1" -> residentsMenu();
                case "2" -> staffMenu();
                case "3" -> shiftsMenu();
                case "4" -> prescriptionsMenu();
                case "5" -> showLog();
                case "0" -> { System.out.println("Bye!");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }
    private void splash() {
        System.out.println("""
    ================= Care Home CLI ================
    Seeded staff: Manager Mary [M-1], Dr. Sam [D-1], Nurse Kim [N-1]
    Seeded residents: Alex [R-1], Mia [R-2]
    Beds: W1-R1-B1, W1-R1-B2, W1-R2-B1
    =================================================""");
    }
    // ---------- Residents ----------
    private void residentsMenu() {
        while (true) {
            switch (menu("Residents: 1) List  2) Assign  3) Move  4) Add Resident  5) Add Bed  0) Back")) {
                case "1" -> listResidents();
                case "2" -> assignResident();
                case "3" -> moveResident();
                case "4" -> addResident();
                case "5" -> addBed();
                case "0" -> { return; }
                default -> System.out.println("Invalid.");
            }
        }
    }
    private void addBed() {
        String id = ask("New bed ID (e.g., W1-R3-B2)");
        try { ctx.bedService.addBed(ctx.manager.getId(), id); System.out.println("Added bed: " + id); }
        catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }
    private void listResidents() {
        System.out.println("Residents:");
        for (var r : ctx.residentRepo.findAll()) {
            System.out.printf("- %s [%s] bed=%s%n", r.getName(), r.getId(),
                    Objects.toString(r.getBedId(), "(none)"));
        }
        System.out.println("Beds:");
        for (var b : ctx.bedRepo.findAll()) System.out.println("- " + b);
    }
    private void assignResident() {
        String rid = ask("Resident ID (e.g., R-1)"); String bid = ask("Bed ID (e.g., W1-R1-B1)");
        try { ctx.residentService.assignResidentToBed(ctx.manager.getId(), rid, bid);
            System.out.println("Assigned.");
        }
        catch (BedOccupiedException | RuntimeException ex) { System.out.println("Error: " + ex.getMessage()); }
    }
    private void moveResident() {
        String rid = ask("Resident ID");
        String bid = ask("To Bed ID");
        try { ctx.residentService.moveResident(ctx.nurse.getId(), rid, bid); System.out.println("Moved.");
        }
        catch (BedOccupiedException | RuntimeException ex)
        {
            System.out.println("Error: " + ex.getMessage());
        }
    }
    private void addResident() {
        String name = ask("Resident name");
        String g = ask("Gender (MALE/FEMALE/OTHER)").toUpperCase();
        try {
            Gender gender = Gender.valueOf(g);
            var r = ctx.residentService.addResident(ctx.manager.getId(), name, gender);
            System.out.println("Added resident: " + r.getId());
        } catch (Exception e) { System.out.println("Error: " + e.getMessage());
        }
    }
    // ---------- Staff ----------
    private void staffMenu() {
        while (true) {
            switch (menu("Staff: 1) List  2) Set Credentials 3) Add Staff  0) Back")) {
                case "1" -> listStaff();
                case "2" -> setCreds();
                case "3" -> addStaff();
                case "0" -> { return; }
                default -> System.out.println("Invalid.");
            }
        }
    }
    private void listStaff() {
        for (var s : ctx.staffRepo.findAll())
            System.out.printf("- %s [%s] role=%s user=%s%n", s.getName(), s.getId(), s.getRole(), s.getUsername());
    }
    private void setCreds() {
        String sid = ask("Staff ID (M-1/D-1/N-1)");
        var s = ctx.staffRepo.findById(sid).orElse(null);
        if (s==null) { System.out.println("Not found.");
            return;
        }
        String u = ask("New username");
        String p = ask("New password");
        ctx.staffService.saveOrUpdateCredentials(ctx.manager.getId(), s, u, p);
        System.out.println("Saved.");
    }
    private void addStaff() {
        String name = ask("Name");
        Role role = Role.valueOf(ask("Role (MANAGER/DOCTOR/NURSE)").toUpperCase());
        String user = ask("Username"); String pass = ask("Password");
        try {
            var s = ctx.staffService.addStaff(ctx.manager.getId(), name, role, user, pass);
            System.out.println("Added staff: " + s.getId());
        } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }
    // ---------- Shifts ----------
    private void shiftsMenu() {
        while (true) {
            switch (menu("Shifts: 1) List for Staff  2) Assign  0) Back")) {
                case "1" -> listShifts();
                case "2" -> assignShift();
                case "0" -> { return; }
                default -> System.out.println("Invalid.");
            }
        }
    }
    private void listShifts() {
        String sid = ask("Staff ID");
        var list = ctx.shiftService.getShifts(sid);
        if (list.isEmpty()) System.out.println("(none)");
        else list.forEach(s -> System.out.println("- " + s));
    }
    private void assignShift() {
        String sid = ask("Staff ID"); DayOfWeek day = DayOfWeek.valueOf(ask("Day (MON..SUN)").toUpperCase());
        try {
            LocalTime st = LocalTime.parse(ask("Start (HH:MM)")); LocalTime en = LocalTime.parse(ask("End (HH:MM)"));
            ctx.shiftService.assignShift(ctx.manager.getId(), sid, new Shift(day, st, en));
            System.out.println("Assigned.");
        } catch (ShiftViolationException ex) { System.out.println("Rule violation: " + ex.getMessage()); }
        catch (Exception ex) { System.out.println("Invalid input: " + ex.getMessage()); }
    }
    // ---------- Prescriptions ----------
    private void prescriptionsMenu() {
        while (true) {
            switch (menu("Prescriptions: 1) Add (Doctor)  2) Administer (Nurse)  0) Back")) {
                case "1" -> addPrescription();
                case "2" -> administer();
                case "0" -> { return; }
                default -> System.out.println("Invalid.");
            }
        }
    }
    private void addPrescription() {
        String rid = ask("Resident ID"); String med = ask("Medicine"); String dose = ask("Dose"); String sched = ask("Schedule");
        try { ctx.prescriptionService.add(ctx.doctor.getId(), ctx.doctor, rid, med, dose, sched); System.out.println("Added."); }
        catch (AuthorizationException e)
        { System.out.println("Auth error.");
        }
    }
    private void administer()
    {
        String rid = ask("Resident ID"); String med = ask("Medicine"); String dose = ask("Dose");
        try { ctx.administrationService.administerDose(ctx.nurse.getId(), ctx.nurse, rid, med, dose); System.out.println("Administered."); }
        catch (AuthorizationException e)
        {
            System.out.println("Auth error.");
        }
    }
    // ---------- Log ----------
    private void showLog() {
        var p = Path.of("actions.log");
        if (!Files.exists(p)) { System.out.println("(No log yet)");
            return;
        }
        try {
            Files.lines(p).forEach(System.out::println);
        }
        catch (IOException e)
        { System.out.println("Error: " + e.getMessage());
        }
    }
    // ---------- helpers ----------
    private String menu(String prompt)
    {
        System.out.print("\n" + prompt + " > ");
        return in.nextLine().trim();
    }
    private String ask(String prompt)
    {
        System.out.print(prompt + ": ");
        return in.nextLine().trim();
    }
}
