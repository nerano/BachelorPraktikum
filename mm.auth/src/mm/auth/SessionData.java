package mm.auth;

import java.util.Date;

public class SessionData {

  private Date expire;
  private String role;
  
  public SessionData(Date expire, String role) {
    this.expire = expire;
    this.role = role;
  }
  
  public Date getExpire() {
    return expire;
  }
  
  public void setExpire(Date expire) {
    this.expire = expire;
  }
  
  public String getRole() {
    return role;
  }
  
  public void setRole(String role) {
    this.role = role;
  }
}
