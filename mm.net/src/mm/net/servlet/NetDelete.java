package mm.net.servlet;


import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import mm.net.main.NetData;



@Path("/delete")
public class NetDelete {
    
    /**
     * Frees the global VLan with the given ID.
     * 
     * <p>
     * After freeing the VLan it can be retrieved again for further use by other experiments.
     * </p>
     * 
     * @param id ID of the VLan to free
     * @return  200(OK) or 500(Internal Server Error) if the VLan could not be freed.
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


    /**
     * Frees the local VLan with the given ID.
     * 
     * <p>
     * After freeing the VLan it can be retrieved again for further use by other experiments.
     * </p>
     * 
     * @param id ID of the VLan to free
     * @return  200(OK) or 500(Internal Server Error) if the VLan could not be freed.
     */
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
