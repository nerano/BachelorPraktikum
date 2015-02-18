package mm.net.servlet;

import java.util.LinkedList;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mm.net.main.NetData;
import mm.net.modeling.VLan;
import mm.net.exceptions.TransferNotCompleteException;

@Path("/get")
public class NetGet {

	private Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("{id}")
	public Response getVLanById(@PathParam("id") int id) {

		
		String responseString;

		if (!(NetData.exists(id))) {
			responseString = "404, VLan " + id + " not found!";
			return Response.status(404).entity(responseString).build();
		}

		VLan vlan = NetData.getById(id);

		responseString = gson.toJson(vlan);
		return Response.status(200).entity(responseString).build();

	}
	/**
	 * 
	 * @param incoming Only 
	 * @return
	 * @throws TransferNotCompleteException
	 */
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("{incoming}")
	public Response getMultipleVLansById(@PathParam("incoming") String incoming) throws TransferNotCompleteException {

		LinkedList<VLan> returnList = new LinkedList<VLan>();
		String responseString;
		String[] parts = incoming.split(";");
		int id;
		
		if(!(parts[parts.length-1].equals("end"))){
			 throw new TransferNotCompleteException("Transfer not Complete in NetGet GetMultipleVLansById. Message : " + incoming);
		  }
		
		for (int i = 0; i < parts.length - 1; i++) {
			
			id = Integer.parseInt(parts[i]);
			
			if (!(NetData.exists(id))) {
				responseString = "404, VLan " + id + " not found!";
				return Response.status(404).entity(responseString).build();
			}
			
			returnList.add(NetData.getById(id));
		

		}
		
		
		responseString = gson.toJson(returnList);
		return Response.status(200).entity(responseString).build();

	}
}