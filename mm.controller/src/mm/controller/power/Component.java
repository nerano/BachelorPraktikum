package mm.controller.power;

import com.google.gson.annotations.Expose;



public class Component {

  private String typ;
 // private transient PowerSupply ps;
 // private transient int port;
  private int status;
  private int vlanid;

  public Component (String typ) {
        
    this.typ = typ;
    //this.ps = ps;
   // this.port = port;
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



  
  
    
    

}
