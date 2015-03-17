package mm.server.rapi;

import mm.server.instance.Instance;
import mm.server.instance.Template;
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
import javax.ws.rs.core.Response;


/**
 * This class will called by the controller and execute the equivalent methods of the ganeti class.
 * @author Benedikt Bakker
 *
 */
@Path("/ganeti")
public class RestInterface {

  private Ganeti ga = new Ganeti();
  private HashMap<String, Template> map = ServerData.getTemplateList();
  
  /**
   * Calls the method which returns a list of instances on the server.
   * @return String with all instance names.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getInstances() {
    return ga.getInstances();
    
  }
  
  /**
   * Calls the method which returns a String of all instance templates with their
   *attributes.
   * @return String with all instance names and attributes.
   */
  @GET
  @Path("template")
  @Produces(MediaType.APPLICATION_JSON)
  public String getTemplate() {
    JSONObject json;
    List<JSONObject> ret = new ArrayList<JSONObject>();
    String instance = "";
    String[] keys = map.keySet().toArray(new String[map.size()]);
    for ( int i = 0; i < map.size(); i++) {
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
  
  /**
   * Calls the method which returns a String with all attributes and their values of an
   *instance on the ganeti server.
   * @param instance the name of the instance.
   * @return String with all attributes.
   */
  @GET
  @Path("{instance}")
  //@Produces(MediaType.APPLICATION_JSON)
  public String getInstanceInfo(@PathParam("instance") String instance) {
    return ga.getInstanceInfo(instance);
  }
  
  /**
   * Calls the method which returns a String with one given attribute and it's value
   *of an given instance on the ganeti server.
   * @param instance the name of the instance.
   * @param param the attribute of an instance.
   * @return the value of an attribute of an instance.
   */
  @GET
  @Path("{instance}/{param}")
  //@Produces(MediaType.APPLICATION_JSON)
  public String getInstanceInfoParam(@PathParam("instance") String instance,
      @PathParam("param") String param) {
    return ga.getInstanceInfoParam(instance,param);
  }
  
  /**
   * Takes the name of the creating instance and the values of one template instance
   *and connect these information to one String. Then it calls the method which creates an instance
   *on the ganeti server.
   * @param json A String of a JSONObject with the name of the creating instance and the value of
   *one template instance.
   * @return the HTTP Response of the ganeti server.
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createInstance(String json) {
    Template temp = null;
    String create = "";
    if (json.isEmpty()) {
      return Response.status(409).entity("There are no parameters").build();
    }
    if (!json.contains("template") || !json.contains("name") || !json.contains("bridge") 
        || !json.contains("ip") || !json.contains("size")) {
      return Response.status(409)
          .entity("There is either no template name or no instance name or no bridge or no ip"
              + "or no disks size").build();
    }
    try {
      JSONObject param = new JSONObject(json);
      temp = map.get(param.get("template"));
      Instance vm = new Instance();
      String tmp = temp.getJson().toString();
      vm.setJson(new JSONObject(tmp));
      vm.setDisk(temp.getDisk());
      vm.setNic(temp.getNic());
      vm.setNics("link", param.getString("bridge"));
      vm.setNics("ip", param.getString("ip"));
      vm.setName(param.getString("name"));
      vm.setDisksSize(param.getInt("size"));
      create = vm.toString();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    System.out.println(create);
    //return ga.create(create);
    return Response.ok().build();
  }
  
  /**
   * Calls the method which deletes an instance on the ganeti server.
   * @param instance the name of instance which should be deleted.
   * @return the HTTP Response of the ganeti server.
   */
  @DELETE
  @Path("{instance}")
  public Response deleteInstance(@PathParam("instance") String instance) {
    return ga.delete(instance);
  }
  
  /**
   * Calls the method which reboots an instance on the ganeti server.
   * @param instance the name of the instance.
   * @param type a JSONObject String with type or an empty String.
   * @return the HTTP Response of the ganeti server.
   */
  @POST
  @Path("{instance}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response rebootInstance(@PathParam("instance") String instance, 
      String type) {
    return ga.reboot(instance, type);
  }
  
  /**
   * Calls the method which starts an instance on the ganeti server.
   * @param instance the name of the instance.
   * @param type a JSONObject String or an empty String.
   * @return the HTTP Response of the ganeti server.
   */
  @PUT
  @Path("{instance}/start")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response startInstance(@PathParam("instance") String instance,
      String type) {
    return ga.startup(instance,type);
  }
  
  /**
   * Calls the method which stops an instance on the ganeti server.
   * @param instance the name of the instance.
   * @param type a JSONObject String or an empty String.
   * @return the HTTP Response of the ganeti server.
   */
  @PUT
  @Path("{instance}/stop")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response stopInstance(@PathParam("instance") String instance,
      String type) {
    return ga.shutdown(instance,type);
  }
  
  /**
   * Calls the method which renames an instance on the ganeti server.
   * @param instance the name of the instance.
   * @param newName a JSONObject String with new_name as key and a non empty String as value.
   */
  @PUT
  @Path("{instance}/rename")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response renameInstance(@PathParam("instance") String instance, 
      String newName) {
    String resp = "";
    if (newName.isEmpty() || !newName.contains("new_name")) {
      resp = "There is no new_name";
      return Response.status(409).entity(resp).build();
    }
    return ga.rename(instance, newName);
  }
}