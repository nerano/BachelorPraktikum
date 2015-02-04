package mm.controller.power;

import java.util.LinkedList;
import java.util.List;

public class Node {


  private String id;
  private LinkedList<Component> components;
  
  
  
  public Node(String id) {
      
    this.id = id;
    this.components = new LinkedList<Component>();
      
      
  }
  
  public Node(String id, LinkedList<Component> components) {
      
    this.id = id;
    this.components = components;
      
  }

  
  public void addComponent(Component component) {
      
    components.add(component);
      
  }

  
  
  
  

}