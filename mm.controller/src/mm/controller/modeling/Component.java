package mm.controller.modeling;

public class Component {

  private boolean status;
  private int vLanId;
  private String type;
  
  public Component(String type) {
    this.type = type;
    this.vLanId = 0;
    this.status = false;
  }
  
  public boolean isStatus() {
    return status;
  }
  public void setStatus(boolean status) {
    this.status = status;
  }
  public int getvLanId() {
    return vLanId;
  }
  public void setvLanId(int vLanId) {
    this.vLanId = vLanId;
  }
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }
}
