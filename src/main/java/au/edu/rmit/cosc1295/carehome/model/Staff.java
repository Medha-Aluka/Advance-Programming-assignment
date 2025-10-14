package au.edu.rmit.cosc1295.carehome.model;
public abstract class Staff extends Person {
  private final Role role;
  private String username;
  private String passwordHash;
  protected Staff(String id,String name,Role role,String username,String password)
  {
      super(id,name);
      this.role=role;
      this.username=username;
      this.passwordHash=password; }
  public Role getRole()
  {
      return role;
  }
  public String getUsername()
  {
      return username;
  }
  public void setUsername(String u)
  {
      this.username=u;
  }
  public void setPassword(String p)
  {
      this.passwordHash=p;
  }
  public String getPasswordHash()
  {
      return passwordHash;
  }
  @Override public String toString()
  {
      return getName() + " (" + role + ")";
  }
}
