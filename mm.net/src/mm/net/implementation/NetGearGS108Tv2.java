package mm.net.implementation;

import mm.net.modeling.NetComponent;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;

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
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class NetGearGS108Tv2 implements NetComponent {

    private String host;
    private String identifier;
    private String community = "private";
    private Snmp   snmp      = null;
    private int    retries   = 2;
    private int    timeout   = 1500;
    private Target target;

    /**
     * 
     * @param identifier
     * @param add
     */
    public NetGearGS108Tv2(String identifier, String add) {

        this.identifier = identifier;
        host = "udp:" + add + "/161";

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

        if (!(varString.length() != 8 || varString.length() != 2)) {
            return errorHandler("setEgressPorts",
                    "Invalid format, should be 2 in hexadecimal or 8 in binary,"
                            + " but was '" + varString + "' \n");
        }

        if (varString.length() == 8) {
            varString = String.format("%02x", Integer.parseInt(varString, 2));
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

        if (!(varString.length() != 8 || varString.length() != 2)) {
            return errorHandler("setUntaggedPorts",
                    "Invalid format, should be 2 in hexadecimal or 8 in binary, "
                            + "but was '" + varString + "' \n");
        }

        if (varString.length() == 8) {
            varString = String.format("%02x", Integer.parseInt(varString, 2));
        }

        String egress = ".1.3.6.1.2.1.17.7.1.4.3.1.4." + vlanId;
        Variable variable = OctetString.fromHexString(varString);

        System.out.println(variable);
        ResponseEvent responseEvent = set(egress, variable);

        return setHandler(responseEvent, "setUntaggedPorts");

    }
    
    @Override
    public Response setVLan(String config) {
        // TODO Auto-generated method stub
        return null;
    }

    
    @Override
    public Response setVLan(String ports, int vlanId, String pvid) {

        // TODO Auto-generated method stub
        return null;
    }
    
    /**
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
            String egressPorts = ".1.3.6.1.2.1.17.7.1.4.3.1.4." + vlanId;

            ResponseEvent event = get(egressPorts);

            if (event != null && event.getResponse() != null) {
                // TODO noSuchObject handling
                String varString = event.getResponse().get(0).getVariable()
                        .toString();
                varString = Integer.toBinaryString(Integer.parseInt(
                        varString.substring(0, 2), 16));
                return Response.ok(varString).build();
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
     * Returns the VLan ID of the given port.
     * 
     * <p>
     * 
     * @param port
     *            port to query
     * @return a Response Object with status code and message body.
     */
    public Response getPVID(int port) {
        try {
            String pvidOID = ".1.3.6.1.2.1.17.7.1.4.5.1.1." + port;

            ResponseEvent event = get(pvidOID);

            if (event != null && event.getResponse() != null) {
                // TODO noSUchObject handling
                int pvid = event.getResponse().get(0).getVariable().toInt();
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

                if (varString.equals("noSuchObject")
                        || varString.equals("noSuchInstance")) {

                    return Response.status(500).entity(varString).build();

                } else {
                    varString = varString.substring(0, 2);
                    varString = new BigInteger(varString, 16).toString(2);

                    String output = String.format("%08s", varString).replace(
                            ' ', '0');
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

    /**
     * Returns the name of a given VLan ID.
     * <p>
     * The name as String is packaged in a Response Object. If the VLan with the
     * given ID did not exist on the hardware, "noSuchInstance" is returned.
     * 
     * HTTP status codes:
     * <li>200: Requested information could be obtained, body has the name of
     * VLan or "noSuchInstance".</li>
     * <li>500: Requested information could not be obtained, boy contains
     * specific information.</li>
     * <p>
     * 
     * @param vlanId
     *            VLan ID to query
     * @return a Response Object with status code and message body.
     */
    public Response getStaticName(int vlanId) {
        try {
            String staticName = ".1.3.6.1.2.1.17.7.1.4.3.1.1." + vlanId;

            ResponseEvent event = get(staticName);

            if (event != null && event.getResponse() != null) {
                // TODO DAS IST KEIN GUTES ERRORHANDLING DA DAS EVENT BEI
                // TIMEOUT NICHT NULL IST
                // TODO noSuchObject handlin
                if (event.getResponse() == null) {
                    System.out.println("HALLO DAS IST NULL");
                }
                String s = event.getResponse().get(0).getVariable().toString();
                return Response.ok(s).build();
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
        String[] a = { oidString };
        return get(a);
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

        try {
            event = snmp.send(pdu, this.target, null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return event;

    }

    private ResponseEvent set(String oidString, Variable variable) {

        String[] oids = { oidString };
        Variable[] vars = { variable };
        return set(oids, vars);
    }

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
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return response;

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

        Address targetAddress = GenericAddress.parse(host);
        CommunityTarget tar = new CommunityTarget();

        tar.setCommunity(new OctetString(community));
        tar.setAddress(targetAddress);

        tar.setRetries(retries);
        tar.setTimeout(timeout);
        tar.setVersion(SnmpConstants.version2c);
        target = tar;
        System.out.println(target.toString());

    }

    public String toString() {

        StringBuffer sb = new StringBuffer();

        sb.append("Identifier: '").append(this.identifier).append("'\n");
        sb.append("Host: '").append(this.host).append("'\n");
        sb.append("TargetAddress: '").append(this.target.getAddress()).append("'\n");
        sb.append("Community: '").append(this.community).append("'\n");
        sb.append("Retries: '").append(this.retries).append("'\n");
        sb.append("Timeout: '").append(this.timeout).append("'\n");

        return sb.toString();
    }

    // /////////////////////////// WORK ON THIS
    // ////////////////////////////////////////

    private String not(String a) {
        int ai = Integer.parseInt(a, 2);
        int result = ~ai & 0xff;
        // System.out.println(ai);

        String output = String.format("%8s", Integer.toBinaryString(result))
                .replace(' ', '0');
        // System.out.println(output);
        return output;
    }

    private String and(String a, String b) {

        int ai = Integer.parseInt(a, 2);
        int bi = Integer.parseInt(b, 2);

        int result = ai & bi;

        String output = String.format("%8s", Integer.toBinaryString(result))
                .replace(' ', '0');

        return output;

    }
}
