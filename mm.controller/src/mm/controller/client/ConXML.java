package mm.controller.client;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.json.XML;

@Path("/conxml")
public class ConXML {

 /** @GET
  @Produces(MediaType.APPLICATION_XML)
  
  public Response getXML(){
  
    String xmlString = 
        "<?xml version=\"1.0\" ?><test attrib=\"moretest\">Turn this to JSON</test>"; 
    
    String responseString = xmlString;
    
    Response response = Response.ok(responseString, MediaType.APPLICATION_XML).build();
    
    return response;
    
  } **/
  
}
