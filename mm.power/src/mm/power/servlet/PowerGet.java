package mm.power.servlet;

import java.io.IOException;
import java.util.LinkedList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mm.power.exceptions.EntryDoesNotExistException;
import mm.power.exceptions.TransferNotCompleteException;
import mm.power.main.PowerData;
import mm.power.modeling.*;

@Path("/get")
public class PowerGet {
	
	 private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	
  /**
   * Returns status-information about all known nodes in the network.
   * The information contains the node, the type of the component and the power 
   * status of the component[0/1]. 
   * @return response A Response object with the information about the power status
 * @throws EntryDoesNotExistException 
 * @throws TransferNotCompleteException 
 * @throws IOException 
   */
  @GET
  @Produces(MediaType.TEXT_PLAIN)
    public Response getAll() throws IOException, TransferNotCompleteException, EntryDoesNotExistException {

    Response response = testGet();
    
    return response;

  }
  /**
   * Returns information about the power source with the given ID.
   * @param id ID of the PowerSupply and the socket/port number e.g "PowerSupply;1;end"
   * @return response A response Object with the requested information
 * @throws TransferNotCompleteException 
 * @throws EntryDoesNotExistException 
 * @throws IOException 
   */
  @GET
  @Path("{incoming}")
  @Produces({MediaType.TEXT_PLAIN, "json/application"})
    public Response getById(@PathParam("incoming") String incoming) throws TransferNotCompleteException, IOException, EntryDoesNotExistException {

	  LinkedList<PowerSource> returnList = new LinkedList<PowerSource>();
	  PowerSupply ps;
	  boolean status;
	  String id;
	  int socket;
	  //String incoming = "TESTAEHOME#1;1;TESTAEHOME#1;2;TESTAEHOME#1;3;notfound;2;end";
	  
	  String[] parts = incoming.split(";");
	  

	  if(!(parts[parts.length-1].equals("end"))){
		  throw new TransferNotCompleteException("Transfer not Complete in PowerGet GetById. Message : " + incoming);
	  }
	  
	  
	  for (int i = 0; i < parts.length - 2; i = i + 2) {
		  
		  id = parts[i];
		  
		  if(!(PowerData.exists(id))){
			  return Response.status(404).entity("404, PowerSource '" + id + "' not found").build();
		  }
		  
		  socket = Integer.parseInt(parts[i+1]);
		  ps = PowerData.getById(id);
		  
		if(ps.status(socket).equals("0")){
			status = false;
	    } else {
	    	status = true;
	    }
		
		returnList.add(new PowerSource(id + ";" + socket, status));
	}
	  
	String json = gson.toJson(returnList);
	Response response = Response.status(200).entity(json).build();
    return response;

  }

  
  public Response testGet() throws IOException, TransferNotCompleteException, EntryDoesNotExistException{
      
	  LinkedList<PowerSource> returnList = new LinkedList<PowerSource>();
	  PowerSupply ps;
	  boolean status;
	  String id;
	  int socket;
	  String incoming = "TESTAEHOME#1;1;TESTAEHOME#1;2;TESTAEHOME#1;3;notfound;2;end";
	  
	  String[] parts = incoming.split(";");
	  

	  if(!(parts[parts.length-1].equals("end"))){
		  throw new TransferNotCompleteException("Transfer not Complete in PowerGet GetById. Message : " + incoming);
	  }
	  
	  
	  for (int i = 0; i < parts.length - 2; i = i + 2) {
		  
		  id = parts[i];
		  socket = Integer.parseInt(parts[i+1]);
		  ps = PowerData.getById(id);

		  if(!(PowerData.exists(id))){
			  return Response.status(404).entity("404, PowerSource '" + id + "' not found").build();
		  }
		  
		if(ps.status(socket).equals("0")){
			status = false;
	    } else {
	    	status = true;
	    }
		
		returnList.add(new PowerSource(id + ";" + socket, status));
	}
	  
	String json = gson.toJson(returnList);
	Response response = Response.status(200).entity(json).build();
    return response;
      
  }

  
  
  /** @GET
  @Path("/test")
  public String getAlll() {
      
      String powerString = testGet();
      
      Gson gson = new Gson();
      
      LinkedList<Node> nodeList = new LinkedList<Node>();
      
      Type type = new TypeToken<LinkedList<Node>>(){}.getType();
      
      nodeList = gson.fromJson(powerString, type);
    
      String ss;
      
      ss = nodeList.get(0).getId();
      
      
      
      return ss;
      
        
        
  } **/



}
