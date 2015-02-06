package mm.controller.main;

import java.util.HashMap;
import java.util.LinkedList;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import mm.controller.modeling.Component;
import mm.controller.modeling.Experiment;
import mm.controller.modeling.NodeObjects;
import mm.controller.main.*;



public class ServletContextClass implements ServletContextListener
    {
           
    
    public ExpData expdata;
    public static int counter;
    public static HashMap<String, Component> portMapping;
    public static LinkedList<NodeObjects> allNodes;

   /**
    * !-- Initialize everything for the Controller here --!
    */
    public void contextInitialized(ServletContextEvent arg0) 
    {
       counter = 3;     
       expdata = new ExpData();
       portMapping = new HashMap<String, Component>();
       allNodes = new LinkedList<NodeObjects>();
       //TODO add all parsed Nodes to allNodes
       
       
    
       addExpExample();
       
    }

    
    public static Component getComponent(String port){
    	return portMapping.get(port);
    	
    }

    public void contextDestroyed(ServletContextEvent arg0) 
    {
             
    }//end constextDestroyed method

    public static int getCounter(){
        return counter;
    }
  
    public static void counterp(){
        counter++;
    }
    
  
    
    
    
    public static void addExpExample(){
        
        exp1();
        
        
    }
    
    public static void exp1(){
        
       String porta1 = "NetComponentA.1";
 	   String porta2 = "NetComponentA.2";
 	   
 	   String portf7 = "NetzKomponenteF.7";
 	   String portf8 = "NetzKomponenteF.8";
    	
    	
    	Component c1 = new Component("WARP");
        Component c2 = new Component("APU");
        
        c1.setStatus(false);
        c2.setStatus(false);
        
        c1.setvLanId(0);
        c2.setvLanId(0);
        
        Component c3 = new Component("Komponente X");
        Component c4 = new Component("Komponente Z");
        
        c3.setvLanId(0);
        c4.setvLanId(0);
        
        c3.setStatus(false);
        c4.setStatus(false);
        
        NodeObjects node1 = new NodeObjects("Knoten A");
        NodeObjects node2 = new NodeObjects("Knoten B");
        
        
        node1.addComponent(c1);
        node1.addComponent(c2);
        
        node2.addComponent(c3);
        node2.addComponent(c4);
        
        node1.setBuilding("Haus 1");
        node2.setBuilding("Haus 2");
        
        node1.setRoom("Raum Rechts");
        node2.setRoom("Raum Links");
        
        Experiment exp = new Experiment(123);
        exp.addNode(node1);
        exp.addNode(node2);
        
        ExpData.addExp(exp);

       portMapping.put(porta1, c1);
 	   portMapping.put(porta2, c2);
 	   portMapping.put(portf7, c3);
 	   portMapping.put(portf8, c4);
    
    allNodes.add(node1);
    allNodes.add(node2);
    
    }
    
   public static void exp2(){
        
        Component c1 = new Component("WARP");
        Component c2 = new Component("APU");
        
        c1.setStatus(false);
        c2.setStatus(false);
        
       c1.setvLanId(124);
       c2.setvLanId(124);
        
        Component c3 = new Component("Komponente X");
        Component c4 = new Component("Komponente Z");
        
        NodeObjects node1 = new NodeObjects("Knoten A");
        NodeObjects node2 = new NodeObjects("Knoten B");
        
        
        node1.addComponent(c1);
        node1.addComponent(c2);
        
        node2.addComponent(c3);
        node2.addComponent(c4);
        
        node1.setBuilding("Haus 1");
        node2.setBuilding("Haus 2");
        
        node1.setRoom("Raum Rechts");
        node1.setRoom("Raum Links");
        
        Experiment exp = new Experiment(123);
        exp.addNode(node1);
        exp.addNode(node2);
        
        ExpData.addExp(exp);
    }
    
    }