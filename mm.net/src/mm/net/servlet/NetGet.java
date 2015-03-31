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
     * Returns the VLan status of every given port.
     * 
     * <p>
     * Expects a String of concatenated ports ending with ";end" as PathParam.
     * </p>
     * 
     * <p>
     * URI: <code> /mm.net/rest/get/vlanStatus/{incoming} </code>
     * </p>
     * 
     * <p>
     * A possible incoming String could look like this:
     * NetGear1;1;NetGear2;2;end. The NetService then proceeds to check the
     * PVIDs/Native IDs on these ports. It returns a JSON of a list of
     * Interfaces, in which the port and the current PVID are specified.
     * </p>
     * 
     * <p>
     * If a error occurred during the process the Interface on which the error
     * occurred holds a error String on the field "failure" and the HTTP status
     * code 500 (Internal Server Error) is returned. If no error occurred the
     * failure field is set to "none" and the status code 200(OK) is returned.
     * 
     * @param incoming
     *            a String of concatenated ports, ending with ";end"
     * @return a JSON of a list of interfaces.
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

    /**
     * Returns a free global VLan.
     * 
     * <p>
     * The VLan returned is free on all NetComponents in the network.
     * </p>
     * 
     * @return a JSON of a VLan when the status code is 200(OK) or 404 if no
     *         global VLan is available anymore
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/globalVLan")
    public Response getNewGlobalVlan() {

        VLan vlan;
        do {
            vlan = NetData.getFreeGlobalVlan();
            if (vlan != null && vlan.isFree()) {
                return Response.ok(gson.toJson(vlan)).build();
            }
        } while (vlan != null);

        return Response.status(404).entity("No global VLan available").build();
    }

    /**
     * Returns a free local VLan.
     * 
     * <p>
     * The VLan returned is free on all NetComponents in the network.
     * </p>
     * 
     * @return a JSON of a VLan when the status code is 200(OK) or 404 if no
     *         local VLan is available anymore
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/localVLan")
    public Response getNewLocalVlan() {
        VLan vlan;
        do {
            vlan = NetData.getFreeLocalVlan();
            if (vlan != null && vlan.isFree()) {
                return Response.ok(gson.toJson(vlan)).build();
            }
        } while (vlan != null);

        return Response.status(404).entity("No local VLan available").build();
    }

    /**
     * Checks if a VLan is free on the NetComponents which are part of the VLan.
     * 
     * <p>
     * Expects a Base64/URL safe encoded VLan in JSON format as PathParam.
     * </p>
     * 
     * <p>
     * URI: <code>/mm.net/rest/get/isFreeOnNC/{enc}</code>
     * </p>
     * 
     * @param encoded
     *            a VLAN in JSON format, Base64/URL safe encoded
     * @return
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/isFreeOnNc/{enc}")
    public Response isFree(@PathParam("enc") String encoded) {

        String decoded = Base64.decodeAsString(encoded);
        VLan vlan = gson.fromJson(decoded, VLan.class);

        if (vlan.isFreeOnNC()) {
            return Response.ok("true").build();
        } else {
            return Response.ok("false").build();
        }

    }

    /**
     * Returns the ConsistencyCheck of one of the two static VLans.
     * 
     * <p>
     * Expects either "power" or "management" as PathParam
     * </p>
     * 
     * <p>
     * URI: <code> /mm.net/rest/get/consistency/{net}
     * </p>
     * 
     * The ConsistencyCheck report as String of the chosen static VLan.
     * 
     * @param encoded
     *            a VLAN in JSON format, Base64/URL safe encoded
     * @return a String with the report of the consistency check.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/staticConsistency/{net}")
    public Response getPowerConsistency(@PathParam("net") String net) {

        String responseString = "Consistency Check on the " + net + " Net\n";

        VLan vlan = NetData.getStaticVlan(net);
        if (vlan != null) {
            responseString += NetData.getStaticVlan(net).isConsistent();
            return Response.ok(responseString).build();
        } else {
            return Response.status(404).entity("Static Net " + net + " not found!").build();
        }
    }

    /**
     * Returns the ConsistencyCheck of the given VLan
     * 
     * <p>
     * Expects a Base64 encoded VLan in JSON format as PathParam.
     * </p>
     * 
     * <p>
     * URI: <code> /mm.net/rest/get/consistency/{enc}
     * </p>
     * 
     * The ConsistencyCheck report as String.
     * 
     * @param encoded
     *            a VLAN in JSON format, Base64/URL safe encoded
     * @return a String with the report of the consistency check.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/consistency/{enc}")
    public Response geCons(@PathParam("enc") String encoded) {

        String decoded = Base64.decodeAsString(encoded);
        VLan vlan = gson.fromJson(decoded, VLan.class);

        String responseString = vlan.isConsistent();

        return Response.status(200).entity(responseString).build();

    }

}
