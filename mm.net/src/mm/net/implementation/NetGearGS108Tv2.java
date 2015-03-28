package mm.net.implementation;

import mm.net.modeling.NetComponent;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.ws.rs.core.Response;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UnsignedInteger32;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class NetGearGS108Tv2 implements NetComponent {

    private Target              target;
    private String              host;
    private String              identifier;
    private final int           ports     = 8;
    private String              community = "private";
    private Snmp                snmp      = null;
    private int                 retries   = 2;
    private int                 timeout   = 1500;
    private LinkedList<Integer> trunks    = new LinkedList<Integer>();

    /**
     * 
     * @param identifier
     *            unique id
     * @param add
     *            IP or hostname
     * @param trunks
     *            list of trunkports
     */
    public NetGearGS108Tv2(String identifier, String add, LinkedList<Integer> trunks) {

        this.identifier = identifier;
        host = "udp:" + add + "/161";
        this.trunks = trunks;

        createTarget();
    }

    /**
     * Has to be called before issuing SNMP commands to the NetComponent.
     * <p>
     * 200 status code if the startup went right, 500 if it went wrong. More
     * details in the message body.
     * 
     * @return Response Object with 200 or 500 status code
     */
    public Response start() {
        try {
            TransportMapping<?> udp;
            udp = new DefaultUdpTransportMapping();
            snmp = new Snmp(udp);
            udp.listen();
        } catch (Exception e) {
            e.printStackTrace();
            return errorHandler("start()", e);
        }
        return Response.ok().build();

    }

    /**
     * Call this after issuing all SNMP commands to clear all associated
     * objects.
     * <p>
     * 200 if the stopping went right, 500 if something went wrong. More details
     * in the message body.
     * 
     * @return Response Object with status code 200 or 500 (and message body).
     */
    public Response stop() {
        try {
            snmp.close();
        } catch (Exception e) {
            e.printStackTrace();
            return errorHandler("stop()", e);
        }
        return Response.ok().build();
    }

    @Override
    public Response destroyVlan(int vlanId) {

        String rowStatus = ".1.3.6.1.2.1.17.7.1.4.3.1.5." + vlanId;
        Variable variable = new Integer32(6);

        ResponseEvent responseEvent = set(rowStatus, variable);

        return setHandler(responseEvent, "destroyVLan '" + vlanId + "'");

    }

    /**
     * Sets the PVID on the given port.
     * 
     * <p>
     * 
     * @param port
     *            int, port on which the PVID should be set.
     * @param pvid
     *            int, PVID to set.
     * @return a Outbound Response Object with a status code and message body
     */
    @Override
    public Response setPVID(int port, int pvid) {

        String pvidString = ".1.3.6.1.2.1.17.7.1.4.5.1.1." + port;

        Variable variable = new UnsignedInteger32(pvid);

        ResponseEvent responseEvent = set(pvidString, variable);

        System.out.println(responseEvent.getResponse().toString());

        return setHandler(responseEvent, "setPVID");

    }

    public Response reset() {
        
        String pvidString = ".1.3.6.1.4.1.4526.11.1.3.9.0";
        
        Variable variable = new Integer32(1);
        
        ResponseEvent responseEvent = set(pvidString, variable);
        
        return setHandler(responseEvent, "reset()");
        
    }
    
    
    /**
     * Sets the egress (outgoing) ports on VLan with the provided VLan ID.
     * 
     * <p>
     * Specify in the String which ports should be egress ports. The String can
     * be a HexString of length two or a binary String of length 8, where a 0
     * means that the port is no egress port and a 1 that the port is an egress
     * port.
     * <p>
     * Examples:
     * 
     * <div style="text-indent:30px;"> <code>AA</code> sets the 1st, 3rd, 5th,
     * 7th port as an egress port, same as <code>10101010</code> </div>
     * 
     * <div style="text-indent:30px;"> <code>00001111</code> sets the last four
     * ports on egress mode, same as <code>0F</code> </div>
     * <p>
     * 
     * @param vlanId
     *            the vlanId to which the ports belong
     * @param varString
     *            specification of the egress ports
     * @return a Response Object with status code, if the status is 500 the body
     *         contains a specified error message.
     */
    public Response setEgressPorts(int vlanId, String varString) {

        switch (varString.length()) {
        case 2:
            if(!varString.matches("[01234567879ABCDEFabcdef]{2}")) {
                return errorHandler("setEgressPorts","not valid hex");
            }
            break;

        case 8:
            if(!varString.matches("[01]{8}")) {
                return errorHandler("setEgressPorts","not valid binary");
            }
            varString = String.format("%02x", Integer.parseInt(varString, 2));
            
            break;
        default:
            return errorHandler("setEgressPorts",
                    "Invalid format, should be 2 in hexadecimal or 8 in binary,"
                            + " but was '" + varString + "' \n");
        }

        String egress = ".1.3.6.1.2.1.17.7.1.4.3.1.2." + vlanId;
        Variable variable = OctetString.fromHexString(varString);

        System.out.println(variable);
        ResponseEvent responseEvent = set(egress, variable);

        return setHandler(responseEvent, "setEgressPorts");

    }

    /**
     * Sets the untagged ports on VLan with the provided VLan ID.
     * 
     * <p>
     * Specify in the String which ports should be untagged ports. The String
     * can be a HexString of length two or a binary String of length 8, where a
     * 0 means that the port is no untagged port and a 1 that the port is an
     * untagged port.
     * <p>
     * Examples:
     * 
     * <div style="text-indent:30px;"> <code>AA</code> sets the 1st, 3rd, 5th,
     * 7th port as an untagged port, same as <code>10101010</code> </div>
     * 
     * <div style="text-indent:30px;"> <code>00001111</code> sets the last four
     * ports on untagged mode, same as <code>0F</code> </div>
     * <p>
     * 
     * @param vlanId
     *            the vlanId to which the ports belong
     * @param varString
     *            specification of the untagged ports
     * @return a Response Object with status code, if the status is 500 the body
     *         contains a specified error message.
     */
    public Response setUntaggedPorts(int vlanId, String varString) {

        switch (varString.length()) {
        case 2:
            if(!varString.matches("[01234567879ABCDEFabcdef]{2}")) {
                return errorHandler("setUntaggedPorts","not valid hex");
            }
            break;

        case 8:
            if(!varString.matches("[01]{8}")) {
                return errorHandler("setUntaggedPorts","not valid binary");
            }
            varString = String.format("%02x", Integer.parseInt(varString, 2));
            
            break;
        default:
            return errorHandler("setEgressPorts",
                    "Invalid format, should be 2 in hexadecimal or 8 in binary,"
                            + " but was '" + varString + "' \n");
        }

        String egress = ".1.3.6.1.2.1.17.7.1.4.3.1.4." + vlanId;
        Variable variable = OctetString.fromHexString(varString);

        System.out.println(variable);
        ResponseEvent responseEvent = set(egress, variable);

        return setHandler(responseEvent, "setUntaggedPorts");

    }


    public Response setTrunkPort(int port, int vlanId, String name) {
        LinkedList<Integer> list = new LinkedList<Integer>();
        list.add(port);
        return setTrunkPort(list, vlanId, name);
    }

    public Response setTrunkPort(List<Integer> ports, int vlanId, String name) {

        destroyVlan(vlanId);

        String egressPorts = "";
        String untaggedPorts = "";

        for (int i = 1; i <= 8; i++) {
            if (ports.contains(i)) {
                egressPorts = egressPorts + "1";
                untaggedPorts = untaggedPorts + "0";
            } else {
                egressPorts = egressPorts + "0";
                untaggedPorts = untaggedPorts + "1";
            }
        }

        Response response = setRowStatus(vlanId, 4);
        if (response.getStatus() != 200) {
            System.out.println("ROW STATUS FAILED");
            destroyVlan(vlanId);
            return response;
        }

        response = setStaticName(vlanId, name);
        if (response.getStatus() != 200) {
            destroyVlan(vlanId);
            return response;
        }

        response = setEgressPorts(vlanId, egressPorts);
        if (response.getStatus() != 200) {
            destroyVlan(vlanId);
            return response;
        }

        response = setUntaggedPorts(vlanId, untaggedPorts);
        if (response.getStatus() != 200) {
            destroyVlan(vlanId);
            return response;
        }

        return Response.ok().build();

    }

    @Override
    public Response removePort(int port, int vlanId) {

        LinkedList<Integer> list = new LinkedList<Integer>();
        list.add(port);

        return removePort(list, vlanId);
    }

    /**
     * Removes the Port from the Egress(Outgoing) Portlist and sets the PVID to
     * 1
     */
    @Override
    public Response removePort(List<Integer> ports, int vlanId) {

        Response response = getEgressPorts(vlanId);

        System.out.println("removePort Status getEgress: " + response.getStatus());

        if (response.getStatus() != 200) {
            return errorHandler("removePort", "could not get egressPorts because of: \n"
                    + ((String) response.getEntity()));
        }

        String newEgress = ((String) response.getEntity());

        System.out.println("NEW EGRESS AT REMOVE PORT: " + newEgress);

        for (int i = 0; i < ports.size(); i++) {

            StringBuilder newEgressBuilder = new StringBuilder(newEgress);
            newEgressBuilder.setCharAt(ports.get(i) - 1, '0');
            newEgress = newEgressBuilder.toString();

            setPVID(ports.get(i), 1);
        }

        System.out.println("NEW EGRESS2 AT REMOVE PORT: " + newEgress);

        response = setEgressPorts(vlanId, newEgress);

        if (response.getStatus() != 200) {
            errorHandler("removePort - setEgressPorts",
                    (String) response.getEntity());
        }

        System.out.println("SET EGRESS IN REMOVE PORT");
        System.out.println((String) response.getEntity());

        return Response.ok().build();

    }

    public Response setPort(int port, int vlanId, String name) {

        LinkedList<Integer> list = new LinkedList<Integer>();
        list.add(port);

        return setPort(list, vlanId, name);

    }

    public Response setPort(List<Integer> ports, int vlanId, String name) {

        destroyVlan(vlanId);

        String egressPorts = "";
        String untaggedPorts = "";

        for (int i = 1; i <= 8; i++) {
            if (ports.contains(i)) {
                egressPorts = egressPorts + "1";
                untaggedPorts = untaggedPorts + "1";
            } else {
                egressPorts = egressPorts + "0";
                untaggedPorts = untaggedPorts + "1";
            }

        }

        Response response = setRowStatus(vlanId, 4);
        if (response.getStatus() != 200) {
            System.out.println("ROW STATUS FAILED");
            destroyVlan(vlanId);
            return response;
        }

        response = setStaticName(vlanId, name);
        if (response.getStatus() != 200) {
            destroyVlan(vlanId);
            return response;
        }

        response = setEgressPorts(vlanId, egressPorts);
        if (response.getStatus() != 200) {
            destroyVlan(vlanId);
            return response;
        }

        response = setUntaggedPorts(vlanId, untaggedPorts);
        if (response.getStatus() != 200) {
            destroyVlan(vlanId);
            return response;
        }

        for (int i = 0; i < ports.size(); i++) {
            setPVID(ports.get(i), vlanId);
        }

        return Response.ok().build();

    }

    @Override
    public Response addPort(int port, int vlanId) {

        LinkedList<Integer> list = new LinkedList<Integer>();
        list.add(port);

        return addPort(list, vlanId);

    }

    @Override
    public Response addPort(List<Integer> ports, int vlanId) {

        Response response = getEgressAndUntaggedPorts(vlanId);

        System.out.println("addPorts Status getEgress and Untagged: " + response.getStatus());

        if (response.getStatus() != 200) {
            return errorHandler("addPort", "could not get egressPorts/Untagged because of: \n"
                    + ((String[]) response.getEntity())[1]);
        }

        String newEgress = ((String[]) response.getEntity())[0];
        String newUntagged = ((String[]) response.getEntity())[1];

        System.out.println("NEW EGRESS: " + newEgress);
        System.out.println("NEW UNTAGGED: " + newUntagged);

        for (int i = 0; i < ports.size(); i++) {

            StringBuilder newEgressBuilder = new StringBuilder(newEgress);
            newEgressBuilder.setCharAt(ports.get(i) - 1, '1');
            newEgress = newEgressBuilder.toString();

            StringBuilder newUntaggedBuilder = new StringBuilder(newUntagged);
            newUntaggedBuilder.setCharAt(ports.get(i) - 1, '1');
            newUntagged = newUntaggedBuilder.toString();

            setPVID(ports.get(i), vlanId);

        }

        System.out.println("NEW EGRESS2: " + newEgress);
        System.out.println("NEW UNTAGGED2: " + newUntagged);

        response = setEgressPorts(vlanId, newEgress);

        if (response.getStatus() != 200) {
            errorHandler("addPort - setEgressPorts",
                    (String) response.getEntity());
        }

        System.out.println("SET EGRESS IN ADDPORT");
        System.out.println((String) response.getEntity());

        response = setUntaggedPorts(vlanId, newUntagged);

        if (response.getStatus() != 200) {
            errorHandler("addPort - setUntaggedPorts",
                    (String) response.getEntity());
        }

        System.out.println("SET UNTAGGED IN ADDPORT");
        System.out.println((String) response.getEntity());

        return Response.ok().build();
    }

    @Override
    public Response addTrunkPort(int port, int vlanId) {

        LinkedList<Integer> list = new LinkedList<Integer>();
        list.add(port);

        return addTrunkPort(list, vlanId);
    }

    @Override
    public Response addTrunkPort(List<Integer> ports, int vlanId) {

        Response response = getEgressAndUntaggedPorts(vlanId);

        System.out.println("addTrunkPorts Status getEgress: " + response.getStatus());

        if (response.getStatus() != 200) {
            return errorHandler("addTrunkPort", "could not get egressPorts/Untagged because of: \n"
                    + Arrays.toString((String[]) response.getEntity()));
        }

        String newEgress = ((String[]) response.getEntity())[0];
        String newUntagged = ((String[]) response.getEntity())[1];

        System.out.println("NEW EGRESS: " + newEgress);
        System.out.println("NEW UNTAGGED: " + newUntagged);

        for (int i = 0; i < ports.size(); i++) {

            StringBuilder newEgressBuilder = new StringBuilder(newEgress);
            newEgressBuilder.setCharAt(ports.get(i) - 1, '1');
            newEgress = newEgressBuilder.toString();

            StringBuilder newUntaggedBuilder = new StringBuilder(newUntagged);
            newUntaggedBuilder.setCharAt(ports.get(i) - 1, '0');
            newUntagged = newUntaggedBuilder.toString();
        }

        System.out.println("NEW EGRESS2: " + newEgress);
        System.out.println("NEW UNTAGGED2: " + newUntagged);

        response = setEgressPorts(vlanId, newEgress);

        if (response.getStatus() != 200) {
            errorHandler("addTrunkPort - setEgressPorts",
                    (String) response.getEntity());
        }

        System.out.println("SET EGRESS IN ADDTRUNK");
        System.out.println((String) response.getEntity());

        response = setUntaggedPorts(vlanId, newUntagged);

        if (response.getStatus() != 200) {
            errorHandler("addTrunkPort - setUntaggedPorts",
                    (String) response.getEntity());
        }

        System.out.println("SET UNTAGGED IN ADDTRUNK");
        System.out.println((String) response.getEntity());

        return Response.ok().build();

    }

    /**
     * Sets the RowStatus of the given VLan.
     * 
     * <p>
     * <code>
     * 1:active(1) <br>
     * 2:notInService(2) <br>
     * 3:notReady(3) <br>
     * 4:createAndGo(4) <br>
     * 5:createAndWait(5) <br>
     * 6:destroy(6) <br>
     * </code>
     * 
     * @param vlanId
     * @param rowStatus
     * @return
     */
    public Response setRowStatus(int vlanId, int rowStatus) {

        String rowStatusOID = ".1.3.6.1.2.1.17.7.1.4.3.1.5." + vlanId;
        Variable rowVar = new Integer32(rowStatus);

        ResponseEvent responseEvent = set(rowStatusOID, rowVar);

        return setHandler(responseEvent, "setRowStatus");
    }

    /**
     * 
     * @param vlanId
     * @param name
     * @return
     */
    public Response setStaticName(int vlanId, String name) {

        String staticNameOID = ".1.3.6.1.2.1.17.7.1.4.3.1.1." + vlanId;
        Variable nameVar = new OctetString(name);

        ResponseEvent responseEvent = set(staticNameOID, nameVar);

        return setHandler(responseEvent, "setStaticName");
    }

    /**
     * 
     * @param vlanId
     * @return
     */
    public Response getUntaggedPorts(int vlanId) {
        try {
            String untaggedPorts = ".1.3.6.1.2.1.17.7.1.4.3.1.4." + vlanId;

            ResponseEvent event = get(untaggedPorts);

            if (event != null && event.getResponse() != null) {
                String varString = event.getResponse().get(0).getVariable().toString();

                if (varString.equals("noSuchObject") || varString.equals("noSuchInstance")) {

                    return Response.status(500).entity(varString).build();
                }

                varString = varString.substring(0, 2);
                varString = new BigInteger(varString, 16).toString(2);
                String output = String.format("%8s", varString).replace(' ', '0');
                return Response.ok(output).build();
            } else {
                return errorHandler("getUntaggedPorts",
                        "SNMP-GET timed out or IOException");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return errorHandler("getUntaggedPorts", e);
        }

    }

    /**
     * Returns the PVID of the given port.
     * 
     * <p>
     * 
     * @param port
     *            port to query
     * @return a Response Object with status code and message body.
     */
    public Response getPVID(int port) {
        try {
            String pvidOid = ".1.3.6.1.2.1.17.7.1.4.5.1.1." + port;
            System.out.println("CALLING GET PVID");
            ResponseEvent event = get(pvidOid);

            if (event != null && event.getResponse() != null) {
                String pvid = event.getResponse().get(0).getVariable().toString();
                System.out.println(pvid);
                if (pvid.equals("noSuchObject") || pvid.equals("noSuchInstance")) {

                    return Response.status(500).entity(pvid).build();
                }

                return Response.ok(pvid).build();
            } else {
                return errorHandler("getPVID",
                        "SNMP-GET timed out or IOException");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return errorHandler("getStaticName", e);
        }

    }

    /**
     * Returns all PVIDs from this NetComponent in form of an array of int.
     * 
     * For this implementation this is an array of length 8.
     * 
     * @return a outbound Response Object with status code and message body.
     */
    public Response getAllPvids() {
        try {
            ResponseEvent responseEvent = getBulk(".1.3.6.1.2.1.17.7.1.4.5.1.1", 8);
            int[] array = new int[8];

            if (responseEvent != null && responseEvent.getResponse() != null) {

                System.out.println(responseEvent.getResponse());

                Vector<? extends VariableBinding> vector = responseEvent.getResponse()
                        .getVariableBindings();
                int i = 0;
                for (VariableBinding vb : vector) {
                    System.out.println(vb.toString());
                    System.out.println(vb.getVariable());
                    array[i] = vb.getVariable().toInt();
                    i++;
                }
                System.out.println(Arrays.toString(array));
                return Response.status(200).entity(array).build();

            } else {
                return errorHandler("getPvids",
                        "SNMP-GET timed out or IOException");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return errorHandler("getAllPvids", e);
        }
    }

    public Response getEgressAndUntaggedPorts(int vlanId) {

        try {
            String egressPortsOID = ".1.3.6.1.2.1.17.7.1.4.3.1.2." + vlanId;
            String untaggedPortsOID = ".1.3.6.1.2.1.17.7.1.4.3.1.4." + vlanId;

            String[] getArray = { egressPortsOID, untaggedPortsOID };

            ResponseEvent event = get(getArray);

            if (event != null && event.getResponse() != null) {

                String egressPorts = event.getResponse().get(0).getVariable().toString();
                String untaggedPorts = event.getResponse().get(1).getVariable().toString();

                String[] returnArray = { egressPorts, untaggedPorts };

                if (egressPorts.equals("noSuchObject") || egressPorts.equals("noSuchInstance")) {

                    return Response.status(500).entity(returnArray).build();

                } else {

                    for (int i = 0; i < 2; i++) {
                        returnArray[i] = returnArray[i].substring(0, 2);
                        returnArray[i] = new BigInteger(returnArray[i], 16).toString(2);
                        returnArray[i] = String.format("%8s", returnArray[i]).replace(' ', '0');
                    }
                    return Response.ok(returnArray).build();
                }
            } else {
                return errorHandler("getEgressAndUntaggedPorts",
                        "SNMP-GET timed out or IOException");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return errorHandler("getEgressAndUntaggedPorts", e);
        }

    }

    /**
     * Returns the egress (outgoing) ports for this VLan.
     * 
     * Returns a String with length 8 (for 8 ports) where every char is either 0
     * or 1. 0 indicates that the port is not an egress port for the VLan, 1
     * indicates that the port is an egress port.
     * 
     * 
     * @param vlanId
     *            VLan ID to query for
     * @return Response Object with a String, with status code 200 or 500.
     */
    public Response getEgressPorts(int vlanId) {
        try {
            String egressPorts = ".1.3.6.1.2.1.17.7.1.4.3.1.2." + vlanId;

            ResponseEvent event = get(egressPorts);

            if (event != null && event.getResponse() != null) {

                String varString = event.getResponse().get(0).getVariable()
                        .toString();

                if (varString.equals("noSuchObject") || varString.equals("noSuchInstance")) {

                    return Response.status(500).entity(varString).build();

                } else {
                    varString = varString.substring(0, 2);
                    varString = new BigInteger(varString, 16).toString(2);
                    String output = String.format("%8s", varString).replace(' ', '0');
                    return Response.ok(output).build();
                }
            } else {
                return errorHandler("getEgressPorts",
                        "SNMP-GET timed out or IOException");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return errorHandler("getEgressPorts", e);
        }

    }

    public boolean isFree(int vlanId) {
        
        String staticOID = ".1.3.6.1.2.1.17.7.1.4.3.1.1." + vlanId;
        
        ResponseEvent event = get(staticOID);
        
        System.out.println(event.toString());
        System.out.println(event.getResponse());
        
        
        if(event != null) {
            String varString = event.getResponse().get(0).getVariable().toString();
       
            System.out.println(event.getResponse());
            
            System.out.println(varString);
            
            if(varString.equals("noSuchInstance")) {
                return true;
            }
            
        }
        
        return false;
        
    }
    
    
    
    /**
     * Returns the name of a given VLan ID.
     *
     * <p>
     * The name as String is packaged in a Response Object. If the VLan with the
     * given ID did not exist on the hardware, "noSuchInstance" is returned.
     * 
     * HTTP status codes:
     * <li>200: Requested information could be obtained, body has the name of
     * VLan or "noSuchInstance".</li>
     * <li>500: Requested information could not be obtained, boy contains
     * specific information.</li>
     *
     * <p>
     * 
     * @param vlanId
     *            VLan ID to query
     * @return a Outbound Response Object with status code and message body.
     */
    public Response getStaticName(int vlanId) {
        try {
            String staticName = ".1.3.6.1.2.1.17.7.1.4.3.1.1." + vlanId;

            ResponseEvent event = get(staticName);

            if (event != null && event.getResponse() != null) {
                String varString = event.getResponse().get(0).getVariable().toString();

                if (varString.equals("noSuchObject") || varString.equals("noSuchInstance")) {

                    return Response.status(500).entity(varString).build();
                }

                return Response.ok(varString).build();
            } else {
                return errorHandler("getStaticName",
                        "SNMP-GET timed out or IOException");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return errorHandler("getStaticName", e);
        }
    }

    private ResponseEvent get(String oidString) {
        String[] array = { oidString };
        return get(array);
    }

    private ResponseEvent get(String[] oidStrings) {

        OID oid;
        PDU pdu = new PDU();
        for (String s : oidStrings) {
            oid = new OID(s);
            pdu.add(new VariableBinding(oid));
        }
        pdu.setType(PDU.GET);

        ResponseEvent event;

        if (target.getAddress() == null) {
            System.out.println("WARNING: Address is null! SNMP-GET will fail!");
        }

        try {
            event = snmp.send(pdu, this.target, null);

            if (event == null) {
                System.out.println("EVENT IST NULL");
            } else {
                System.out.println("EVENTRESPONE = " + event.getResponse());
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return event;

    }

    /**
     * Sets a single variable affiliated with the oidString.
     * 
     * @param oidString
     * @param variable
     * @return
     */
    private ResponseEvent set(String oidString, Variable variable) {

        String[] oids = { oidString };
        Variable[] vars = { variable };
        return set(oids, vars);
    }

    /**
     * Sets the given variables with their affiliated oidStrings. The order in
     * both arrays must be the same.
     * 
     * @param oidStrings
     * @param variables
     * @return
     */
    private ResponseEvent set(String[] oidStrings, Variable[] variables) {
        OID oid;
        Variable var;
        VariableBinding varBind;
        PDU pdu = new PDU();
        int i = 0;
        for (String s : oidStrings) {
            oid = new OID(s);
            var = variables[i];
            varBind = new VariableBinding(oid, var);
            pdu.add(varBind);
            i++;
        }
        pdu.setType(PDU.SET);
        ResponseEvent response = null;

        try {
            response = snmp.set(pdu, target);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return response;

    }

    private ResponseEvent getBulk(String oidString, int entrys) {

        OID oid = new OID(oidString);
        PDU pdu = new PDU();

        pdu.add(new VariableBinding(oid));

        pdu.setType(PDU.GETBULK);
        pdu.setMaxRepetitions(entrys);

        ResponseEvent event = null;

        try {
            event = snmp.getBulk(pdu, target);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return event;
    }

    /**
     * 
     * @param responseEvent
     * @param method
     * @return
     */
    private Response setHandler(ResponseEvent responseEvent, String method) {

        if (responseEvent != null && responseEvent.getResponse() != null) {

            PDU responsePDU = responseEvent.getResponse();
            int errorStatus = responsePDU.getErrorStatus();

            if (errorStatus == PDU.noError) {
                return Response.ok(responsePDU.getVariableBindings().toString()).build();
            } else {

                int errorIndex = responsePDU.getErrorIndex();
                String errorStatusText = responsePDU.getErrorStatusText();

                String error = "Error: Failed Request \n"
                        + "ErrorStatus = '" + errorStatus + "' \n"
                        + "ErrorIndex = '" + errorIndex + "' \n"
                        + "ErrorStatus Text = '" + errorStatusText + "'";

                return errorHandler(method, error);

            }
        } else {
            return errorHandler(method,
                    "TimeOut, ResponseEvent or ResponsePDU was null");
        }
    }

    /**
     * Returns a Response Object with the location of the error (where input)
     * and the error(error String). Adds some format and method of the object,
     * which caused the error.
     * 
     * @param where
     *            which method caused the error
     * @param error
     *            what was the error
     * @return Response Object with status code 500
     */
    private Response errorHandler(String where, String error) {

        StringBuffer sb = new StringBuffer();

        sb.append("Error occured in mm.net in ").append(where).append(" on:\n");
        sb.append(this.toString());
        sb.append("Error: \n").append(error).append("\n")
                .append("End of error. \n");

        return Response.status(500).entity(sb.toString()).build();
    }

    /**
     * 
     * @param where
     * @param e
     * @return
     */
    private Response errorHandler(String where, Exception e) {

        StringBuffer sb = new StringBuffer();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        sb.append("Error occured in mm.net in ").append(where).append(" on:\n");
        sb.append(this.toString());
        sb.append("Error: \n").append(sw.toString()).append("\n").append("End of error. \n");

        return Response.status(500).entity(sb.toString()).build();
    }

    /**
     * Creates a CommunityTarget from the address of this NetGearSwitch.
     */
    private void createTarget() {

        System.out.println("Creating SNMP Target for : " + this.identifier);
        Address targetAddress = GenericAddress.parse(host);
        CommunityTarget tar = new CommunityTarget();

        tar.setCommunity(new OctetString(community));
        tar.setAddress(targetAddress);

        tar.setRetries(retries);
        tar.setTimeout(timeout);
        tar.setVersion(SnmpConstants.version2c);
        target = tar;

        if (tar.getAddress() == null) {
            System.out.println("WARNING: Target Adress is null!");
        }
        System.out.println(target.toString());

    }

    public LinkedList<Integer> getTrunks() {
        return trunks;
    }

   /**  public String toString(int vlanId) {

        String name = (String) getStaticName(vlanId).getEntity();
        String egress = (String) getEgressPorts(vlanId).getEntity();
        String untagged = (String) getUntaggedPorts(vlanId).getEntity();

        StringBuffer sb = new StringBuffer();
        sb.append("VLAN " + vlanId + ":\n");
        sb.append("Name : '" + name + "' \n");
        sb.append("Egress : '" + egress + "' \n");
        sb.append("Untagged : '" + untagged + "' \n");
        return sb.toString();
    } **/

    public String toString() {

        StringBuffer sb = new StringBuffer();

        sb.append("Identifier: '").append(this.identifier).append("'\n");
        sb.append("Host: '").append(this.host).append("'\n");
        sb.append("Trunks: '").append(this.trunks).append("'\n");
        sb.append("TargetAddress: '").append(this.target.getAddress()).append("'\n");
        sb.append("Community: '").append(this.community).append("'\n");
        sb.append("Retries: '").append(this.retries).append("'\n");
        sb.append("Timeout: '").append(this.timeout).append("'\n");

        return sb.toString();
    }

    public String getId() {
        return this.identifier;
    }

    public int getPorts() {
        return this.ports;
    }

}
