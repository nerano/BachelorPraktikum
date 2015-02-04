package mm.controller.modeling;

import java.util.LinkedList;
import java.util.List;

public class NodeObjects {
    
  private String id;
  private String typeName;
  private List<Component> components = new LinkedList<Component>();
  private String building;
  private String room;
  private String latitude;
  private String longitude;
   
  public NodeObjects() {  }
  
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
  
  public List<Component> getComponents() {
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
