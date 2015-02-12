package mm.controller.servlet;

import java.util.LinkedList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mm.controller.exclusion.ActiveNodeStrat;
import mm.controller.main.ExpData;
import mm.controller.modeling.Component;
import mm.controller.modeling.Experiment;
import mm.controller.modeling.NodeObjects;
import mm.controller.net.ControllerNetGet;
import mm.controller.net.VLan;
import mm.controller.power.Node;
import mm.controller.power.ControllerPowerGet;
import mm.controller.main.ServletContextClass;

@Path("/get")
public class ControllerGet {
	
		private ControllerPowerGet powerGet = new ControllerPowerGet();
		private ControllerNetGet netGet = new ControllerNetGet();
		
		@GET
		@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
		@Path("/nodes")
		public String getAllNodes(){
		    String response;
	        Gson gson = new GsonBuilder().setPrettyPrinting().create();
	        
	        response = gson.toJson(mm.controller.main.ServletContextClass.allNodes);
		    
	        return response;
		}
		
	 @GET
	 @Produces({"json/application","text/plain"})
	 @Path("/activeExp/{id}")
	 public String getActiveNodesByExpId(@PathParam("id") int id) throws CloneNotSupportedException{
		 String response;
		 Gson gson = new GsonBuilder().setPrettyPrinting().create();
		 
		 
		 
		 if(!(ExpData.exists(id))){
			 return response = "404, Experiment not found";
		 }
		 
		 Experiment exp = (Experiment) ExpData.getById(id).clone();
		
		 LinkedList<NodeObjects> nodeList = exp.getList();
		
		 LinkedList<NodeObjects> powerList = powerGet.getAll();
		 VLan vlan = netGet.getVlan(id);
		 
		 
		 
		 merge(nodeList, powerList, "status");
		 merge(nodeList, vlan);
		 
		 for (NodeObjects nodeObject : nodeList) {
			
			 if(!(isNodeActive(nodeObject, id))){
				 nodeList.remove(nodeObject);
			 }
			 
		}
		 
		 
		NodeObjects last = nodeList.getLast();
		 
		if(!(isNodeActive(last, id))){
			nodeList.remove(last);
		}
		 
		response = gson.toJson(exp);
		
		return response ;
	 }

	
	 protected boolean isNodeActive(NodeObjects node, int id){
		
		 boolean bool = false;
		 
		 LinkedList<Component> compList = node.getComponents();
		 
		 for (Component component : compList) {
			if(component.getStatus() == true & component.getvLanId() == id ) {
				bool = true;
			}
		}
		 
		 
		 return bool;
	 }
	 
	 
    @GET
    @Produces({"json/application","text/plain"})
    @Path("/exp/{id}")
    public String getExpById(@PathParam("id") int id){
        
        
        String response;
       
        Gson gson = new GsonBuilder().setExclusionStrategies(new ActiveNodeStrat())
        							 .setPrettyPrinting().create();
       
        response = gson.toJson(ExpData.getById(id));
        
        if(response.charAt(0) != '{'){
            response = "404, Experiment not found";
        }
        
        return response;
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJSON() {
   
        ControllerPowerGet powerget = new ControllerPowerGet();
        
        LinkedList<NodeObjects> test;
        test = powerget.getAll();
        System.out.println(test.toString());
        return test.toString();
    
    }
 
    @GET
    @Path("/test")
    public String getTest(){
        return "testHALLO";
    }
    
}
