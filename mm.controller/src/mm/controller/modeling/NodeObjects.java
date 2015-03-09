package mm.controller.modeling;

import java.util.LinkedList;

import javax.ws.rs.core.Response;

import mm.controller.main.ControllerData;

public class NodeObjects {

	private String id;
	private String typeName;
	private LinkedList<Component> components = new LinkedList<Component>();
	private String building;
	private String room;
	private String latitude;
	private String longitude;
	private boolean status = false;
	
	public NodeObjects() {
	}

	public NodeObjects(String id) {
		this.id = id;
	}
	
	/**
	 * Updates a the PowerStatus of the Node with the information from the given
	 * list of PowerSources
	 * 
	 * @param statusList
	 *            a list with PowerSources
	 */
	public void updateNodeStatusPower(LinkedList<PowerSource> statusList) {

		for (PowerSource powerSource : statusList) {
			for (Component component : components) {
				if (component.getPowerSource().equals(powerSource.getId())) {
					component.setStatus(powerSource.getStatus());
				}
			}
		}
	}

	public void updateNodeStatusVLan(LinkedList<VLan> vlanList) {

		LinkedList<String> portList;
		Component component;

		for (VLan vLan : vlanList) {
			portList = vLan.getPortList();

			for (String string : portList) {
				component = ControllerData.getComponentByPort(string);
				if (component != null) {
					component.setvLanId(vLan.getId());
				}
			}

		}

	}

	/**
	 * Turns off all components of this node.
	 * <p>
	 * 
	 * @return a Response Object with a HTTP status code and on an error case a
	 *         message body.
	 */
	public Response turnOff() {
		StringBuffer sb = new StringBuffer();
		boolean bool = true;
		for (Component component : components) {
			Response r = component.turnOff();

			if (r.getStatus() != 200) {
				bool = false;
				sb.append("Error on " + component.getType() + "\n");
				sb.append(r.readEntity(String.class)).append("\n");
			}
		}
		if (bool) {
			return Response.ok().build();
		} else {
			return Response.status(500).encoding(sb.toString()).build();
		}
	}

	/**
	 * Turns on all components of this node.
	 * <p>
	 * 
	 * @return a Response Object with a HTTP status code and on an error case a
	 *         message body.
	 */
	public Response turnOn() {
		StringBuffer sb = new StringBuffer();
		boolean bool = true;
		for (Component component : components) {
			Response r = component.turnOn();

			if (r.getStatus() != 200) {
				bool = false;
				sb.append("Error on " + component.getType() + "\n");
				sb.append((String) r.getEntity()).append("\n");
			}
		}
		if (bool) {
			return Response.ok().build();
		} else {
			return Response.status(500).encoding(sb.toString()).build();
		}
	}

	/**
	 * Turns on a given component, located on the called node.
	 * <p>
	 * 
	 * @param comp
	 *            String with ComponentType to turn off
	 * @return a Response Object with status code and message body.
	 */
	public Response turnOn(String comp) {

		for (Component component : components) {
			if (component.getType().equals(comp)) {
				Response r = component.turnOn();
				return r;
			}
		}
		return Response.status(404).entity("404, Component not found").build();
	}

	/**
	 * Turns off a given component, located on the called node.
	 * <p>
	 * 
	 * @param comp
	 *            String with ComponentType to turn off
	 * @return a Response Object with status code and message body.
	 */
	public Response turnOff(String comp) {

		for (Component component : components) {
			if (component.getType().equals(comp)) {
				Response r = component.turnOff();
				return r;
			}
		}
		return Response.status(404).entity("404, Component not found").build();
	}

	

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNodeType() {
		return typeName;
	}

	public void setNodeType(String nodeType) {
		this.typeName = nodeType;
	}

	public void addComponent(Component comp) {
		this.components.add(comp);
	}

	public LinkedList<Component> getComponents() {
		return this.components;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	
	public String toString() {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("ID: '").append(id).append("'\n");
		sb.append("TypeName: '").append(typeName).append("' \n");
		sb.append("Components: \n");
		for (Component component : components) {
			sb.append("Component: '").append(component.toString());
		}
		sb.append("Room: '").append(room).append("' \n");
		sb.append("Building: '").append(building).append("' \n");
		
		
		return sb.toString();
	}
	
}
