package mm.net.servlet;


import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mm.net.main.NetData;
import mm.net.modeling.VLan;

@Path("/get")
public class NetGet {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

   /**  @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{id}")
    public Response getVLanById(@PathParam("id") int id) {

        String responseString;

        if (!(NetData.exists(id))) {
            responseString = "404, VLan " + id + " not found!";
            return Response.status(404).entity(responseString).build();
        }

        VLan vlan = NetData.getById(id);

        responseString = gson.toJson(vlan);
        return Response.status(200).entity(responseString).build();

    } **/

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/globalVLan")
    public Response getNewGlobalVlan() {
        VLan vlan = NetData.getFreeGlobalVlan();
        if(vlan != null) {
            return Response.ok(gson.toJson(vlan)).build();
        }
       return Response.status(404).entity("No global VLan available").build();
    }
    
    
    
    /**
     * 
     * @param incoming
     *            Only
     * @return
     * @throws TransferNotCompleteException
     */
   /** @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{incoming}")
    public Response getMultipleVLansById(@PathParam("incoming") String incoming) {

        LinkedList<VLan> returnList = new LinkedList<VLan>();
        String responseString;
        String[] parts = incoming.split(";");
        int id;

        if (!(parts[parts.length - 1].equals("end"))) {
            responseString = "Transfer not Complete in NetGet GetMultipleVLansById. Message : '"
                    + incoming + "' \n";
            return Response.status(400).entity(responseString).build();
        }

        for (int i = 0; i < parts.length - 1; i++) {

            id = Integer.parseInt(parts[i]);

            if (!(NetData.exists(id))) {
                responseString = "404, VLan " + id + " not found!";
                return Response.status(404).entity(responseString).build();
            }

            returnList.add(NetData.getById(id));

        }

        responseString = gson.toJson(returnList);
        return Response.status(200).entity(responseString).build();

    } **/
}
