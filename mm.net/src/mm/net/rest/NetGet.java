package mm.net.rest;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mm.net.modeling.VLan;


@Path ("/get")
public class NetGet {
 

  @GET
  @Path("{id}")
  public String getById(@PathParam("id") int id) {
        
      
  
  VLan vlan = mm.net.implementation.testGetFromNetHardware.get(id);
  
  Gson gson = new GsonBuilder().setPrettyPrinting().create();
  String json = gson.toJson(vlan);
  
  if(json.charAt(0) != '{'){
      json = "404, VLan not found!";
  }
  
  return json;
        
        
    }





}
