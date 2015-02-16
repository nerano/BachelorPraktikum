package mm.controller.auth;

  import java.io.IOException;
  import java.net.MalformedURLException;
  import java.net.URI;
  import java.net.URL;

  import javax.servlet.RequestDispatcher;
  import javax.servlet.ServletException;
  import javax.servlet.annotation.WebServlet;
  import javax.servlet.http.HttpServlet;
  import javax.servlet.http.HttpServletRequest;
  import javax.servlet.http.HttpServletResponse;
  import javax.servlet.http.HttpSession;
  import javax.ws.rs.GET;
  import javax.ws.rs.Path;
  import javax.ws.rs.client.Client;
  import javax.ws.rs.client.ClientBuilder;
  import javax.ws.rs.client.WebTarget;
  import javax.ws.rs.core.MediaType;
  import javax.ws.rs.core.Response;
  import javax.ws.rs.core.UriBuilder;
  import javax.ws.rs.core.UriBuilderException;

  import org.glassfish.jersey.client.ClientConfig;

  public class Test {
    
    static ClientConfig config = new ClientConfig();
    static Client client = ClientBuilder.newClient(config);
    static WebTarget target = client.target(getBaseURI());
    static String sessionId;
    
    /*@GET
    @Path("sessionID")
    public void getSessionID(HttpServletRequest request, HttpServletResponse response) throws IOException {
      HttpSession session = request.getSession();
      System.out.println(session.getId());
      
      session.setMaxInactiveInterval(30);
      String encodedURL = response.encodeRedirectURL(Test.getBaseURI().toString());
      response.sendRedirect(encodedURL);
    }*/
    
    public static void main(String[] args) {

      //System.out.println(target.path("rest").path("hello").request().accept(MediaType.TEXT_PLAIN).get(Response.class).toString());
      sessionId = target.path("rest").path("hello").path("createSession").request().accept(MediaType.TEXT_PLAIN).get(String.class);
      System.out.println("SessionID: "+ sessionId);
      System.out.println(target.path("rest").path("hello").path("getID").request().get(String.class));
      System.out.println(target.path("rest").path("session").path(sessionId).request().get(String.class));
      
      try {
        target = client.target(getUrlWithSessionId());
      } catch (MalformedURLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (UriBuilderException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
      System.out.println(target.toString());
    }

    private static URI getBaseURI() {

      return UriBuilder.fromUri("http://localhost:8080/mm.auth").build();
    }
    
    private static URI getUrlWithSessionId() throws MalformedURLException, IllegalArgumentException, UriBuilderException {
      
      return UriBuilder.fromUri("http://localhost:8080/mm.auth/rest/hello/" + sessionId).build();    
    }
  }