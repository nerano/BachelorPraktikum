package mm.net.rest;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;

import com.google.gson.Gson;

import mm.net.modeling.VLan;


@Path ("/get")
public class NetGet {
  @GET
  @Path("{id}")
  public String getById(@PathParam("id") int id) {
        
  VLan vlan = new VLan(123);
  
  LinkedList<String> tmp = new LinkedList<String>();
  
  tmp.add("ID1.6");
  tmp.add("ID2.8");
  
  vlan.addPorts(tmp);
  
  Gson gson = new Gson();
  String json = gson.toJson(vlan);
  
  return json;
        
        
    }





}
