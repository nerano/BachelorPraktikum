package mm.server.rapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mm.server.instance.Instance;
import mm.server.instance.Template;
import mm.server.main.ServerData;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
  private static final String url = "http://localhost:5080/2";
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
   * with a given param.
   * 
   * @param list
   *          is a String a the get request from the ganeti server.
   * @param param
   *          is a given param, which should be parse the list.
   * @return a String of a List with only the names of the Ganeti instances.
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
   * Returns only the name of all instances on the ganeti server.
   * 
   * @return list of all instances of the server.
   */
  @GET
  @Path("ganeti")
  @Produces(MediaType.APPLICATION_JSON)
  public String getInstances() {
    target = client.target(url);
    String list = "";
    try {
      list = target.path("instances").request().get(String.class);
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
    return this.parseList(list, "id");
  }

  /**
   * Returns all attributes of an instance.
   * 
   * @param instance
   *          the name of the instance,
   * @return a list with all attributes.
   */
  @GET
  @Path("ganeti/{instance}")
  public String getInstanceInfo(@PathParam("instance") String instance) {
    target = client.target(url);
    String list = "";
    try {
      list = target.path("instances").path(instance).request()
          .get(String.class);
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
    return list;
  }

  /**
   * Returns one attribute of an instance.
   * 
   * @param instance
   *          the name of the instance.
   * @param param
   *          the name of the parameter.
   * @return a list with the attribute of a given status.
   */
  @GET
  @Path("ganeti/{instance}/{param}")
  public String getInstanceInfoParam(@PathParam("instance") String instance,
      @PathParam("param") String param) {
    target = client.target(url);
    String list = "";
    try {
      list = target.path("instances").path(instance).request()
          .get(String.class);
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
    return this.parseList(list, param);
  }

  /**
   * Creates an instance on the ganeti server.
   * 
   * 
   * @param param
   *          is the JSONObject with all parameters from the controller to
   *          create an instance.
   * @return the HTTP Response of the ganeti server.
   */
  @POST
  @Path("ganeti")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response create(String param) {
    target = client.target(url);
    Template temp = null;
    Response resp = null;
    if (param.isEmpty()) {
      return Response.status(409).entity("There are no parameters").build();
    }
    if (!param.contains("template") || !param.contains("name")
        || !param.contains("bridge") || !param.contains("size")) {
      return Response
          .status(409)
          .entity(
              "There is either no template name or no instance name or no bridge "
                  + "or no disks size").build();
    }
    try {
      JSONObject create = new JSONObject(param);
      temp = ServerData.getTemplateList().get(create.get("template"));
      String instance = gson.toJson(temp);
      Instance vm;
      vm = gson.fromJson(instance, Instance.class);
      vm.removeLists();
      vm.removeAttribute("id");
      vm.removeAttribute("server");
      vm.setNicLink(create.getString("bridge"));
      if (create.has("ip")) {
        vm.setNicIp(create.getString("ip"));
      }
      vm.setAttribute("instance_name", create.getString("name"));
      vm.setDisksSize(create.getInt("size"));
      vm.setLists();
      builder = target.path("instances").request()
          .header("Content-Type", "application/json");
      resp = builder.accept("application/json").post(
          Entity.json(gson.toJson(vm.getAttribute())));
    } catch (JSONException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return resp;
  }

  /**
   * Starts a given instance.
   * 
   * @param instance
   *          name of the instance that will be started.
   * @param type
   *          must be a JSONObject with settings, but can be emtpy.
   * @return the HTTP Response of the ganeti server.
   */
  @PUT
  @Path("ganeti/{instance}/start")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response startup(@PathParam("instance") String instance, String type) {
    target = client.target(url);
    Response resp = null;
    try {
      builder = target.path("instances").path(instance).path("startup")
          .request().header("Content-Type", "application/json");
      resp = builder.accept("application/json").put(Entity.json(type));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return resp;
  }

  /**
   * Stops a given instance.
   * 
   * @param instance
   *          name of the VM which should be stopped.
   * @param type
   *          must be a JSONObject with settings, but can be empty.
   * @return the HTTP Response of the ganeti server.
   */
  @PUT
  @Path("ganeti/{instance}/stop")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response shutdown(@PathParam("instance") String instance, String type) {
    target = client.target(url);
    Response resp = null;
    try {
      builder = target.path("instances").path(instance).path("shutdown")
          .request().header("Content-Type", "application/json");
      resp = builder.accept("application/json").put(Entity.json(type));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return resp;
  }

  /**
   * Deletes a given Instance from the server.
   * 
   * @param instance
   *          name of the VM which should be deleted.
   * @return the HTTP Response of the ganeti server.
   */
  @DELETE
  @Path("ganeti/{instance}")
  public Response delete(@PathParam("instance") String instance) {
    target = client.target(url);
    Response resp = null;
    try {
      resp = target.path("instances").path(instance).request().delete();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return resp;
  }

  /**
   * Reboots a given instance.
   * 
   * @param instance
   *          name of the VM which should reboot.
   * @param type
   *          must be a JSONObject with either soft, hard or full for the type
   *          of rebooting.
   * @return the HTTP Response of the ganeti server.
   */
  @POST
  @Path("ganeti/{instance}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response reboot(@PathParam("instance") String instance, String type) {
    if (!type.equals("soft") || !type.equals("hard") || !type.equals("full")) {
      return Response.status(409).entity("The given type is wrong").build();
    }
    target = client.target(url);
    Response resp = null;
    try {
      builder = target.path("instances").path(instance).path("reboot")
          .request().header("Content-Type", "application/json");
      resp = builder.accept("application/json").post(Entity.json(type));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return resp;
  }

  /**
   * Renames a given instance with a new name.
   * 
   * @param instance
   *          name of the VM which should be renamed.
   * @param newName
   *          of the given VM.
   * @return the HTTP Response.
   */
  @PUT
  @Path("ganeti/{instance}/rename")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response rename(@PathParam("instance") String instance, String newName) {
    if (newName.isEmpty()) {
      return Response.status(409).entity("There is no newName").build();
    }
    target = client.target(url);
    Response resp = null;
    try {
      builder = target.path("instances").path(instance).path("rename")
          .request().header("Content-Type", "application/json");
      resp = builder.accept("application/json").put(Entity.json(newName));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return resp;
  }
}