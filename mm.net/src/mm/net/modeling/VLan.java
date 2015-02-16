package mm.net.modeling;

import java.util.LinkedList;
import java.util.List;

public class VLan {

  private final int id;
  private List<String> portList;

    
  public VLan(int id){
        
    this.id = id;
    portList = new LinkedList<String>();
  }
    
    
  public void addPorts(LinkedList<String> list){
        
    portList.addAll(list);
        
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
