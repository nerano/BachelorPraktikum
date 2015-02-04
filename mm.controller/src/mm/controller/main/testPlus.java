package mm.controller.main;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/test")
public class testPlus {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/test")
    public String getResource() {
       //counter++;
        return "Counter: " + ServletContextClass.getCounter();
    }

    @GET
    @Path("/testplus")
    public String getResourcePlus(){
        ServletContextClass.counterp();
        return "Counter: " + ServletContextClass.getCounter();
    }




}
