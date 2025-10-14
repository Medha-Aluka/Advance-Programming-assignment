package au.edu.rmit.cosc1295.carehome.exceptions;
public class ShiftViolationException extends Exception
{
    public ShiftViolationException(String m)
    {
        super(m);
    }
    public ShiftViolationException()
    {
        super("Shift allocation violates rules.");
    }
}
