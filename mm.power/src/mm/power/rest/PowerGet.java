package mm.power.rest;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import mm.power.implementation.*;
import mm.power.modeling.*;

@Path("/get")
public class PowerGet {
  /**
   * Returns status-information about all known nodes in the network.
   * The information contains the node, the type of the component and the power 
   * status of the component[0/1]. 
   * @return response A Response object with the information about the power status
   */
  @GET
  @Produces(MediaType.TEXT_PLAIN)
    public String getAll() {

    String response = testGet();
    
    return response;

  }
  /**
   * Returns information about the power status of the node with the given ID.
   * @param id ID of the node
   * @return response A response Object with the requested information
   */
  @GET
  @Path("{id}")
  @Produces({MediaType.TEXT_PLAIN, "json/application"})
    public Response getById(@PathParam("id") String id) {

    Response response = null;
    // String test = status(id);

    response.status(200);
    return response;

  }

  
  public String testGet(){
      
    String nodeId = "Knoten A";
    
    String componentTyp = "WARP";
    
    PowerSupply ps = new AEHome(17, 75);
     
    int socket = 1;
      
    Node testNode = new Node(nodeId);
      
      
    Component testComponent = new Component(componentTyp,
                                              ps,
                                              socket);
   
  
    testComponent.setStatus(true);
    
    
    Component testComponent2 = new Component("APU",
            ps,
            socket);
    
   
    Component testComponent3 = new Component("Komponente X",
            ps,
            socket);
    Component testComponent4 = new Component("Komponente Z",
            ps,
            socket);
    
    
    
    testNode.addComponent(testComponent);
    testNode.addComponent(testComponent2);
   
    Node testNode2 = new Node("Knoten B");
    
    testNode2.addComponent(testComponent3);
    testNode2.addComponent(testComponent4);
    
    
    testComponent3.setStatus(false);
    testComponent4.setStatus(true);
    
    List<Node> nodeList = new LinkedList<Node>();
    
    nodeList.add(testNode);
    nodeList.add(testNode2);
    
    
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String json = gson.toJson(nodeList);
    
    return json;
      
  }

  
  
  @GET
  @Path("/test")
  public String getAlll() {
      
      String powerString = testGet();
      
      Gson gson = new Gson();
      
      LinkedList<Node> nodeList = new LinkedList<Node>();
      
      Type type = new TypeToken<LinkedList<Node>>(){}.getType();
      
      nodeList = (LinkedList<Node>) gson.fromJson(powerString, type);
    
      String ss;
      
      ss = nodeList.get(0).getId();
      
      
      
      return ss;
      
        
        
  }



}
