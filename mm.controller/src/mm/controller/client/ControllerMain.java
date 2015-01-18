package mm.controller.client;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

public class ControllerMain {
  /**
   * The main of the Controller.
   * @param args argument of main
   */
  public static void main(String[] args) {

    ClientConfig config = new ClientConfig();
    Client client = ClientBuilder.newClient(config);
    WebTarget target = client.target(getBaseUri());
    MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<String, String>();
    queryParams.add("user", "testUser");

    String[] acceptString = { MediaType.TEXT_PLAIN };
    String[] acceptStringX = { MediaType.TEXT_XML };
    String[] acceptStringH = { MediaType.TEXT_HTML };
    String[] testUser = { "admin" };
    String[] testPassword = { "pw" };

    /** System.out.println(target.path("mm.auth").path("rest").path("authmain")
      .request()
      .accept(acceptString).get(Response.class)
      .toString());

    System.out.println(target.path("mm.auth").path("rest").path("authmain")
      .queryParam("user", "admin")
      .queryParam("pw", "pw").request()
      .accept(acceptString).get(String.class));**/

    WebTarget authTarget = client.target(getAuthBaseUri());

    System.out.println(authTarget.request().accept(acceptString).get(Response.class).toString());

    System.out.println(authTarget.queryParam("user", "admin").queryParam("pw", "pw").request()
        .accept(acceptString).get(String.class));

    System.out.println(target.path("mm.power").path("rest")
        .path("powermain").request()
        .accept(acceptString).get(Response.class)
        .toString());

    System.out.println(target.path("mm.power").path("rest")
        .path("powermain").request()
        .accept(acceptString).get(String.class));

    System.out.println(target.path("mm.net").path("rest").path("netmain")
        .request()
        .accept(acceptString).get(Response.class)
        .toString());

    System.out.println(target.path("mm.net").path("rest").path("netmain")
        .request()
        .accept(acceptString).get(String.class));
  }

  private static URI getBaseUri() {
    // Specify the BaseURI. For Testing Purposes "http://localhost:8080/"
    // URI for the Power "http://localhost:8080/mm.power/rest/powermain"
    // URI for the Auth "http://localhost:8080/mm.auth/rest/authmain"
    // URI for the Net "httP://localhost:8080/mm.net/rest/netmain"
    return UriBuilder.fromUri("http://localhost:8080/").build();
  }

  private static URI getAuthBaseUri() {
    return UriBuilder.fromUri("http://localhost:8080/mm.auth/rest/authmain").build();
  }

  private static URI getPowerBaseUri() {
    return UriBuilder.fromUri("http://localhost:8080/mm.power/rest/powermain").build();
  }

  private static URI getNetBaseUri() {

    return UriBuilder.fromUri(
        "http://localhost:8080/mm.net/rest/netmain").build();
  }
}