package mm.auth.session;

public class UserData {
  private String userName;
  private String password;
  private String role;

  /**
   * Constructor setting user name, password and role.
   * 
   * @param user name 
   * @param pw password for user
   * @param role contains the role of a user. Could be administrator or standard user.
   */
  public UserData(String user, String pw, String role) {
    this.userName = user;
    this.password = pw;
    this.role = role;
  }
  
  /**
   * Constructor setting only password and role.
   * 
   * @param pw password for user
   * @param role contains the role of a user. Could be administrator or standard user.
   */
  public UserData(String pw, String role) {
    this.setPassword(pw);
    this.setRole(role);
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }
}
