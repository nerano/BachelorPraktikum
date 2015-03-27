package mm.controller.modeling;

import java.util.LinkedList;

import javax.ws.rs.core.Response;

import mm.controller.power.ControllerPowerPut;

public class Component {

	private String type;
	private String ports;
	private LinkedList<Interface> interfaces = new LinkedList<Interface>();
    private String powerSource;
	private boolean status;
	private int vLanIds;
	private String trunkPort;

	public Component(String type) {
		this.type = type;
		// this.vLanId;
		// this.statusfalse;
	}

	public void setTrunk(String trunk) {
	    this.trunkPort = trunk;
	}
	
	public String getTrunkport() {
	    return this.trunkPort;
	}
	
	public void addInterface(Interface interf) {
        interfaces.add(interf);
    }
    
    public LinkedList<Interface> getInterfaces() {
        return interfaces;
    }
    
    public void setInterfaces(LinkedList<Interface> interfaces) {
        this.interfaces = interfaces;
    }
	
	public void setPowerSource(String powerSource) {
		this.powerSource = powerSource;
	}

	public String getPowerSource() {
		return this.powerSource;
	}

	protected int getvLanId() {
		return vLanIds;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
	
	public boolean getStatus() {
	  return this.status;
	}

	public void setvLanId(int vLanId) {
		this.vLanIds = vLanId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPort() {
		return ports;
	}

	public void setPort(String port) {
		this.ports = port;
	}
	
	
	/**
	 * Turns off this component.
	 * <p>
	 * To turn off the component the affiliated PowerSource from this component 
	 * submitted to the PowerPut Module in the Controller.
	 * @return a Response Object with a status code and in some cases a message body
	 */
	public Response turnOn() {

		StringBuffer buffer = new StringBuffer();

		buffer.append(powerSource);

		if (buffer.charAt(buffer.length() - 1) != ';') {
			buffer.append(";");
		}

		buffer.append("end");

		Response response = ControllerPowerPut.turnOn(buffer.toString());
		
		return response;

	}
	
	/**
	 * Turns off this component.
	 * <p>
	 * To turn off the component the affiliated PowerSource from this component 
	 * submitted to the PowerPut Module in the Controller.
	 * @return a Response Object with a status code and in some cases a message body
	 */
	public Response turnOff() {

		StringBuffer buffer = new StringBuffer();

		buffer.append(powerSource);

		if (buffer.charAt(buffer.length() - 1) != ';') {
			buffer.append(";");
		}

		buffer.append("end");

		Response response = ControllerPowerPut.turnOff(buffer.toString());

		return response;

	}
	
	public String toString() {
        
        StringBuffer sb = new StringBuffer();
        
        sb.append("Type: '").append(type).append("'\n");
        sb.append("PowerSource: '").append(powerSource).append("'\n");
        
        for (Interface interface1 : interfaces) {
            sb.append("Interface: '").append(interface1.toString()).append("'\n");
        }
        
        return sb.toString();
        
    }
}
