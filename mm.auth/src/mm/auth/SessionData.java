package mm.auth;

import java.util.Date;

public class SessionData {

  private Date expire;
  private String sessionId;
  private String user;
  private String role;
  
  /**
   * Constructor setting expire time, user and user role.
   * 
   * @param expire time of sessionId.
   * @param user name.
   * @param role of the user using the sessionId.
   */
  public SessionData(Date expire, String user, String role) {
    this.expire = expire;
    this.user = user;
    this.role = role;
  }
  
  /**
   * Constructor setting sessionId and user role.
   * 
   * @param sessionId as initialization for these object.
   * @param role of the user using the sessionId
   */
  public SessionData(String sessionId, String role) {
    this.sessionId = sessionId;
    this.role = role;
  }
  
  public Date getExpire() {
    return expire;
  }
  
  public void setExpire(Date expire) {
    this.expire = expire;
  }
  
  public String getSessionId() {
    return this.sessionId;
  }
  
  public String getUser() {
    return user;
  }
  
  public String getRole() {
    return role;
  }
  
  public void setRole(String role) {
    this.role = role;
  }
}
