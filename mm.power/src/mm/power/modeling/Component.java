package mm.power.modeling;

import com.google.gson.annotations.Expose;

import mm.power.implementation.PowerSupply;

public class Component {

  private String typ;
  private transient PowerSupply ps;
  private transient int port;
  private int status;

  public Component (String typ, PowerSupply ps, int port) {
        
    this.typ = typ;
    this.ps = ps;
    this.port = port;
        this.status = 0;
        
  }

  
  public void setStatus(int status){
      
      this.status = status;
      
  }
public String getTyp() {
    return typ;
}

public void setTyp(String typ) {
    this.typ = typ;
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
