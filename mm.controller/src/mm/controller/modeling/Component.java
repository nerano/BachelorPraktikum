package mm.controller.modeling;

public class Component {

  private String type;
  @SuppressWarnings("unused")
private boolean status;
  private int vLanId;
  private String port;
  private String powerSource;
  
  public Component(String type) {
    this.type = type;
    // this.vLanId;
    // this.statusfalse;
  }
  
  public void setPowerSource(String powerSource){
	  this.powerSource = powerSource;
  }
  
  public String getPowerSource(){
	  return this.powerSource;
  }
  
  protected int getvLanId(){
	  return vLanId;
  }
 
  public void setStatus(boolean status) {
    this.status = status;
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
