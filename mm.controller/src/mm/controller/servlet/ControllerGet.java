package mm.controller.servlet;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mm.controller.auth.WebAuthTest;
import mm.controller.exclusion.NoStatusNodeStrat;
import mm.controller.main.ControllerData;
import mm.controller.modeling.Experiment;
import mm.controller.modeling.NodeObjects;
import mm.controller.modeling.PowerSource;
import mm.controller.net.ControllerNetGet;
/**
 * Class for all GET methods in the controller servlet
 * 
 */
@Path("/get")
@Singleton
public class ControllerGet {
    
    @GET
    @Path("/test")
    public Response test(@Context HttpHeaders header, @HeaderParam("testHeaderKey") String auth) {
        
        
        header.getHeaderString("testHeaderKey");
        
        System.out.println("GETHEADERSTRING: " + header.getHeaderString("testHeaderKey"));
        System.out.println("HEADERPARAM: " + auth);
        
        
        return Response.ok("HEADERTEST").header("testHeaderKey", "testHeaderValue").build();
    }

	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	/**
	 * Returns all nodes.
	 * <p>
	 * Returns all nodes which are known to the controller; these are all nodes specified 
	 * in the XML-file and parsed on startup. No dynamic information is serialized, only static.
	 * E.g. the VLAN and power status is not serialized, but the location.
	 * 
	 * The Response Object holds a JSON representation of a list, which contains all nodes.
	 *
	 * @return a Response Object with status code 200
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
	@Path("/nodes")
	public Response getAllNodes() {
	  //if () {
	    
		Gson gson = new GsonBuilder().setExclusionStrategies (new NoStatusNodeStrat())  
				 .setPrettyPrinting().create();
		// TODO TESTEN
		LinkedList<NodeObjects> list = ControllerData.getAllNodesAsList();
		
		String responseString = gson.toJson(list);
		// responseString = ControllerData.getAllNodesAsList().toString();
		return Response.status(200).entity(responseString).build();
	  //}
	}
	
	   /**
     * 
     * @return
     */
    @GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Path("/configs")
    public Response getConfigs() {
        
       String responseString = gson.toJson(ControllerData.getAllConfigs());
        
       return Response.ok(responseString).build();
        
    }
    
    /**
     * 
     * @return
     */
    @GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Path("/ports")
    public Response getPorts() {
      
      String responseString = gson.toJson(ControllerData.getAllWPorts());
      
      return Response.ok(responseString).build();
      
    }
    
    /**
     * 
     * @param exp
     * @return
     * @throws UnsupportedEncodingException
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/status/{exp}")
    public Response getNodePowerStatus(@PathParam("exp") String exp) throws UnsupportedEncodingException {
        
        Experiment experiment = ControllerData.getExpById(exp);
        
        LinkedList<PowerSource> psrc = experiment.status();
        
        experiment.updateNodeStatusPower(psrc);
        
        return Response.ok(gson.toJson(experiment)).build();
    }
    
	/**
	 * Returns an experiment.
	 * <p>
	 * Returns an experiment with the given ID in JSON format, the ID is passed through the URI
	 * The Response Objects holds the JSON in the message body
	 * URI: baseuri:port/mm.controller/rest/get/exp/{id}
	 * 
	 * Possible HTTP status codes:
	 * 
	 * <li> 200: The requested experiment is located in the message body.
	 * <li> 401: 401: Unauthorized: Header does not contain a sessionId. 
	 * <li> 404: The requested experiment with the given ID does not exist.
	 * @param id  Identifier of the experiment
	 * @return a Response Object
	 */
	@GET
	@Produces({ "json/application", "text/plain" })
	@Path("/exp/{id}")
	public Response getExpById(@HeaderParam("sessionId") String sessionId, @PathParam("id") String id) {

		String responseString;
		WebAuthTest auth = new WebAuthTest();
    Response response = auth.checkSession(sessionId);
    if (sessionId != null) {
      if (response.getStatus() == 200) {
        if (!(ControllerData.existsExp(id))) {
          responseString = "404, Experiment not found";
          response = Response.status(404).entity(responseString).build();
          return response;
        }

        Gson gson = new GsonBuilder()/* .setExclusionStrategies(new NoStatusNodeStrat()) */
									 .setPrettyPrinting().create();

        /*if (auth.getUserRole(sessionId).readEntity(String.class).equals(exp.getUser())) {*/
          responseString = gson.toJson(ControllerData.getExpById(id));
        /*}*/
        response = Response.status(200).entity(responseString).build();

        return response;
      }
      return response;
    } else {
      return Response.status(401).entity("No SessionId contained!").build();
    }
	}
	
	/**
	 * 
	 * @param net
	 * @return
	 */
	@GET
    @Produces({ "json/application", "text/plain" })
    @Path("/staticConsistency/{net}")
	public Response getStaticConsistency(@PathParam("net") String net) {
	    return Response.ok( ControllerNetGet.staticConsistency(net)).build();
	}
	
	/**
     * 
     * @param net
     * @return
     */
    @GET
    @Produces({ "json/application", "text/plain" })
    @Path("/expConsistency/{id}")
    public Response getExpConsistency(@PathParam("id") String id) {
        
        Experiment exp = ControllerData.getExpById(id);
        
        if(exp == null) {
            return Response.status(404).entity("404, Experiment not found!").build();
        }
        
        String consistency = exp.isConsistent();
        
        return Response.ok(consistency).build();
    }
	
	
	/**
	 * Returns a list of all experiments.
	 * <p>
	 * Returns a list of all currently existing experiments in JSON format. 
	 * No input Parameters are required.
	 * URI: <code>baseuri:port/mm.controller/rest/get/exp</code>
	 * <p>
	 * Possible HTTP status codes:
	 * <li> 200: Header contains valid sessionId.
	 * <li> 401: Unauthorized: Header does not contain a sessionId. 
	 * 
	 * @return a Response Object with the JSON in the message body.
	 */
	@GET
	@Produces({ "json/application", "text/plain" })
	@Path("/exp")
	public Response getAllExp(@HeaderParam("sessionId") String sessionId){
		/**
		 * test case if the role management works.
		 **/
	 
	  System.out.println(sessionId);
	  
	    
	  WebAuthTest auth = new WebAuthTest();
	  Response response = auth.checkSession(sessionId);
	  if (sessionId != null) {
	    if(response.getStatus() == 200) {
	      String user = auth.getUser(sessionId).readEntity(String.class);
	      if (auth.getUserRole(sessionId).readEntity(String.class).equals("admin")) {
	        return Response.ok(gson.toJson(ControllerData.getAllExp())).build();
	      } else {
	          return this.getExpByUser(user);
	      }
	    } else {
	        return response;
	    }
	  } else {
	    return Response.status(401).encoding("No SessionId contained!").build();
	  }
	}
	
	 /**
   * Returns a list of all experiments of a user.
   * <p>
   * Returns a list of all currently existing experiments for a user in JSON format. 
   * No input Parameters are required. The user name is stored in a header received
   * from the webinterface.
   * URI: baseuri:port/mm.controller/rest/get/exp
   * 
   * @return a Response Object with the JSON in the message body.
   */
	private Response getExpByUser(String user) {
	  LinkedList<Experiment> expList = ControllerData.getAllExp();
	  LinkedList<Experiment> temp = new LinkedList<Experiment>();
	  String responseString;
	  for (Experiment exp : expList) {
	    if (exp.getUser().equals(user)) {
	      temp.add(exp);
	    }
	  }
	  
	  if (temp.isEmpty()) {
	    responseString = "";
	  } else {
	    responseString = gson.toJson(temp);
	  }
	  return Response.ok(responseString).build();
	}

	


}
