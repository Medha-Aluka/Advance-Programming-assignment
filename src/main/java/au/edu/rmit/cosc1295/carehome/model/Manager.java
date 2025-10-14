package au.edu.rmit.cosc1295.carehome.model;
public class Manager extends Staff
{
    public Manager(String id, String name, String u, String p)
    {
        super(id,name,Role.MANAGER,u,p);
    }
}
