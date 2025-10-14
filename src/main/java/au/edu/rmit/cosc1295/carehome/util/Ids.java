package au.edu.rmit.cosc1295.carehome.util;
public final class Ids {
    private Ids() {}
    public static String next(String prefix) {
        return prefix + "-" + Long.toString(System.nanoTime(), 36).toUpperCase();
    }
}
