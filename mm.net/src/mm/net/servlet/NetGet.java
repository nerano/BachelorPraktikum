package mm.net.servlet;

import java.util.LinkedList;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.internal.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mm.net.main.NetData;
import mm.net.modeling.Interface;
import mm.net.modeling.NetComponent;
import mm.net.modeling.VLan;

@Path("/get")
public class NetGet {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * 
     * @param incoming
     * @return
     */
    @GET
    @Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
    @Path("/vlanStatus/{incoming}")
    public Response getVlanInfo(@PathParam("incoming") String incoming) {

        String[] parts = incoming.split(";");
        Response response;
        String responseString;
        int responseStatus = 200;
        int size = (parts.length - 1) / 2;

        String[] ncs = new String[size];
        int[] ports = new int[size];

        if (!(parts[parts.length - 1].equals("end")) || parts.length < 3
                || (parts.length - 1) % 2 != 0) {
            responseString = "Transfer not Complete in NetGet GetMultipleVLansById. Message : '"
                    + incoming + "' \n";
            return Response.status(400).entity(responseString).build();
        }

        for (int k = 0; k < parts.length - 2; k = k + 2) {
            ncs[k / 2] = parts[k];
            ports[k / 2] = Integer.parseInt(parts[k + 1]);
        }

        LinkedList<Interface> returnList = new LinkedList<Interface>();

        int pvid = 0;
        NetComponent nc;
        int port;
        for (int i = 0; i < size; i++) {

            port = ports[i];

            nc = NetData.getNetComponentById(ncs[i]);

            if (nc == null) {
                responseStatus = 404;
                responseString = "does not exist";
                returnList.add(new Interface(ncs[i] + ";" + port, pvid, responseString));
            } else {

                nc.start();
                response = nc.getPVID(port);
                nc.stop();

                System.out.println("ResponseStatus NetGet PVID " + response.getStatus());

                if (response.getStatus() != 200) {

                    responseStatus = 500;
                    responseString = (String) response.getEntity();
                    returnList.add(new Interface(ncs[i] + ";" + port, pvid, responseString));

                } else {
                    pvid = Integer.parseInt((String) response.getEntity());
                    returnList.add(new Interface(ncs[i] + ";" + port, pvid, null));
                    pvid = 0;
                }
            }
        }
        return Response.status(responseStatus).entity(gson.toJson(returnList)).build();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/globalVLan")
    public Response getNewGlobalVlan() {

        VLan vlan;
        do {
            vlan = NetData.getFreeGlobalVlan();
            if (vlan.isFree()) {
                return Response.ok(gson.toJson(vlan)).build();
            }
        } while (vlan != null);

        return Response.status(404).entity("No global VLan available").build();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/localVLan")
    public Response getNewLocalVlan() {
        VLan vlan;
        do {
            vlan = NetData.getFreeLocalVlan();
            if (vlan.isFree()) {
                return Response.ok(gson.toJson(vlan)).build();
            }
        } while (vlan != null);

        return Response.status(404).entity("No local VLan available").build();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/isFreeOnNc/{enc}")
    public Response isFree(@PathParam("enc") String encoded) {
        
        String decoded = Base64.decodeAsString(encoded);
        VLan vlan = gson.fromJson(decoded, VLan.class);
        
        if(vlan.isFreeOnNC()) {
            return Response.ok("true").build();
        } else {
            return Response.ok("false").build();
        }
        
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/staticConsistency/{net}")
    public Response getPowerConsistency(@PathParam("net") String net) {
        
        String responseString = "Consistency Check on the " + net + " Net\n";
        
        VLan vlan = NetData.getStaticVlan(net);
        if(vlan != null) {
            responseString += NetData.getStaticVlan(net).isConsistent();
            return Response.ok(responseString).build();
        } else {
            return Response.status(404).entity("Static Net " + net + " not found!").build();
        }
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/consistency/{enc}")
    public Response geCons(@PathParam("enc") String encoded) {
       
        String decoded = Base64.decodeAsString(encoded);
        VLan vlan = gson.fromJson(decoded, VLan.class);
     
        String responseString = "Consistency check on ID " + vlan.getId();
        responseString += vlan.isConsistent();
        
        return Response.status(200).entity(responseString).build();
        
    }
    
    
   
}
