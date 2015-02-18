package mm.power.servlet;

import java.io.IOException;
import java.net.URLDecoder;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mm.power.exceptions.EntryDoesNotExistException;
import mm.power.exceptions.TransferNotCompleteException;
import mm.power.main.PowerData;
import mm.power.modeling.PowerSupply;


@Path("/put")
public class PowerPut {

	/**
	 * 
	 * @param incoming
	 * @return
	 * @throws IOException
	 * @throws EntryDoesNotExistException
	 * @throws TransferNotCompleteException
	 */
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("/turnOn")
	public Response turnOn(String incoming) throws IOException, EntryDoesNotExistException, TransferNotCompleteException{
		
		System.out.println("IN POWER SERVICE KAM AN " + incoming);
		
		//URLDecoder.decode(incoming, "UTF-8")
		
		String[] parts = incoming.split(";");
		String id;
		StringBuffer buffer = new StringBuffer();
		int socket;
		PowerSupply ps;
		boolean check = true;
		
		if(!(parts[parts.length-1].equals("end"))){
			  
			// throw new TransferNotCompleteException("Transfer not Complete in PowerPut turnOn. Message : " + incoming);
			 return Response.status(400).entity("Messagetransfer not complete").build();
		}
		
		
		
		for(int i = 0; i < parts.length - 2; i = i + 2) {
			
			 id = parts[i];
			 socket = Integer.parseInt(parts[i+1]);
			
			if(!(PowerData.exists(id))){
				return Response.status(404).entity("404, PowerSource '" + id + "' not found").build();
			}
			
			ps = PowerData.getById(id);
			
		
			if(!ps.turnOn(socket)){
				buffer.append(" ").append(id).append(socket);
				check = false;
			} 
			
			
		}
		
		if(check){
			return Response.status(200).entity("All PowerSources successful turned on").build();
		} else {
			return Response.status(500).entity("Following PowerSources could not be turned on" + buffer.toString()).build();
		}
		
		
	}
	
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("/turnOff")
	public Response turnOff(String incoming) throws TransferNotCompleteException, IOException, EntryDoesNotExistException{
		
		String[] parts = incoming.split(";");
		String id;
		int socket;
		
		if(!(parts[parts.length-1].equals("end"))){
			  throw new TransferNotCompleteException("Transfer not Complete in PowerPut turnOff. Message : " + incoming);
		  }
		
		PowerSupply ps;
		
		for(int i = 0; i < parts.length - 2; i = i + 2) {
			
			 id = parts[i];
			 socket = Integer.parseInt(parts[i+1]);
			
			if(!(PowerData.exists(id))){
				return Response.status(404).entity("404, PowerSource '" + id + "' not found").build();
			}
			
			ps = PowerData.getById(id);
			ps.turnOff(socket);
		

		}
		
		return Response.status(200).entity("All PowerSources successful turned off").build();
	}
		
}
