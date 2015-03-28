package mm.controller.modeling;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mm.controller.main.ControllerData;
import mm.controller.net.ControllerNetGet;

public class NodeObjects {

    private String id;
    private String typeName;
    private LinkedList<Component> components = new LinkedList<Component>();
    private LinkedList<Config> applicableConfigs = new LinkedList<Config>();
    private String building;
    private String room;
    private String latitude;
    private String longitude;
    private boolean status;
    //private String trunk;
    private Config config;
    
    
	public NodeObjects() {
	}

	public NodeObjects(String id) {
		this.id = id;
	}
	
	public boolean getStatus() {
	    return this.status;
	}
	
	public Config getConfig() {
        return config;
    }

    public void setConfig(Config config2) {
        this.config = config2;
    }

    /**
	 * Updates a the PowerStatus of the Node with the information from the given
	 * list of PowerSources. Changes the power status in every component and the
	 * power status inside of the node itself.
	 * 
	 * power status inside the node is true, when at least one component from this 
	 * node has the power status true
	 * 
	 * @param statusList
	 *            a list with PowerSources
	 */
	public void updateNodeStatusPower(LinkedList<PowerSource> statusList) {

	    boolean nodeStatus = false;
	    
		for (PowerSource powerSource : statusList) {
			for (Component component : components) {
				if (component.getPowerSource().equals(powerSource.getId())) {
					component.setStatus(powerSource.getStatus());
					
					if(!powerSource.getStatus()) {
					    nodeStatus = true;
					}
				}
			}
		}
	
		this.status = nodeStatus;
	
	}
	
	/**
	 * Returns a Set of all Switchports which are connected to this node
	 * @return
	 */
	public Set<String> getAllSwitchPorts() {
	    
	    Set<String> set = new HashSet<String>();
	    
	    for (Component component : components) {
            for (Interface inf : component.getInterfaces()) {
                set.add(inf.getSwitchport());
            }
        }
	    return set;
	}
	public Set<String> getRoles() {
	    
	    System.out.println("GET ROLES IN NODEOBJECT");
	    System.out.println("COMPONENTS " + components);
	    
	    Set<String> roles = new HashSet<String>();
	    for (Component component : components) {
	        for (Interface inf : component.getInterfaces()) {
	            roles.add(inf.getRole());
            }
        }
	    return roles;
	}
	
	/**
	 * Returns the Port which is connected as the described role or null if no port has such a role.
	 * @param role
	 * @return
	 */
	public String getPortByRole(String role) {
	    
	    for (Component component : components) {
            for (Interface inf : component.getInterfaces()) {
                if(inf.getRole().equals(role)) {
                    return inf.getSwitchport();
                }
            }
        }
	   return null;
	}
	
	/**
	 * Returns the Trunkport which is serving the role in the node. 
	 * @param role  the role you want to access via the returned trunkport
	 * @return  the trunkport of the role, null if there is no such role in the node
	 */
	public String getTrunkPortByRole(String role) {
	    
	    for (Component component : components) {
            for (Interface inf : component.getInterfaces()) {
                if(inf.getRole().equals(role)) {
                    return component.getTrunkport();
                }
            }
        }
       return null;
	}
	
	/**
	 * Checks if a Config is applicable for the node.
	 * 
	 * Every Role from the Config has to be present in the Node.
	 * @param config
	 * @return
	 */
	public boolean isApplicable(Config config) {
	    
	    Set<String> configRoles = config.getRoles();
	    Set<String> nodeRoles = this.getRoles();
	    
	    System.out.println("CONFIG ROLES: " + configRoles);
	    System.out.println("NODE ROLES: " + nodeRoles);
	    
	    for (String configRole : configRoles) {
	        if(!nodeRoles.contains(configRole)) {
                return false;
            }
	    }
	    return true;
	}
	
	/**
	 * Gets the list of all Configs and calculates all applicable Configs for the node
	 * and adds them to the internal list
	 */
	public void calcApplicableConfigs() {
	    
	    applicableConfigs.clear();
	    
	    for(Config config : ControllerData.getAllConfigs()) {
	        if(this.isApplicable(config)) {
	            applicableConfigs.add(config);
	        }
	    }
	    
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
	 * Checks if a Node is currently available.
	 * 
	 * Availability of a node means that every port of this node is not affiliated with a 
	 * PVID/Native VLan ID. If only one port has a PVID on the NetComponent then the whole node
	 * is not available for use.
	 * 
	 * Possible HTTP status codes: 
	 * 
	 * <li> 200: The Node is free to use
	 * <li> 403: The Node is currently used by another user. The Node ID and the User are
	 * described in the message body, if the user can be determined. 
	 * <li> 500: The Controller could not retrieve the requested information a specified error
	 * description is located in the message body
	 * 
	 * @return  Returns an outbound Response Object with status code and message body
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
                if(exp == null) {
                    responseStatus = 403;
                    responseString = "Node is currently used, but could not identify the user, "
                            + "because the VLan ID '" + vlanId + "' is not in the system.";
                } else {
                responseString = "Node '" + this.id + "' is currently used by User: '" + exp.getUser() + "'";
                responseStatus = 403;
                    }
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

    /** public void setTrunk(String trunk) {
       this.trunk = trunk;
    }
    
    public String getTrunk() {
      return this.trunk;
    } **/
	
}
