package mm.server.rapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mm.server.instance.Template;
import mm.server.main.ServerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This class is the interface for the controller to get and update the template
 * list.
 * 
 * @author Benedikt Bakker
 *
 */
@Path("/server")
public class GetTemplate {

  private HashMap<String, Template> map = ServerData.getTemplateList();
  private Gson gson = new GsonBuilder().setPrettyPrinting().create();

  /**
   * Updates the HashMap of templates when there are changes in the XML file.
   * 
   * @return the HTTP Response
   */
  @PUT
  @Path("template")
  @Consumes(MediaType.TEXT_PLAIN)
  public Response updateTemplateList() {
    map = ServerData.updateTemplateList();
    return Response.ok().entity("Template wurde geupdated.").build();
  }

  /**
   * Calls the method which returns a List of all instance templates with their
   * attributes.
   * 
   * @return List with all instance names and attributes
   */
  @GET
  @Path("template")
  @Produces(MediaType.APPLICATION_JSON)
  public String getTemplate() {
    List<String> ret = new ArrayList<String>();
    String[] keys = map.keySet().toArray(new String[map.size()]);
    for (int i = 0; i < map.size(); i++) {
      ret.add(gson.toJson(map.get(keys[i]).getAttribute()));
    }
    return ret.toString();
  }
}