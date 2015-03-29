package mm.controller.net;

import java.net.URI;
import java.util.LinkedList;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import mm.controller.modeling.VLan;

import org.glassfish.jersey.client.ClientConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ControllerNetPut {

    
    private static ClientConfig config = new ClientConfig();
    private static Client client = ClientBuilder.newClient(config);
    private static WebTarget target = client.target(getBaseUri());

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    
    
    
    /**
     * Sets all the given Ports of the VLan as Trunk Ports.
     * @param vlan
     * @return
     */
    public static Response setTrunkPort(VLan vlan) {
        
        String data = gson.toJson(vlan);
        
        
        Response response = target.path("setTrunkPort").request().accept(MediaType.TEXT_PLAIN).put(
                                Entity.entity(data, MediaType.TEXT_PLAIN), 
                                                                    Response.class);
        
        String responseString = response.readEntity(String.class);
        
        return Response.status(response.getStatus()).entity(responseString).build();
        
    }
    
    
    public static Response setTrunkPort(Set<String> ports, int vlanId, String name) {
        
        VLan vlan = new VLan(name, vlanId);
        vlan.setPortList(ports);
        return setTrunkPort(vlan);
        
    }
    
    
    /**
     * Adds the given VLan as TrunkPorts.
     * @param vlan  a VLan with ID and a list of Ports
     * @return
     */
    public static Response addTrunkPort(VLan vlan) {
        
        String data = gson.toJson(vlan);
        
        Response response = target.path("addTrunkPort").request().accept(MediaType.TEXT_PLAIN).put(
                Entity.entity(data, MediaType.TEXT_PLAIN), 
                                                    Response.class);
        
        String responseString = response.readEntity(String.class);
        
        return Response.status(response.getStatus()).entity(responseString).build();
        
        
    }
    
    
    
    public static Response setPort(VLan vlan) {
        
        String data = gson.toJson(vlan);
        
        Response response = target.path("setPort").request().accept(MediaType.TEXT_PLAIN).put(
                                Entity.entity(data, MediaType.TEXT_PLAIN), 
                                                                    Response.class);
        
        String responseString = response.readEntity(String.class);
        
        return Response.status(response.getStatus()).entity(responseString).build();
        
    }
    
    
    public static Response addPort(LinkedList<String> ports, int vlanId, String name) {
        
        VLan vlan = new VLan(name, vlanId);
        vlan.addPorts(ports);
        return addPort(vlan);
        
    }
    
    public static Response addPort(VLan vlan) {
        
        String data = gson.toJson(vlan);
        
        Response response = target.path("addPort").request().accept(MediaType.TEXT_PLAIN).put(
                Entity.entity(data, MediaType.TEXT_PLAIN), 
                                                    Response.class);
        
        String responseString = response.readEntity(String.class);
        
        return Response.status(response.getStatus()).entity(responseString).build();
    }
    
    
   
    /**
     * Convenience method for removePort(VLan vlan).
     * 
     * @param ports  ports to remove
     * @param vlanId  VLAN ID on which the ports should be removed
     * @return
     */
    public static Response removePort(LinkedList<String> ports, int vlanId, String name) {
        
        VLan vlan = new VLan(name, vlanId);
        vlan.addPorts(ports);
        return removePort(vlan);
        
    }
    
    /**
     * Removes the ports from the VLAN.
     * 
     * <p>
     * Expects a VLAN object, all ports in this VLAN are removed
     * from the given VLAN ID and are no longer members of this VLAN. Does not
     * destroy the VLAN, even when all members were removed, the VLAN still
     * exists on the NetComponent. PVID/Native ID of the ports is changed to 1.
     * 
     * @param incoming
     *            a VLAN 
     * @return an outbound response object with status code and message body
     */
    public static Response removePort(VLan vlan) {
        
        String data = gson.toJson(vlan);
        
        Response response = target.path("removePort").request().accept(MediaType.TEXT_PLAIN).put(
                Entity.entity(data, MediaType.TEXT_PLAIN), 
                                                    Response.class);
        
        String responseString = response.readEntity(String.class);
        
        return Response.status(response.getStatus()).entity(responseString).build();
        
        
    }
    
    /**
     * Removes the submitted VLAN.
     * 
     * <p>
     * Every port is not longer a member of this VLAN, because the VLAN does not
     * exist anymore after calling this method. Does not remove the VLAN from
     * all NetComponents per se, but only from a NetComponent if it was a member
     * of the submitted VLAN. Sets the PVID of all ports in this VLAN on 1.
     * </p>
     * 
     * @param incoming
     *            a VLAN 
     * @return an outbound response object with status code and message body
     */
   public static Response removeVlan(VLan vlan) {
      
       String data = gson.toJson(vlan);
      
       Response response = target.path("removeVLan").request().accept(MediaType.TEXT_PLAIN).put(
               Entity.entity(data, MediaType.TEXT_PLAIN), 
                                                   Response.class);
       
       String responseString = response.readEntity(String.class);
       
       return Response.status(response.getStatus()).entity(responseString).build();
       
   }
    
    private static URI getBaseUri() {
        return UriBuilder.fromUri("http://localhost:8080/mm.net/rest/put")
                .build();
    }
    
    
}
