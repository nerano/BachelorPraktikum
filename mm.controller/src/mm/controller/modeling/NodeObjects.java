package mm.controller.modeling;

import java.util.LinkedList;

import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mm.controller.main.ControllerData;
import mm.controller.net.ControllerNetGet;

public class NodeObjects {

    private String id;
    private String typeName;
    private LinkedList<Component> components = new LinkedList<Component>();
    private String building;
    private String room;
    private String latitude;
    private String longitude;
    private boolean status = false;
    private String trunk;
    private String config;
    
    
	public NodeObjects() {
	}

	public NodeObjects(String id) {
		this.id = id;
	}
	
	
	
	public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    /**
	 * Updates a the PowerStatus of the Node with the information from the given
	 * list of PowerSources
	 * 
	 * @param statusList
	 *            a list with PowerSources
	 */
	public void updateNodeStatusPower(LinkedList<PowerSource> statusList) {

	    boolean nodeStatus = true;
	    
		for (PowerSource powerSource : statusList) {
			for (Component component : components) {
				if (component.getPowerSource().equals(powerSource.getId())) {
					component.setStatus(powerSource.getStatus());
					
					if(!powerSource.getStatus()) {
					    nodeStatus = false;
					}
				}
			}
		}
	
		this.status = nodeStatus;
	
	}

	/**
	 * Turns off all components of this node.
	 * <p>
	 * 
	 * @return a Outbound Response Object with a HTTP status code and on an error case a
	 *         message body.
	 */
	public Response turnOff() {
		StringBuffer sb = new StringBuffer();
		boolean bool = true;
		for (Component component : components) {
			Response response = component.turnOff();

			if (response.getStatus() != 200) {
				bool = false;
				sb.append("Error on " + component.getType() + "\n");
				sb.append(response.readEntity(String.class)).append("\n");
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
	 * @return a Outbund Response Object with a HTTP status code and on an error case a
	 *         message body.
	 */
	public Response turnOn() {
		StringBuffer sb = new StringBuffer();
		boolean bool = true;
		for (Component component : components) {
			Response response = component.turnOn();

			if (response.getStatus() != 200) {
				bool = false;
				sb.append("Error on " + component.getType() + "\n");
				sb.append(response.readEntity(String.class)).append("\n");
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
				Response response = component.turnOn();
				return response;
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
				Response response = component.turnOff();
				return response;
			}
		}
		return Response.status(404).entity("404, Component not found").build();
	}

	
	/**
	 * Checks if a Node is currently available, which means none of the ports from the
	 * node are active in any VLan.
	 * 
	 * 
	 * @return
	 */
	public Response isAvailable() {
	    
	    LinkedList<Interface> infList = ControllerNetGet.getVlanInfo(this);
	    int responseStatus = 200;
	    String responseString = null;
	    int vlanId;
	    LinkedList<Interface> returnList = new LinkedList<Interface>();
	    Experiment exp;
	    
	    for (Interface inf : infList) {
            vlanId = inf.getVlanId();
	        switch (vlanId) {
            case 0:
                responseStatus = 500;
                returnList.add(inf);
                break;
            case 1:
                break;
            default:
                exp = ControllerData.getExpByVlanId(vlanId);
                responseString = exp.getUser();
                responseStatus = 403;
                break;
            }
        }
	    
	    if(responseStatus == 500) {
	        Gson gson = new GsonBuilder().setPrettyPrinting().create();
	        responseString = gson.toJson(returnList);
	    }
	    
	    if(responseStatus == 200) {
	        responseString = "Node is free to use!";
	    }
	    
	   return Response.status(responseStatus).entity(responseString).build();
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
            sb.append(component.toString());
        }
        
        sb.append("Room: '").append(room).append("' \n");
        sb.append("Building: '").append(building).append("' \n");
        
        
        return sb.toString();
    }

    public void setTrunk(String trunk) {
        this.trunk = trunk;
    }
    
    public String getTrunk() {
        return this.trunk;
    }
	
}
