package mm.controller.modeling;

import java.util.LinkedList;

public class Experiment {

  private int id;
  private LinkedList<NodeObjects> nodes;
    
    
  public Experiment(int id){
        
    this.id = id;
    this.nodes = new LinkedList<NodeObjects>();
        
  }
    
  public Experiment(int id, LinkedList<NodeObjects> nodes){
        
    this.id = id;
    this.nodes = nodes;
        
  }
    
  public Experiment(int id, NodeObjects node){
        
    this.id = id;
    this.nodes = new LinkedList<NodeObjects>();
    nodes.add(node);
        
  }
    
  public int getId(){
      return this.id;
  }
  
  public void addNode(NodeObjects node){
      nodes.add(node);
  }
      

}
