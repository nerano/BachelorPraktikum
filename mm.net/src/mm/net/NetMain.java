package mm.net;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

@Path("/netmain")
public class NetMain {

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String sayTest() throws IOException{
   
	  Address targetAddress = GenericAddress.parse("udp:192.168.178.94/161");
		
		
		TransportMapping transportUDP = new DefaultUdpTransportMapping();
		//TransportMapping transportTCP = new DefaultTcpTransportMapping();
		
		Snmp snmp = new Snmp(transportUDP);
		
		USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);

		SecurityModels.getInstance().addSecurityModel(usm);
		transportUDP.listen();


		// Adding new User to USM
		snmp.getUSM().addUser(new OctetString("admin"),
							  new UsmUser(new OctetString("admin"),
									AuthMD5.ID,
									new OctetString("password"),
									PrivDES.ID,
									new OctetString("password")));
									
		//UsmUser admin = new UsmUser(new OctetString("admin"), AuthMD5.ID, new OctetString("password"), PrivDES.ID, new OctetString("password"));		
		
		// snmp.getUSM().addUser(admin);
		
		// Creating Target (Switch)
		
		UserTarget target = new UserTarget();
		target.setAddress(targetAddress);
		target.setRetries(1);
		target.setTimeout(5000);
		target.setVersion(SnmpConstants.version3);
		target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
		target.setSecurityName(new OctetString("admin"));
		
		// Creating PDU
		
		PDU pdu = new ScopedPDU();
		pdu.add(new VariableBinding(new OID(".1.3.6.1.4.1.4526.10.1.1.1.3")));
		pdu.setType(pdu.GET);
		
		// send PDU to router
		ResponseEvent response = snmp.get(pdu, target);
		//extract the response
		
		PDU responsePDU = response.getResponse();
		PDU requestPDU = response.getRequest();
		
		// extract the address used by the agent used to send the response 
		Address peerAddress = response.getPeerAddress();
		
		
		
		
		System.out.println(peerAddress);
		
		System.out.println(responsePDU.toString());
		
		System.out.println(requestPDU.toString());
		
	  
	  return "NETtest";
  }
}
