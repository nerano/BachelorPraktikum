package mm.controller.net;

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
    
    
    
    


}
