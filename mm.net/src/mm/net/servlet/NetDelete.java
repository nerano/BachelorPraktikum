package mm.net.servlet;


import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import mm.net.main.NetData;



@Path("/delete")
public class NetDelete {


    //private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    /**
     * Deletes the VLan with the given ID on all NetComponents and frees it for further use.
     * 
     * 
     * @param id
     * @return
     */
    @DELETE
    @Path("globalVLan/{id}")
    public Response freeGlobalVlan(@PathParam("id") int id) {
        
        boolean bool = NetData.freeGlobalVlan(id);
        
        if(!bool) {
            String responseString = "Could not free global VLan" + id;
            return Response.status(500).entity(responseString).build();
        }

        return Response.ok().build();
    }



    @DELETE
    @Path("/localVLan/{id}")
    public Response destroyLocalVlan(@PathParam("id") int id) {
        
        boolean bool = NetData.freeLocalVlan(id);
        
        if(!bool) {
            String responseString = "Could not free local VLan" + id;
            return Response.status(500).entity(responseString).build();
        }
        
        return Response.ok().build();
    }


}
