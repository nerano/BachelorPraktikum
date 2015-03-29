package mm.controller.modeling;

import java.util.LinkedList;

import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mm.controller.main.ControllerData;
import mm.controller.net.ControllerNetGet;


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
  
  
public Response isAvailable() {
    
    LinkedList<Interface> infList = ControllerNetGet.getVlanInfo(this);
    
    int responseStatus = 200;
    String responseString = null;
    int vlanId;
    LinkedList<Interface> returnList = new LinkedList<Interface>();
    Experiment exp;
    
    for (Interface inf : infList) {
        vlanId = inf.getVlanId();
        switch (vlanId) {
        case 0:
            responseStatus = 500;
            returnList.add(inf);
            break;
        case 1:
            break;
        default:
            exp = ControllerData.getExpByVlanId(vlanId);
            if(exp == null) {
                responseStatus = 403;
                responseString = "Port is currently used, but could not identify the user, "
                        + "because the VLan ID '" + vlanId + "' is not in the system.\n";
            } else {
            responseString = "Port '" + this.id + "' is currently used by User: '" + exp.getUser() + "'\n";
            responseStatus = 403;
                }
            break;
        }
    }
    
    if(responseStatus == 500) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        responseString = gson.toJson(returnList);
    }
    
    if(responseStatus == 200) {
        responseString = "Node is free to use!";
    }
    
   return Response.status(responseStatus).entity(responseString).build();
    
}
  
}
