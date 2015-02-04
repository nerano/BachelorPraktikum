package mm.controller.client;

import java.util.LinkedList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mm.controller.main.ExpData;
import mm.controller.power.Node;
import mm.controller.power.PowerGet;

@Path("/get")
public class ControllerGet {


    @GET
    @Produces({"json/application","text/plain"})
    @Path("/exp/{id}")
    public String getExpById(@PathParam("id") int id){
        
        
        
        String response;
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
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
