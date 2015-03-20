package mm.net.modeling;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.Response;

/**
 * This Interfaces describes all methods a NetComponent implementation must implement. 
 * After implementing you have to add your implemented constructor to the NetComponentFac.
 */
public interface NetComponent {

    public int getPorts();
    public String getId();
    
    /**
     * Starts the SNMP instance on the NetComponent. Call this before issuing SNMP commands
     * @return
     */
	public Response start();
	/**
	 * Stops the SNMP instance on the NetComponent. Call this after issuing all SNMP commands
	 * @return
	 */
	public Response stop();

	/**
	 * Returns the PVID from the given port.
	 * @param port
	 * @return an outbound response object with statuscoude(200, 500) and a message body
     *  with possible errorstring
	 */
	public Response getPVID(int port);
	
	public Response getAllPvids();
	
	/**
	 * Returns a ResponseObject with the Egress and Untagged ports.
	 * 
	 * <p>
	 * In the message body a String array is located with the size of 2. Both Strings
	 * are equal in length, which is equal to the number of ports located on the device.
	 * 
	 * A 1 at the i-th position in the String signals that the port with number i is
	 * an egress port or is untagged, where a 0 shows that the port is not an egress port
	 * and is tagged. 
	 * 
	 * @param vlandId
	 * @return  an outbound response object with statuscoude(200, 500) and a message body
     *  with possible errorstring
	 */
	public Response getEgressAndUntaggedPorts(int vlandId);
	
	/**
	 * Sets the PVID/Native VLAN ID for the given port
	 * @param port
	 * @param pvid
	 * @return a outbound response object with statuscoude(200, 500) and a message body
     *  with possible errorstring
	 */
	public Response setPVID(int port, int pvid);
	
	/**
	 * Returns all Trunkports from this NetComponent.
	 * @return  list of all trunkports on this netcomponent
	 */
    public LinkedList<Integer> getTrunks();
    
	/**
	 * Adds a port as an untagged port to an existing VLAN. 
	 * 
	 * <p>
	 * Therefore all the previous assigned ports are still members of 
	 * this VLAN. The PVID/native VLAN ID of this port is set to the VLAN ID. 
	 * So every package coming in on this port are assigned to the given VLAN
	 * 
	  <p>
     * If the VLAN does not exist, it is NOT created, only a 500 status code is returned.
     *
	 * @param port  portnumber
	 * @param vlanId  specifes the VLAN to which the port should be added
	 * @return  a outbound response object
	 */
	public Response addPort(int port, int vlanId);
	
	/**
     * Adds a list of ports as an untagged ports to an existing VLAN. 
     * 
     * <p>
     * Therefore all the previous assigned ports are still members of 
     * this VLAN. The PVID/native VLAN ID of the ports is set to the VLAN ID. 
     * So every package coming in on these ports is assigned to the given VLAN.
     * 
     * <p>
     * If the VLAN does not exist, it is NOT created, only a 500 status code is returned.
     * 
     * @param port  list of portnumbers
     * @param vlanId  specifes the VLAN to which the ports should be added
     * @return  a outbound response object with statuscoude(200, 500) and a message body
     *  with possible errorstring
     */
	public Response addPort(List<Integer> port, int vlanId);
	
	/**
	 * Creates a new VLAN with the given port and name on the vlanId.
	 * 
	 * If the VLAN already exists it is destroyed and the provided configuration is
	 * applied. All ports are set as untagged ports. The PVID/Native VLAN ID is set
	 * accordingly
	 * 
	 * @param port
	 * @param vlanId
	 * @param name
	 * @return a outbound response object with statuscode(200, 500) and a message body
     *  with possible errorstring
	 */
	public Response setPort(int port, int vlanId, String name);
	/**
     * Creates a new VLAN with the given list of ports and name on the vlanId.
     * 
     * If the VLAN already exists it is destroyed and the provided configuration is
     * applied. All ports are set as untagged ports. The PVID/Native VLAN ID is set
     * accordingly
     * 
     * @param port
     * @param vlanId
     * @param name
     * @return a outbound response object with statuscode(200, 500) and a message body
     *  with possible errorstring
     */
    public Response setPort(List<Integer> port, int vlanId, String name);
	
    /**
     * Removes a port from the given VLAN, does not affect the other ports, as they are
     * still remain as members. PVID of this port is changed to 1.
     * @param port  portnumber to remove
     * @param vlanId  from which VLAN the port should be removed 
     * @return  a outbound response object with statuscode (200, 500) and a message body
     */
	public Response removePort(int port, int vlanId);
    
	/**
     * Removes ports from the given VLAN, does not affect the other ports, as they are
     * still remain as members. PVID of the ports is changed to 1.
     * @param port  list of portnumbers to remove
     * @param vlanId  from which VLAN the ports should be removed 
     * @return  a outbound response object with statuscode (200, 500) and a message body
     */
	public Response removePort(List<Integer> port, int vlanId);
    
    /**
     * Adds a port as trunk port to an existing VLAN. 
     *
     * <p>
     * Does not change the membership of other ports. 
     * The port is (T) tagged and the PVID/native VLAN ID is 1. 
     * If the port is already a member of the provided VLAN the configuration is replaced
     * by the provided one.
     * 
     * @param port
     * @param vlanId
     * @return a outbound response object with statuscode(200, 500) and a message body
     *  with possible errorstring
     */
    public Response addTrunkPort(int port, int vlanId);
    /**
     * Adds a list pf ports as trunk port to an existing VLAN. 
     * 
     * <p>
     * Does not change the membership of other ports. The port is (T) tagged and the PVID/native VLAN ID is 1. 
     * If a port is already member of the provided VLAN the configuration is replaced by the provided
     * one
     * 
     * @param port
     * @param vlanId
     * @return an outbound response object with statuscode(200, 500) and a message body
     *  with possible errorstring
     */
    public Response addTrunkPort(List<Integer> port, int vlanId);
	
    /**
     * Creates a new VLAN with the given port as (T) tagged trunk port.
     * 
     * <p>
     * If the given VLAN ID is not used a new VLAN is created. 
     * If the ID is currently used its configuration is replaced by the provided one.
     * @param port
     * @param vlanId
     * @param name
     * @return an outbound response object with statuscode(200, 500) and a message body
     *  with possible errorstring
     */
	public Response setTrunkPort(int port, int vlanId, String name);
	
	/**
	 * Creates a new VLAN with the given ports as (T) tagged trunk ports.
	 * 
	 * <p>
     * If the given VLAN ID is not used a new VLAN is created. 
     * If the ID is currently used its configuration is replaced by the provided one.
	 * @param port
	 * @param vlanId
	 * @param name
	 * @return  an outbound response object with statuscode(200, 500) and a message body
     *  with possible errorstring
	 */
	public Response setTrunkPort(List<Integer> port, int vlanId, String name);
	
	
	/**
	 * Destroys a VLAN on this NetComponent. Does NOT change the PVID/Native ID.
	 * 
	 * @param vlanId  VLAN ID to destroy
	 * @return an outbound response object with statuscode(200, 500) and a message body
     *  with possible errorstring
	 */
    public Response destroyVlan(int vlanId);

}
