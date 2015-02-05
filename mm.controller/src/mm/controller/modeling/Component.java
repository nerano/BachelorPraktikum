package mm.controller.modeling;

public class Component {

  private String type;
  private boolean status;
  private int vLanId;
  private String port;
  
  
  public Component(String type) {
    this.type = type;
    // this.vLanId;
    // this.statusfalse;
  }
  
  public boolean getStatus() {
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

  public String getPort() {
	return port;
  }

  public void setPort(String port) {
	this.port = port;
  }
}
