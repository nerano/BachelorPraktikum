package mm.controller.modeling;

public class VmObjects {

  private String vid;
  private String name;
  
  public VmObjects() {
    
  }
  
  public VmObjects(String vid) {
    this.vid = vid;
  }
  
  public String getvId() {
    return vid;
  }
  
  public void setvId(String vid) {
    this.vid = vid;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
}
