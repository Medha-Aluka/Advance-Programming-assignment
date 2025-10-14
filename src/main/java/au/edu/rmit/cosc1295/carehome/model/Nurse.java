package au.edu.rmit.cosc1295.carehome.model;
public class Nurse extends Staff
{
    public Nurse(String id, String name, String u, String p)
    {
        super(id,name,Role.NURSE,u,p);
    }
}
