package mm.controller.auth;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;

/** Works with the Webinterface. **/
@Path("/auth")
@Singleton
public class WebAuthTest implements ContainerResponseFilter {
   
  private ClientConfig config = new ClientConfig();
  Client client = ClientBuilder.newClient(config);
  WebTarget target = client.target(getBaseUri());
  
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response consumeJSON( String data ) throws JSONException {
    JSONObject json = new JSONObject(data);
    Response response = target.path("createSession").path(json.get("user").toString())
        .path(json.get("password").toString()).request().get(Response.class);
    if(response.getStatus() != 200) {
     return Response.status(403).entity("Wrong password or username").build();
    }
    return Response.ok(response.readEntity(String.class)).build();
  }
  
  @GET
  @Path("/hello")
  @Produces(MediaType.TEXT_PLAIN)
  public String sayPlainTextHello(@HeaderParam("testHeaderKey") String test) {  
    return "Hello there! + TestHeaderKey: " + test;
  }

  public Response getUserRole(String sessionId) {
    return this.target.path("getRole").request().header("sessionId", sessionId).get(Response.class);
  }
  
  public Response getUser(String sessionId) {
    return this.target.path("getUser").request().header("sessionId", sessionId).get(Response.class);
  }
  
  public Response checkSession(String sessionId) {
    return this.target.path("validation").request().header("sessionId", sessionId).get(Response.class);
  }
  
  public URI getBaseUri() {
    return UriBuilder.fromUri("http://localhost:8080/mm.auth/rest/session").build();
  }

  public void filter(ContainerRequestContext requestContext,ContainerResponseContext responseContext) throws IOException {
    MultivaluedMap<String, Object> headers = responseContext.getHeaders();
    headers.add("Access-Control-Allow-Origin", "*"); //headers.add("Access-Control-Allow-Origin", "http://PAGE.COM"); //allows CORS requests only coming from http://PAGE.COM
    headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
    headers.add("Access-Control-Allow-Headers","X-Requested-With, Content-Type, X-Codingpedia, testHeaderKey, sessionId,*");
    headers.add("Access-Control-Allow-Credentials", "true");
    headers.add("Access-Control-Request-Methods", "GET, POST, DELETE, PUT");
    headers.add("Access-Control-Request-Headers", "sessionId");
    headers.add("Access-Control-Expose-Headers", "testheaderresponse");
  }
}
