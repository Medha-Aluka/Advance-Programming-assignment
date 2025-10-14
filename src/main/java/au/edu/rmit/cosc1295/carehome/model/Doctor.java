package au.edu.rmit.cosc1295.carehome.model;
public class Doctor extends Staff
{
    public Doctor(String id, String name, String u, String p)
    {
        super(id,name,Role.DOCTOR,u,p);
    }
}
