package mm.net.servlet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mm.net.main.NetData;
import mm.net.modeling.NetComponent;
import mm.net.modeling.VLan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/put")
public class NetPut {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    /**
     * Expects a VLan in JSON format and sets the given ports as Trunkports with the given ID
     * 
     * @param incoming
     * @return
     */
	@PUT
	@Path("/setTrunkPort")
	@Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	public Response setTrunkPort(String incoming) {
		
		VLan vlan = gson.fromJson(incoming, VLan.class);
		System.out.println(incoming);
	    Response response;
		
		NetComponent nc;
		
		HashMap<String, LinkedList<Integer>> map = portListToHashMap(vlan.getPortList());
		
		for (Entry<String, LinkedList<Integer>>  entry : map.entrySet()) {
            
		    String ncId = entry.getKey();
		    LinkedList<Integer> portList = entry.getValue();
		    
		    nc = NetData.getNetComponentById(ncId);
		    nc.start();
		    response = nc.setTrunkPort(portList, vlan.getId(), vlan.getName());
		    System.out.println("STATUS: " + response.getStatus());
		    System.out.println("ENTITY: " + (String) response.getEntity());
		    nc.stop();
        }
		
		return Response.ok().build();
	}
	
	
	
	
	/**
	 * 
	 * @param incoming
	 * @return
	 */
	@PUT
    @Path("/addTrunkPort")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	public Response addTrunkPort(String incoming) {
	    
	    VLan vlan = gson.fromJson(incoming, VLan.class);
	    Response response;
        NetComponent nc;
        
        HashMap<String, LinkedList<Integer>> map = portListToHashMap(vlan.getPortList());
        
        for (Entry<String, LinkedList<Integer>>  entry : map.entrySet()) {
            
            String ncId = entry.getKey();
            LinkedList<Integer> portList = entry.getValue();
            
            nc = NetData.getNetComponentById(ncId);
            nc.start();
           
            response = nc.addTrunkPort(portList, vlan.getId());
           
            System.out.println("STATUS: " + response.getStatus());
            System.out.println("ENTITY: " + (String) response.getEntity());
           
            if(response.getStatus() != 200) {
                
            }
            
            
            nc.stop();
        }
        
        return Response.ok().build();
	}
	
	/**
	 * 
	 * @param incoming
	 * @return
	 */
	@PUT
    @Path("/addPort")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	public Response addPort(String incoming) {
	    
	    VLan vlan = gson.fromJson(incoming, VLan.class);
	 
	    return null;
	}
	
	
	
	
	
	
	/**
	 * Transforms a List of NetComponent;Port pairs to a HashMap, where the keys are the 
	 * IDs of the NetComponents and the value is a List of Ports.
	 * 
	 * The map contains the same NetComponents and Ports, but in a different format.
	 * 
	 * @param portList
	 * @return
	 */
	private HashMap<String, LinkedList<Integer>> portListToHashMap(LinkedList<String> portList) {
	    
	    String[] portArray;
	    String nc;
	    String portnumber;
	    
	    LinkedList<Integer> list = new LinkedList<Integer>();
	    
	    HashMap<String, LinkedList<Integer>> map = new HashMap<String, LinkedList<Integer>>();
	    
	    for (String port : portList) {
            
	        portArray = port.split(";");
	        nc = portArray[0];
	        portnumber = portArray[1];
	        
	        list = map.get(nc);
	        
	        if(list != null) {
	            list.add(Integer.parseInt(portnumber));
	        } else {
	            list = new LinkedList<Integer>();
	            list.add(Integer.parseInt(portnumber));
	            map.put(nc, list);
	        }
	        
        }
	    return map;
	    
	}
	
	
}
