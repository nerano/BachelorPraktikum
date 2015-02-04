package mm.controller.modeling;

import java.util.LinkedList;

public class Experiment implements Cloneable {

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
  
  public void setList(LinkedList<NodeObjects> list){
	  this.nodes = list;
  }
  public LinkedList<NodeObjects> getList(){
	  return nodes;
  }
       
  @Override
  public Object clone() throws CloneNotSupportedException{
	 Experiment cloned = (Experiment)super.clone();
	 cloned.setList( (LinkedList<NodeObjects>) cloned.getList().clone());
	 return cloned;
  }







}
