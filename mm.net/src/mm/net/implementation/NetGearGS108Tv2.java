package mm.net.implementation;

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

public class NetGearGS108Tv2 {

	private String host;
	private String identifier;
	private String community = "private";
	private Snmp snmp = null;
	private int retries = 2;
	private int timeout = 1500;
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

			if (event != null) {
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

	/**
	 * Returns the egress (outgoing) ports for this VLan.
	 * 
	 * Returns a String with length 8 (for 8 ports) where every char is either 0 or 1. 
	 * 0 indicates that the port is not an egress port for the VLan, 1 indicates that the port
	 * is an egress port. 
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

			if (event != null) {
				String s = event.getResponse().get(0).getVariable().toString();
				s = s.substring(0, 2);
				s = new BigInteger(s, 16).toString(2);
				return Response.ok(s).build();
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
	 * 
	 * @param vlanId
	 * @return
	 */
	public Response getUntaggedPorts(int vlanId) {
		try {
			String egressPorts = ".1.3.6.1.2.1.17.7.1.4.3.1.4." + vlanId;

			ResponseEvent event = get(egressPorts);

			if (event != null) {
				String s = event.getResponse().get(0).getVariable().toString();
				s = s.substring(0, 2);
				s = new BigInteger(s, 16).toString(2);
				return Response.ok(s).build();
			} else {
				return errorHandler("getUntaggedPorts",
						"SNMP-GET timed out or IOException");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return errorHandler("getUntaggedPorts", e);
		}

	}
	
	public Response setEgressPorts(int vlanId) {
		
		String egress = ".1.3.6.1.2.1.17.7.1.4.3.1.2.50";
		OID oid = new OID(egress);
		
		Variable var;
		PDU pdu = new PDU();
		
		var = OctetString.fromHexString("EF");
		
		VariableBinding varB = new VariableBinding(oid, var);
		
		pdu.add(varB);
		pdu.setType(PDU.SET);
		pdu.setRequestID(new Integer32(100));
	
		ResponseEvent response = snmp.set(pdu, target);
		
		
		
	}
	
	private ResponseEvent set(String oidString, Variable variable) {
		
		String[] o = {oidString };
		Variable[] v = { variable };
		return set(o, v);
	}
	
	private ResponseEvent set(String[] oidStrings, Variable[] variables) {
		OID oid;
		Variable var;
		VariableBinding varBind;
		PDU pdu = new PDU();
		int i = 0;
		for(String s : oidStrings) {
			oid = new OID(s);
			var = variables[i];
		    varBind = new VariableBinding(oid, var);
			pdu.add(varBind);
			i++;
		}
		pdu.setType(PDU.SET);
		
		try {
		ResponseEvent response = snmp.set(pdu, target); 
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return response;
		
	
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
		sb.append("Error: \n").append(error).append("End of error. \n");

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
		sb.append("Error: \n").append(sw.toString()).append("End of error. \n");

		return Response.status(500).entity(sb.toString()).build();
	}

	/**
	 * Creates a CommunityTarget from the address of this NetGearSwitch.
	 */
	private void createTarget() {

		Address targetAddress = GenericAddress.parse(host);
		CommunityTarget t = new CommunityTarget();

		t.setCommunity(new OctetString(community));
		t.setAddress(targetAddress);

		t.setRetries(retries);
		t.setTimeout(timeout);
		t.setVersion(SnmpConstants.version2c);
		target = t;
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

}
