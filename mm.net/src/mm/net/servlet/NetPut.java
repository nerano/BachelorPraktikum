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
	    String responseString = "";
	    int responseStatus = 200;
		
		NetComponent nc;
		
		HashMap<String, LinkedList<Integer>> map = portListToHashMap(vlan.getPortList());
		
		for (Entry<String, LinkedList<Integer>>  entry : map.entrySet()) {
            
		    String ncId = entry.getKey();
		    LinkedList<Integer> portList = entry.getValue();
		    
		    nc = NetData.getNetComponentById(ncId);
		    nc.start();
		    response = nc.setTrunkPort(portList, vlan.getId(), vlan.getName());
		    
		    if(response.getStatus() != 200) {
		        responseStatus = 500;
		        responseString += (String) response.getEntity();
		    }
		    System.out.println("STATUS SETTRUNKPORT: " + response.getStatus());
		    System.out.println("ENTITY SETTRUNKPORT: " + (String) response.getEntity());
		    nc.stop();
        }
		
		return Response.status(responseStatus).entity(responseString).build();
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
           
            System.out.println("STATUS ADDTRUNKPORT: " + response.getStatus());
            System.out.println("ENTITY ADDTRUNKPORT: " + (String) response.getEntity());
           
            if(response.getStatus() != 200) {
                //TODO errorhandling
            }
            
            nc.stop();
        }
        
        return Response.ok().build();
	}
	
	@PUT
    @Path("/setPort")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    public Response setPort(String incoming) {
        
        VLan vlan = gson.fromJson(incoming, VLan.class);
        System.out.println("SETPORT: ");
        System.out.println(incoming);
       
        Response response;
        
        NetComponent nc;
        
        HashMap<String, LinkedList<Integer>> map = portListToHashMap(vlan.getPortList());
        
        for (Entry<String, LinkedList<Integer>>  entry : map.entrySet()) {
            
            String ncId = entry.getKey();
            LinkedList<Integer> portList = entry.getValue();
            
            nc = NetData.getNetComponentById(ncId);
            nc.start();
            response = nc.setPort(portList, vlan.getId(), vlan.getName());
            System.out.println("STATUS SETTRUNKPORT: " + response.getStatus());
            System.out.println("ENTITY SETTRUNKPORT: " + (String) response.getEntity());
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
	    Response response;
	    NetComponent nc;
	    
	    System.out.println("ADDPORT INCOMING : " + incoming);
	    
	    HashMap<String, LinkedList<Integer>> map = portListToHashMap(vlan.getPortList());
        
	    for (Entry<String, LinkedList<Integer>>  entry : map.entrySet()) {
            
            String ncId = entry.getKey();
            LinkedList<Integer> portList = entry.getValue();
            nc = NetData.getNetComponentById(ncId);
            nc.start();
            
            response = nc.addPort(portList, vlan.getId());
            
            System.out.println("STATUS ADDPORT: " + response.getStatus());
            System.out.println("ENTITY ADDPORT: " + (String) response.getEntity());
           
            if(response.getStatus() != 200) {
                //TODO errorhandling
            }
            
            nc.stop();
	    }   
	    
	    
	    return Response.ok().build();
	}
	
	@PUT
    @Path("/removePort")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    public Response removePort(String incoming) {
	
	    VLan vlan = gson.fromJson(incoming, VLan.class);
        Response response;
        NetComponent nc;
        
        System.out.println("REMOVEPORT INCOMING : " + incoming);
        
        HashMap<String, LinkedList<Integer>> map = portListToHashMap(vlan.getPortList());
        
        for (Entry<String, LinkedList<Integer>>  entry : map.entrySet()) {
            
            String ncId = entry.getKey();
            LinkedList<Integer> portList = entry.getValue();
            nc = NetData.getNetComponentById(ncId);
            nc.start();
            
            response = nc.removePort(portList, vlan.getId());
            
            System.out.println("STATUS REMOVEPORT: " + response.getStatus());
            System.out.println("ENTITY REMOVEPORT: " + (String) response.getEntity());
           
            if(response.getStatus() != 200) {
                //TODO errorhandling
            }
            
            nc.stop();
        }   
        
        
        return Response.ok().build();
    }
	
	@PUT
    @Path("/removeVLan")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    public Response removeVlan(String incoming) {
    
        VLan vlan = gson.fromJson(incoming, VLan.class);
        Response response;
        NetComponent nc;
        String responseString = "";
        int responseStatus = 200;
        System.out.println("REMOVEVLAN INCOMING : " + incoming);
        
        HashMap<String, LinkedList<Integer>> map = portListToHashMap(vlan.getPortList());
        
        for (Entry<String, LinkedList<Integer>>  entry : map.entrySet()) {
            
            String ncId = entry.getKey();
            LinkedList<Integer> portList = entry.getValue();
            nc = NetData.getNetComponentById(ncId);
            nc.start();
            response = nc.destroyVlan(vlan.getId());

            System.out.println("STATUS REMOVEPORT: " + response.getStatus());
            System.out.println("ENTITY REMOVEPORT: " + (String) response.getEntity());
            
            for (Integer port : portList) {
                response = nc.getPVID(port);
                
                int pvid = Integer.parseInt((String) response.getEntity());
                
                if(pvid != 1  && pvid == vlan.getId()) {
                    nc.setPVID(port, 1);
                } else {
                    responseString = "Could not identify PVID";
                }
                
            }
           
            if(response.getStatus() != 200) {
                //TODO errorhandling
            }
            nc.stop();
        }   
        
        return Response.ok().build();
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
