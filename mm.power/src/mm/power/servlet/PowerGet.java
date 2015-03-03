package mm.power.servlet;

import java.util.LinkedList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mm.power.main.PowerData;
import mm.power.modeling.*;

@Path("/get")
public class PowerGet {
	
	 private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
  /**
   * Returns information about the power source with the given ID.
   * @param id ID of the PowerSupply and the socket/port number e.g "PowerSupply;1;end"
   * @return response A response Object with the requested information
   */
  @GET
  @Path("{incoming}")
  @Produces({MediaType.TEXT_PLAIN, "json/application"})
    public Response getById(@PathParam("incoming") String incoming) {

	  LinkedList<PowerSource> returnList = new LinkedList<PowerSource>();
	  PowerSupply ps;
	  boolean status;
	  String id, error = "none";
	  int socket, responseStatus = 200;
	  //String incoming = "TESTAEHOME#1;1;TESTAEHOME#1;2;TESTAEHOME#1;3;notfound;2;end";
	  
	  String[] parts = incoming.split(";");
	  

	  if(!(parts[parts.length-1].equals("end"))) {
		  String responseString = "Transfer not Complete or wrong format in PowerGet GetById. Message : " + incoming;
		  return Response.status(500).entity(responseString).build();
	  }
	  
	  
	  for (int i = 0; i < parts.length - 2; i = i + 2) {
		  
		  id = parts[i];
		  socket = Integer.parseInt(parts[i+1]);
		  
		  if(!(PowerData.exists(id))) {
			  responseStatus = 404;
			  error = id + " does not exist";
			  returnList.add(new PowerSource(id, false, error));
		 
		  } else {
			  ps = PowerData.getById(id);
			  
			  Response r = ps.status(socket);
			  
			  if(r.getStatus() == 200){
				  
				  if(((String)r.getEntity()).equals("0")){
					  status = false;
				  } else {
					  status = true;
				  }
				  
				  returnList.add(new PowerSource(id + ";" + socket, status));
			  } else {
				  responseStatus = 500;
				  error = (String) r.getEntity();
				  returnList.add(new PowerSource(id + ";" + socket, false, error));
			  }
		
		  }
		
	}
	  
	String json = gson.toJson(returnList);
    return Response.status(responseStatus).entity(json).build();

  }

  
  

 


}
