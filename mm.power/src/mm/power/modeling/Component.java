package mm.power.modeling;

import com.google.gson.annotations.Expose;

import mm.power.implementation.PowerSupply;

public class Component {

  private String type;
  private transient PowerSupply ps;
  private transient int port;
  private boolean status;

  public Component (String type, PowerSupply ps, int port) {
        
    this.type = type;
    this.ps = ps;
    this.port = port;
        this.status = false;
        
  }

  
  public void setStatus(boolean status){
      
      this.status = status;
      
  }
public String getTyp() {
    return type;
}

public void setTyp(String typ) {
    this.type = typ;
}

public PowerSupply getPs() {
    return ps;
}

public void setPs(PowerSupply ps) {
    this.ps = ps;
}

public int getPort() {
    return port;
}

public void setPort(int port) {
    this.port = port;
}

  
  
    
    

}
