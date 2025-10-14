package au.edu.rmit.cosc1295.carehome.exceptions;
public class NotFoundException extends RuntimeException
{
    public NotFoundException(String w)
    {
        super(w+" not found");
    }
}
