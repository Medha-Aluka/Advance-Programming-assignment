package au.edu.rmit.cosc1295.carehome.exceptions;
public class AuthorizationException extends Exception {
    public AuthorizationException(){
        super("Not authorized for this action.");
    }
    public AuthorizationException(String m){
        super(m);
    }
}
