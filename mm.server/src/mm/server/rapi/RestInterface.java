package mm.server.rapi;

import mm.server.instance.Instances;
import mm.server.main.ServerData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;


/**
 * This class will called by the controller and execute the equivalent methods of the ganeti class.
 * @author Benedikt Bakker
 *
 */
@Path("/ganeti")
public class RestInterface {

  private Ganeti ga = new Ganeti();
  private HashMap<String, Instances> map = ServerData.getServerList();
  
  public RestInterface() {
    
  }
  
  public RestInterface(HashMap<String, Instances> instanceMap) {
    map = instanceMap;
    System.out.println(map.keySet().toString());
  }
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getInstances() {
    return ga.getInstances();
    
  }
  
  @GET
  @Path("template")
  @Produces(MediaType.APPLICATION_JSON)
  public String getTemplate() {
    JSONObject json;
    List<JSONObject> ret = new ArrayList<JSONObject>();
    String instance = "";
    String[] keys = map.keySet().toArray(new String[map.size()]);
    for ( int i = 0; i < map.size(); i++) {
      System.out.println("KEYS " + keys[i]);
      instance = map.get(keys[i]).toString();
      try {
        json = new JSONObject(instance);
        json.put("id", keys[i]);
        ret.add(json);
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return ret.toString();
  }
  
  @GET
  @Path("{instance}")
  //@Produces(MediaType.APPLICATION_JSON)
  public String getInstanceInfo(@PathParam("instance") String instance) {
    return ga.getInstanceInfo(instance);
  }
  
  @GET
  @Path("{instance}/{param}")
  //@Produces(MediaType.APPLICATION_JSON)
  public String getInstanceInfoParam(@PathParam("instance") String instance,
      @PathParam("param") String param) {
    return ga.getInstanceInfoParam(instance,param);
  }
  
  /**
   * This Method takes the name of the creating instance and the values of one template instance
   *and connect these information to one String.
   * @param json A String of a JSONObject with the name of the creating instance and the value of
   *one template instance.
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public void createInstance(String json) {
    String create = "";
    String name = "";
    try {
      JSONObject param = new JSONObject(json);
      create = map.get(param.getString("template")).toString();
      name = param.getString("name");
      create = create.substring(0, create.length() - 1);
      create = create.concat(",\"instance_name\":\"" + name + "\"}");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    ga.create(create);
  }
  
  @DELETE
  @Path("{instance}")
  public void deleteInstance(@PathParam("instance") String instance) {
    ga.delete(instance);
  }
  
  @POST
  @Path("{instance}")
  @Consumes(MediaType.APPLICATION_JSON)
  public void rebootInstance(@PathParam("instance") String instance, 
      String type) {
    ga.reboot(instance, type);
  }
  
  @PUT
  @Path("{instance}/start")
  @Consumes(MediaType.APPLICATION_JSON)
  public void startInstance(@PathParam("instance") String instance,
      String type) {
    ga.startup(instance,type);
  }
  
  @PUT
  @Path("{instance}/stop")
  @Consumes(MediaType.APPLICATION_JSON)
  public void stopInstance(@PathParam("instance") String instance,
      String type) {
    ga.shutdown(instance,type);
  }
  
  @PUT
  @Path("{instance}/rename")
  @Consumes(MediaType.APPLICATION_JSON)
  public void renameInstance(@PathParam("instance") String instance, 
      String newName) {
    ga.rename(instance, newName);
  }
}