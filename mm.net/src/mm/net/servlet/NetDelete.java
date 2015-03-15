package mm.net.servlet;

import java.util.LinkedList;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import mm.net.main.NetData;
import mm.net.modeling.NetComponent;


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
        
        LinkedList<NetComponent> ncList = NetData.getAllNetComponents();
        Response response;
        String responseString = "";
        for (NetComponent nc : ncList) {
            response = nc.destroyVlan(id);
        
            if(response.getStatus() != 200) {
                responseString += (String) response.getEntity();
            }
        
        }
        
        boolean bool = NetData.freeGlobalVlan(id);
        
        if(!bool) {
            responseString += "Could not free VLan" + id;
            return Response.status(500).entity(responseString).build();
        }
        
        return Response.ok().build();
        
    }



    @DELETE
    @Path("/localVLan")
    public Response destroyLocalVlan() {
        return null;
    }


}
