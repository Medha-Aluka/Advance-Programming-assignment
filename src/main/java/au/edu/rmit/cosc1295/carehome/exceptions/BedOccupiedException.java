package au.edu.rmit.cosc1295.carehome.exceptions;
public class BedOccupiedException extends Exception
{
    public BedOccupiedException()
    {

        super("Bed is already occupied.");
    }
    public BedOccupiedException(String m)
    {

        super(m);
    }
}
