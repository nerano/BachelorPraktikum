package mm.controller.modeling;

import java.util.LinkedList;

import javax.ws.rs.core.Response;

import mm.controller.main.ControllerData;

public class NodeObjects {

  private String                id;
  private String                typeName;
  private LinkedList<Component> components = new LinkedList<Component>();
  private String                building;
  private String                room;
  private String                latitude;
  private String                longitude;

  public NodeObjects() {
  }

  /**
   * Updates a the PowerStatus of the Node with the information from the given
   * list of PowerSources
   * 
   * @param statusList
   *          a list with PowerSources
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

  public boolean turnOff() {
    boolean bool = true;

    for (Component component : components) {
      bool = component.turnOff();
    }
    return bool;
  }

  public boolean turnOn() {
    boolean bool = true;

    for (Component component : components) {
      bool = component.turnOn();
    }
    return bool;
  }

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
   * @param comp String with ComponentType to turn off
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

  public NodeObjects(String id) {
    this.id = id;
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

}
