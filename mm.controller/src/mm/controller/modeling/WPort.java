package mm.controller.modeling;

import java.util.LinkedList;


public class WPort {

  String id;
  String port;
  String building;
  String room;
  String trunk;
  LinkedList<Integer> vlanIds = new LinkedList<Integer>();
  
  
  public WPort(String id, String port, String building, String room, String trunk) {
    
    this.id = id;
    this.port = port;
    this.building = building;
    this.room = room;
    this.trunk = trunk;
    
  }
  

public String getPort() {
    return this.port;
}
  
public String getId() {
    return this.id;
}

public String getTrunk() {
    return trunk;
}
  
  
  
  
}
