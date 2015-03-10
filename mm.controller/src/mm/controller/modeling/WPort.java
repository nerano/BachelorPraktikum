package mm.controller.modeling;

import java.util.LinkedList;

import javax.ws.rs.core.Response;

public class WPort {

  String id;
  String port;
  String building;
  String room;
  LinkedList<Integer> vlanIds = new LinkedList<Integer>();
  
  
  public WPort(String id, String port, String building, String room) {
    
    this.id = id;
    this.port = port;
    this.building = building;
    this.room = room;
    
  }
  
  public Response setVLan(int vlanId) {
    
    //TODO implement setVLan for WPorts
    
    return null;
  }
  
  public Response destryVLan(int vlanId) {
    //TODO destroy the given vlan on this wport
    return null;
  }
  
  public Response destroyVLan() {
    //TODO destroy all vlans on this wport
    return null;
  }
  
  
  
  
}
