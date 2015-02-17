package mm.controller.auth;

import java.util.Scanner;

/* Will be done by the Webinterface, works */
public class DataInput {

  private String userName;
  private String password;
  private Scanner scanIn = new Scanner(System.in);
  
  public void setUserName() {
    System.out.println("Enter your username: ");
    this.userName = scanIn.nextLine();
  }
  
  public void setPassword() {
    System.out.println("Enter your password: ");
    this.password = scanIn.nextLine();
  }
  
  public String getUserName() {
    return this.userName;
  }
  
  public String getPassword() {
    return this.password;
  }

  public void closeScanner() {
    this.scanIn.close();
  }
}
