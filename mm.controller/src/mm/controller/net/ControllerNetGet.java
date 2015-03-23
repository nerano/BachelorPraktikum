package mm.controller.net;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.util.LinkedList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import mm.controller.modeling.Component;
import mm.controller.modeling.Interface;
import mm.controller.modeling.NodeObjects;
import mm.controller.modeling.VLan;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.internal.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class ControllerNetGet {

	private static ClientConfig config = new ClientConfig();
	private static Client client = ClientBuilder.newClient(config);
	private static WebTarget target = client.target(getBaseUri());

	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	/* -- PUBLIC METHODS -- */
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	public static LinkedList<Interface> getVlanInfo(NodeObjects node) {
	    
	    String parameter = turnNodeToPortString(node);
	    
	    Response response = target.path("vlanStatus").path(parameter).request().get(Response.class);
	    
	    Type type = new TypeToken<LinkedList<Interface>>() {}.getType();
	    
	    return gson.fromJson(response.readEntity(String.class), type);
	}
	
	public static String isConsistent(VLan vlan) {
	    
	    String encoded = Base64.encodeAsString(gson.toJson(vlan));
	    
	    Response response = target.path("consistency").path(encoded)
	            
	            .request().get(Response.class);
	    
	    return response.readEntity(String.class);
	}
	
	public static String staticConsistency(String net) {
	    
	    Response response = target.path("staticConsistency").path(net)
                
                .request().get(Response.class);
        
        return response.readEntity(String.class);
	}
	
	/**
	 * Returns a new and free global VLan from the NetService.
	 * 
	 * @return  an Outbound Response Object with status and message body
	 */
	public static Response getGlobalVlan() {
	    
	    Response response = target.path("globalVLan").request().get(Response.class);
	    
	    String responseString = response.readEntity(String.class);
	    
	    return Response.status(response.getStatus()).entity(responseString).build();
	    
	}
	
	
	public static Response getLocalVlan() {
	    Response response = target.path("localVLan").request().get(Response.class);
        
        String responseString = response.readEntity(String.class);
        
        return Response.status(response.getStatus()).entity(responseString).build();
	}
	
	/* -- PRIVATE METHODS -- */
	
	private static String turnNodeToPortString(NodeObjects node) {
	    
	    StringBuffer sb = new StringBuffer();
	    
	    for (Component component : node.getComponents()) {
            
	        for (Interface inf : component.getInterfaces()) {
	            sb.append(inf.getSwitchport());
	            
	            if (sb.length() > 0 && (sb.charAt(sb.length()-1) != ';')) {
	                    sb.append(";");
	            }
            }
        }
	    
	    sb.append("end");
	    try {
            return URLEncoder.encode(sb.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
	}
	
	private static URI getBaseUri() {
		return UriBuilder.fromUri("http://localhost:8080/mm.net/rest/get")
				.build();
	}

}
