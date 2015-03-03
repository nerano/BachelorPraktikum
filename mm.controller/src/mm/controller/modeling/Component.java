package mm.controller.modeling;

import javax.ws.rs.core.Response;

import mm.controller.power.ControllerPowerPut;

public class Component {

	private String type;
	@SuppressWarnings("unused")
	private boolean status;
	private int vLanIds;
	private String ports;
	private String powerSource;
	private transient ControllerPowerPut powerPut = new ControllerPowerPut();

	public Component(String type) {
		this.type = type;
		// this.vLanId;
		// this.statusfalse;
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

		Response r = powerPut.turnOn(buffer.toString());
		
		return r;

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

		Response r = powerPut.turnOff(buffer.toString());

		return r;

	}

}
