package mm.auth;

import java.io.IOException;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

@Path("/test")
public class TestId{

  static ClientConfig config = new ClientConfig();
  static Client client = ClientBuilder.newClient(config);
  static WebTarget target = client.target(getBaseURI());
  
  @GET
  @Path("/sessionID")
  public void getSessionID(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession();
    System.out.println(session.getId());
    
    session.setMaxInactiveInterval(30);
    String encodedURL = response.encodeRedirectURL(TestId.getBaseURI().toString());
    response.sendRedirect(encodedURL);
  }
  
  /*public static void main(String[] args) {

    //System.out.println(target.path("rest").path("hello").request().accept(MediaType.TEXT_PLAIN).get(Response.class).toString());
    //System.out.println(target.path("rest").path("hello").request().accept(MediaType.TEXT_PLAIN).get(String.class));
    //System.out.println(target.path("rest").path("hello").path("VLAN1").request().accept(MediaType.TEXT_PLAIN).get(String.class));
  }*/
  
  @GET
  @Path("/Test")
  public void hallo() {
    System.out.println("hallo");
  }

  
  private static URI getBaseURI() {

    return UriBuilder.fromUri("http://localhost:8080/mm.auth/rest").build();
  }
}
