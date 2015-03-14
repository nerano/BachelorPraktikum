package mm.net.servlet;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import mm.net.main.NetData;


@Path("/delete")
public class NetDelete {


    //private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    
    @DELETE
    @Path("globalVLan/{id}")
    public Response freeGlobalVlan(@PathParam("id") int id) {
        
        boolean bool = NetData.freeGlobalVlan(id);
        
        if(!bool) {
            return Response.status(500).entity("Could not free VLan" + id).build();
        }
        
        return Response.ok().build();
        
    }



    @DELETE
    @Path("/")
    public Response destroyLocalVlan()


}
