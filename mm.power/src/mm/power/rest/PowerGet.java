package mm.power.rest;

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
      
    String nodeId = "TestID1";
    
    String componentTyp = "WARP";
    
    PowerSupply ps = new AEHome(17, 75);
     
    int socket = 1;
      
    Node testNode = new Node(nodeId);
      
      
    Component testComponent = new Component(componentTyp,
                                              ps,
                                              socket);
   
  
    testComponent.setStatus(1);
    
    
    Component testComponent2 = new Component("APU",
            ps,
            socket);
    
    testNode.addComponent(testComponent);
    testNode.addComponent(testComponent2);
   
    Node testNode2 = new Node("TestID2");
    
    testNode2.addComponent(testComponent);
    testNode2.addComponent(testComponent2);
    
    
    List<Node> nodeList = new LinkedList<Node>();
    
    nodeList.add(testNode);
    nodeList.add(testNode2);
    
    
    Gson gson = new Gson();
    String json = gson.toJson(nodeList);
    
    return json;
      
  }





}
