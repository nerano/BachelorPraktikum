package mm.net.servlet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mm.net.main.NetData;
import mm.net.modeling.NetComponent;
import mm.net.modeling.VLan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * In this class are all HTTP PUT methods from the NetService located.
 * 
 *   
 * <p>
 * The base URI for this class is <code>baseuri:port/mm.net/rest/put/</code>
 *
 * <p>
 * This includes the following actions:
 * <li> addPort / 
 * setPort </li>
 * 
 * <li> addTrunk / 
 * setTrunk </li>
 * 
 * <li> removePort / 
 * removeVlan </li>
 * 
 * <p> 
 * In General: 
 *
 * <p>
 * addX adds ports to an already existing VLAN, eventually replaces already existing 
 * configurations for members of this VLAN, but does not change any other members. 
 * Does not work on non existing VLANs and returns an error.
 * 
 * <p>
 * setX sets the provided VLAN anew, if it already existed, and replaces every existing configuration.
 * If the VLAN did not exist it is created. 
 * 
 *
 */
@Path("/put")
public class NetPut {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Sets the ports of the given VLAN as trunks.
     * 
     * <p>
     * URI: <code>baseuri:port/mm.net/rest/put/setTrunkPort</code>
     * 
     * <p>
     * The ports described in the VLan are set as trunk ports on the specified
     * VLAN. If this VLAN already existed it is destroyed and the submitted one
     * takes its place. All ports are set as (T) tagged trunk ports.
     * 
     * <p>
     * If the VLAN did not exist, it is created.
     * 
     * Possible HTTP statuscodes to return:
     * 
     * <li>200: Everything went right, all ports are set as trunk ports</li>
     * <li>500: If an error occurred this status code is returned with an error
     * description in the message body</li>
     * 
     * @param incoming
     *            a VLAN in JSON format
     * @return an inbound response object with status code and message body
     */
    @PUT
    @Path("/setTrunkPort")
    @Consumes({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
    public Response setTrunkPort(String incoming) {

        VLan vlan = gson.fromJson(incoming, VLan.class);
        System.out.println(incoming);

        Response response;
        String responseString = "";
        int responseStatus = 200;

        NetComponent nc;

        HashMap<String, LinkedList<Integer>> map = portListToHashMap(vlan.getPortList());

        for (Entry<String, LinkedList<Integer>> entry : map.entrySet()) {

            String ncId = entry.getKey();
            LinkedList<Integer> portList = entry.getValue();

            nc = NetData.getNetComponentById(ncId);
            nc.start();
            response = nc.setTrunkPort(portList, vlan.getId(), vlan.getName());

            if (response.getStatus() != 200) {
                responseStatus = 500;
                responseString += (String) response.getEntity();
            }
            System.out.println("STATUS SETTRUNKPORT: " + response.getStatus());
            System.out.println("ENTITY SETTRUNKPORT: " + (String) response.getEntity());
            nc.stop();
        }

        return Response.status(responseStatus).entity(responseString).build();
    }

    /**
     * Adds the ports contained in the submitted VLAN as trunk ports.
     * 
     * <p>
     * URI: <code>baseuri:port/mm.net/rest/put/addTrunkPort</code>
     * 
     * <p>
     * All ports described in the VLAN are added to the described VLAN ID as (T)
     * tagged trunk ports. Does not change other ports, but ports which already
     * hold a membership in the VLAN are replaced with the submitted
     * configuration.
     * 
     * <p>
     * If the VLAN does not exist, it is NOT created, but a 500 status code is
     * returned.
     *
     * <li>200: Everything went right, all ports are added as trunk ports</li>
     * <li>500: If an error occurred this status code is returned with an error
     * description in the message body</li>
     * 
     * @param incoming
     *            a VLAN in JSON format
     * @return an inbound response object with status code and message body
     */
    @PUT
    @Path("/addTrunkPort")
    @Consumes({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
    public Response addTrunkPort(String incoming) {

        VLan vlan = gson.fromJson(incoming, VLan.class);
        Response response;
        NetComponent nc;

        HashMap<String, LinkedList<Integer>> map = portListToHashMap(vlan.getPortList());

        for (Entry<String, LinkedList<Integer>> entry : map.entrySet()) {

            String ncId = entry.getKey();
            LinkedList<Integer> portList = entry.getValue();

            nc = NetData.getNetComponentById(ncId);
            nc.start();

            response = nc.addTrunkPort(portList, vlan.getId());

            System.out.println("STATUS ADDTRUNKPORT: " + response.getStatus());
            System.out.println("ENTITY ADDTRUNKPORT: " + (String) response.getEntity());

            if (response.getStatus() != 200) {
                // TODO errorhandling
            }

            nc.stop();
        }

        return Response.ok().build();
    }

    /**
     * Sets the ports of the given VLAN as (U) untagged members of the VLAN with
     * the affiliated ID.
     * 
     * <p>
     * URI: <code>baseuri:port/mm.net/rest/put/setPort</code>
     * 
     * <p>
     * The ports in the VLAN object are set as members of the VLAN. These ports
     * are (U) untagged, every frame leaving this port loses his VLAN ID tag.
     * 
     * <p>
     * If the VLAN does not exist, it is created. If it existed before it is
     * destroyed and the submitted VLAN takes its place.
     *
     * <li>200: Everything went right, all ports are set as untagged ports</li>
     * <li>500: If an error occurred this status code is returned with an error
     * description in the message body</li>
     * 
     * @param incoming
     *            a VLAN in JSON format
     * @return an inbound response object with status code and message body
     */
    @PUT
    @Path("/setPort")
    @Consumes({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
    public Response setPort(String incoming) {

        VLan vlan = gson.fromJson(incoming, VLan.class);
        System.out.println("SETPORT: ");
        System.out.println(incoming);

        Response response;

        NetComponent nc;

        HashMap<String, LinkedList<Integer>> map = portListToHashMap(vlan.getPortList());

        for (Entry<String, LinkedList<Integer>> entry : map.entrySet()) {

            String ncId = entry.getKey();
            LinkedList<Integer> portList = entry.getValue();

            nc = NetData.getNetComponentById(ncId);
            nc.start();
            response = nc.setPort(portList, vlan.getId(), vlan.getName());
            System.out.println("STATUS SETTRUNKPORT: " + response.getStatus());
            System.out.println("ENTITY SETTRUNKPORT: " + (String) response.getEntity());
            nc.stop();
        }

        return Response.ok().build();
    }

    /**
     * Adds the ports of the given VLAN as (U) untagged members of the VLAN with
     * the affiliated ID.
     * 
     * <p>
     * URI: <code>baseuri:port/mm.net/rest/put/addPort</code>
     * 
     * <p>
     * The ports in the VLAN object are added as members of the VLAN. These
     * ports are (U) untagged, every frame leaving this port loses his VLAN ID
     * tag. Every other member of the VLAN is not changed, the submitted ports
     * are only added to the VLAN.
     * 
     * <p>
     * The VLAN has to exist, if it does not exist a 500 HTTP status code is
     * returned. If it exists the ports are added accordingly.
     *
     * <li>200: Everything went right, all ports are set as untagged ports</li>
     * <li>500: If an error occurred this status code is returned with an error
     * description in the message body</li>
     * 
     * @param incoming
     *            a VLAN in JSON format
     * @return an inbound response object with status code and message body
     */
    @PUT
    @Path("/addPort")
    @Consumes({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
    public Response addPort(String incoming) {

        VLan vlan = gson.fromJson(incoming, VLan.class);
        Response response;
        NetComponent nc;

        System.out.println("ADDPORT INCOMING : " + incoming);

        HashMap<String, LinkedList<Integer>> map = portListToHashMap(vlan.getPortList());

        for (Entry<String, LinkedList<Integer>> entry : map.entrySet()) {

            String ncId = entry.getKey();
            LinkedList<Integer> portList = entry.getValue();
            nc = NetData.getNetComponentById(ncId);
            nc.start();

            response = nc.addPort(portList, vlan.getId());

            System.out.println("STATUS ADDPORT: " + response.getStatus());
            System.out.println("ENTITY ADDPORT: " + (String) response.getEntity());

            if (response.getStatus() != 200) {
                // TODO errorhandling
            }

            nc.stop();
        }

        return Response.ok().build();
    }

    /**
     * Removes the ports from the VLAN.
     * 
     * <p>
     * URI: <code>baseuri:port/mm.net/rest/put/removePort</code>
     * 
     * <p>
     * Expects a VLAN object in JSON format, all ports in this VLAN are removed
     * from the given VLAN ID and are no longer members of this VLAN. Does not
     * destroy the VLAN, even when all members were removed, the VLAN still
     * exists on the NetComponent.
     * 
     * <li>200: Everything went right, all ports were removed</li>
     * <li>500: If an error occurred this status code is returned with an error
     * description in the message body</li>
     * 
     * @param incoming
     *            a VLAN in JSON format
     * @return an inbound response object with status code and message body
     */
    @PUT
    @Path("/removePort")
    @Consumes({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
    public Response removePort(String incoming) {

        VLan vlan = gson.fromJson(incoming, VLan.class);
        Response response;
        NetComponent nc;

        System.out.println("REMOVEPORT INCOMING : " + incoming);

        HashMap<String, LinkedList<Integer>> map = portListToHashMap(vlan.getPortList());

        for (Entry<String, LinkedList<Integer>> entry : map.entrySet()) {

            String ncId = entry.getKey();
            LinkedList<Integer> portList = entry.getValue();
            nc = NetData.getNetComponentById(ncId);
            nc.start();

            response = nc.removePort(portList, vlan.getId());
            System.out.println("STATUS REMOVEPORT: " + response.getStatus());
            System.out.println("ENTITY REMOVEPORT: " + (String) response.getEntity());

            if (response.getStatus() != 200) {
                // TODO errorhandling
            }

            nc.stop();
        }

        return Response.ok().build();
    }

    /**
     * Removes the submitted VLAN.
     * 
     * Every port is not longer a member of this VLAN, because the VLAN does not
     * exist anymore after calling this method. Does not remove the VLAN from
     * all NetComponents per se, but only from a NetComponent if it was a member
     * of the submitted VLAN.
     * 
     * <p>
     * URI: <code>baseuri:port/mm.net/rest/put/removePort</code>
     * 
     * <li>200: Everything went right, the VLAN was removed</li>
     * <li>500: If an error occurred this status code is returned with an error
     * description in the message body</li>
     * 
     * @param incoming
     *            a VLAN in JSON format
     * @return an inbound response object with status code and message body
     */
    @PUT
    @Path("/removeVLan")
    @Consumes({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
    public Response removeVlan(String incoming) {

        VLan vlan = gson.fromJson(incoming, VLan.class);
        Response response;
        NetComponent nc;
        String responseString = "";
        int responseStatus = 200;
        System.out.println("REMOVEVLAN INCOMING : " + incoming);

        HashMap<String, LinkedList<Integer>> map = portListToHashMap(vlan.getPortList());

        for (Entry<String, LinkedList<Integer>> entry : map.entrySet()) {

            String ncId = entry.getKey();
            LinkedList<Integer> portList = entry.getValue();
            nc = NetData.getNetComponentById(ncId);
            nc.start();
            response = nc.destroyVlan(vlan.getId());

            System.out.println("STATUS REMOVEPORT: " + response.getStatus());
            System.out.println("ENTITY REMOVEPORT: " + (String) response.getEntity());

            for (Integer port : portList) {
                response = nc.getPVID(port);

                int pvid = Integer.parseInt((String) response.getEntity());

                if (pvid != 1 && pvid == vlan.getId()) {
                    nc.setPVID(port, 1);
                } else {
                    responseString = "Could not identify PVID";
                }

            }

            if (response.getStatus() != 200) {
                // TODO errorhandling
            }
            nc.stop();
        }

        return Response.ok().build();
    }

    /**
     * Transforms a List of NetComponent;Port pairs to a HashMap, where the keys
     * are the IDs of the NetComponents and the value is a List of Ports.
     * 
     * The map contains the same NetComponents and Ports, but in a different
     * format.
     * 
     * @param portList
     * @return
     */
    private HashMap<String, LinkedList<Integer>> portListToHashMap(LinkedList<String> portList) {

        String[] portArray;
        String nc;
        String portnumber;

        LinkedList<Integer> list = new LinkedList<Integer>();

        HashMap<String, LinkedList<Integer>> map = new HashMap<String, LinkedList<Integer>>();

        for (String port : portList) {

            portArray = port.split(";");
            nc = portArray[0];
            portnumber = portArray[1];

            list = map.get(nc);

            if (list != null) {
                list.add(Integer.parseInt(portnumber));
            } else {
                list = new LinkedList<Integer>();
                list.add(Integer.parseInt(portnumber));
                map.put(nc, list);
            }
        }
        return map;

    }

}
