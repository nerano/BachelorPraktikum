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
import mm.controller.power.Node;
import mm.controller.power.PowerGet;

@Path("/get")
public class ControllerGet {
	
	
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
		
		 for (NodeObjects nodeObject : nodeList) {
			
			 if(!(isNodeActive(nodeObject, id))){
				 nodeList.remove(nodeObject);
			 }
			 
		}
	
		response = gson.toJson(exp);
		return response;
	 }

	
	 
	 
	 protected boolean isNodeActive(NodeObjects node, int id){
		
		 boolean bool = true;
		 
		 LinkedList<Component> compList = node.getComponents();
		 
		 for (Component component : compList) {
			if(component.getStatus() == false || component.getvLanId() != id ) {
				bool = false;
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
   
        PowerGet powerget = new PowerGet();
        
        LinkedList<Node> test;
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
