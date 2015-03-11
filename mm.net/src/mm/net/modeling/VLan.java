package mm.net.modeling;

import java.util.LinkedList;
import java.util.List;

public class VLan {
    
  String name;  
  private int id;
  private List<String> portList = new LinkedList<String>();
  private boolean global;

    
  
  public VLan(int id, boolean global) {
      
      this.id = id;
      this.global = global;
      
  }
  
  
  public VLan(int id) {
      this.id = id;
  }
  
  public VLan(String name, int id, boolean global){
        
    this.id = id;
  }
    
    
  public void addPorts(LinkedList<String> list){
        
    portList.addAll(list);
        
  }

  
  public void clear() {
      this.name = "";
      this.portList = new LinkedList<String>();
  }

public List<String> getPortList() {
	return portList;
}


public void setPortList(List<String> portList) {
	this.portList = portList;
}


public int getId() {
	return id;
}
    
    
   
    


}
