package mm.server.rapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import mm.server.instance.Template;
import mm.server.main.ServerData;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import java.lang.reflect.Type;
import java.net.URI;
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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

/**
 * This class will be called by the RestInterface class and communicates with
 * the RAPI of the Ganeti server via REST.
 * 
 * @author Benedikt Bakker
 *
 */
@Path("/server")
public class Ganeti implements VmServer {
  // benedikt:ben2305@
  private ClientConfig config;
  private Client client;
  private Invocation.Builder builder;
  private WebTarget target;
  private Gson gson = new GsonBuilder().create();

  /**
   * Creating instances of Client and authenticate with user name and password.
   */
  public Ganeti() {
    this.config = new ClientConfig();
    this.client = ClientBuilder.newClient(config);
    HttpAuthenticationFeature feat = HttpAuthenticationFeature.basic(
        "benedikt", "ben2305");
    this.client.register(feat);
  }

  /**
   * This private method will be called by the get Methods and will parse a list
   * with a given parameter.
   * 
   * @param list
   *          is a String a the get request from the ganeti server
   * @param param
   *          is a given parameter, which should be parse the list
   * @return a String of a List with only the names of the Ganeti instances
   */
  private String parseList(String list, String param) {
    List<String> arr = new ArrayList<String>();
    StringBuilder id = new StringBuilder();
    do {
      if (list.startsWith(param + "\":")) {
        list = list.substring(param.length() + 3);
        if (list.startsWith("\"")) {
          list = list.substring(1);
        }
        do {
          id.append(list.charAt(0));
          list = list.substring(1);
        } while (!list.startsWith("\"") && !list.startsWith(","));
        arr.add(id.toString());
        id.delete(0, id.length());
      } else {
        list = list.substring(1);
      }
    } while (!list.isEmpty());
    return arr.toString();
  }

  /**
   * Creates a bridge on the server between an instance and the vlan.
   * 
   * @param bridge
   *          name for the bridge
   * @param ethernet
   *          name of the interface for the vlan
   */
  private void createBridge(String bridge, String ethernet) {
    
  }
  
  /**
   * Returns only the name of all instances on the ganeti server.
   * 
   * @return list of all instances of the server
   */
  @GET
  @Path("ganeti")
  @Produces(MediaType.APPLICATION_JSON)
  public String getInstances() {
    target = client.target(getBaseUri());
    String list = target.path("instances").request().get(String.class);
    return this.parseList(list, "id");
  }

  /**
   * Returns all attributes of an instance.
   * 
   * @param instance
   *          the name of the instance
   * @return a list with all attributes
   */
  @GET
  @Path("ganeti/{instance}")
  public String getInstanceInfo(@PathParam("instance") String instance) {
    target = client.target(getBaseUri());
    String list = target.path("instances").path(instance).request()
          .get(String.class);
    return list;
  }

  /**
   * Returns one attribute of an instance.
   * 
   * @param instance
   *          the name of the instance
   * @param param
   *          the name of the parameter
   * @return a list with the attribute of a given status
   */
  @GET
  @Path("ganeti/{instance}/{param}")
  public String getInstanceInfoParam(@PathParam("instance") String instance,
      @PathParam("param") String param) {
    target = client.target(getBaseUri());
    String list = target.path("instances").path(instance).request()
        .get(String.class);
    return this.parseList(list, param);
  }

  /**
   * Creates an instance on the ganeti server.
   * 
   * Possible HTTP status codes:
   * 
   * <li> 200: The creation of the instance was successful.<\li>
   * <li> 409: The given parameters are incorrect.<\li>
   * 
   * @param param
   *          is the JSONObject with all parameters from the controller to
   *          create an instance.
   *          <li> template: the name of the template which should be loaded
   *          <li> name: the instance name for the ganeti instance
   *          <li> bridge: the name of the bridge interface
   *          <li> size: the size of the disk of the instance
   *          <li> ip: the address for the instance, but can be empty
   * @return the HTTP Response of the ganeti server
   */
  @POST
  @Path("ganeti")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response create(String param) {
    if (param.isEmpty()) {
      return Response.status(409).entity("There are no parameters").build();
    }
    target = client.target(getBaseUri());
    Type type = new TypeToken<HashMap<String, Object>>() {}.getType();
    HashMap<String, Object> create = gson.fromJson(param, type);
    if (create.get("template").toString().isEmpty() || create.get("name").toString().isEmpty()
        || create.get("bridge").toString().isEmpty() || create.get("size").toString().isEmpty()) {
      return Response.status(409).entity(
              "There is either no template name, no instance name, no bridge or no disks size")
              .build();
    }
    Template vm = ServerData.getTemplateList().get(create.get("template"));
    vm.removeLists();
    vm.removeAttribute("id");
    vm.removeAttribute("server");
    int vlanId = Integer.parseInt(create.get("bridge").toString().replace(".0", ""));
    String name = create.get("name").toString();
    String ethernet = "eth0." + vlanId;
    String bridge = "br" + name;
    this.createBridge(bridge, ethernet);
    vm.setNicLink(bridge);
    if (create.containsKey("ip")) {
      vm.setNicIp(create.get("ip").toString());
    }
    vm.setAttribute("instance_name", name);
    vm.setDisksSize(Integer.parseInt(create.get("size").toString().replace(".0", "")));
    vm.setLists();
    builder = target.path("instances").request()
        .header("Content-Type", "application/json");
    builder.accept("application/json").post(
        Entity.json(gson.toJson(vm.getAttribute())));
    return Response.ok().build();
  }

  /**
   * Starts a given instance.
   * 
   * @param instance
   *          name of the instance that will be started
   * @param type
   *          must be a JSONObject with settings, but can be empty
   * @return the HTTP Response of the ganeti server
   */
  @PUT
  @Path("ganeti/{instance}/start")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response startup(@PathParam("instance") String instance, String type) {
    target = client.target(getBaseUri());
    if (this.getInstances().contains(instance)) {
      builder = target.path("instances").path(instance).path("startup")
          .request().header("Content-Type", "application/json");
      builder.accept("application/json").put(Entity.json(type));
      return Response.ok().build();
    } else {
      return Response.status(400).entity("The given instance is not on the server.").build();
    }
  }

  /**
   * Stops a given instance.
   * 
   * @param instance
   *          name of the VM which should be stopped
   * @param type
   *          must be a JSONObject with settings, but can be empty
   * @return the HTTP Response of the ganeti server
   */
  @PUT
  @Path("ganeti/{instance}/stop")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response shutdown(@PathParam("instance") String instance, String type) {
    target = client.target(getBaseUri());
    if (this.getInstances().contains(instance)) {
      builder = target.path("instances").path(instance).path("shutdown")
          .request().header("Content-Type", "application/json");
      builder.accept("application/json").put(Entity.json(type));
      return Response.ok().build();
    } else {
      return Response.status(400).entity("The given instance is not on the server.").build();
    }
  }

  /**
   * Deletes a given Instance from the server.
   * 
   * @param instance
   *          name of the VM which should be deleted
   * @return the HTTP Response of the ganeti server
   */
  @DELETE
  @Path("ganeti/{instance}")
  public Response delete(@PathParam("instance") String instance) {
    target = client.target(getBaseUri());
    if (this.getInstances().contains(instance)) {
      target.path("instances").path(instance).request().delete();
      return Response.ok().build();
    } else {
      return Response.status(400).entity("The given instance is not on the server.").build();
    }
  }

  /**
   * Reboots a given instance.
   * 
   * @param instance
   *          name of the VM which should reboot
   * @param type
   *          must be a JSONObject with either soft, hard or full for the type
   *          of rebooting
   * @return the HTTP Response of the ganeti server
   */
  @POST
  @Path("ganeti/{instance}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response reboot(@PathParam("instance") String instance, String type) {
    target = client.target(getBaseUri());
    if (!type.equals("soft") || !type.equals("hard") || !type.equals("full")) {
      return Response.status(409).entity("The given type is wrong").build();
    }
    if (this.getInstances().contains(instance)) {
      builder = target.path("instances").path(instance).path("reboot")
          .request().header("Content-Type", "application/json");
      builder.accept("application/json").post(Entity.json(type));
      return Response.ok().build();
    } else {
      return Response.status(400).entity("The given instance is not on the server.").build();
    }
  }

  /**
   * Renames a given instance with a new name.
   * 
   * @param instance
   *          name of the VM which should be renamed
   * @param newName
   *          of the given VM
   * @return the HTTP Response
   */
  @PUT
  @Path("ganeti/{instance}/rename")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response rename(@PathParam("instance") String instance, String newName) {
    if (newName.isEmpty()) {
      return Response.status(409).entity("There is no newName").build();
    }
    target = client.target(getBaseUri());
    if (this.getInstances().contains(instance)) {
      builder = target.path("instances").path(instance).path("rename")
          .request().header("Content-Type", "application/json");
      builder.accept("application/json").put(Entity.json(newName));
      return Response.ok().build();
    } else {
      return Response.status(400).entity("The given instance is not on the server.").build();
    }
  }
  
  private static URI getBaseUri() {
    return UriBuilder.fromUri("http://localhost:5080/2")
        .build();
  }
}