package mm.auth;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

// Plain old Java Object it does not extend as class or implements 
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation. 
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML. 

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello
@Path("/authmain")
public class AuthMain {

  MediaType mediatype = new MediaType(MediaType.TEXT_PLAIN, "subAuth");
	
	HashMap<String, String> users = new HashMap<String, String>();
	
	// This method is called if TEXT_PLAIN is request
 
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes("text/plain")
  public String sayAuth(@QueryParam("user") String user, @QueryParam("pw") String pw) {
    
	  users.put("testUser", "testPassword");
	  users.put("admin", "pw");
	  
	  
	  
	  if(user != null && users.get(user).equals(pw)){
		  return "true";
	  } else {
		  return "false";
	  }
	  
  }
  


} 