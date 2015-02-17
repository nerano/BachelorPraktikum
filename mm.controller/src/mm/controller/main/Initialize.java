package mm.controller.main;

import java.util.HashMap;
import java.util.LinkedList;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import mm.controller.modeling.Component;
import mm.controller.modeling.Experiment;
import mm.controller.modeling.NodeObjects;
import mm.controller.modeling.VLan;



public class Initialize implements ServletContextListener
    {
           
    
    public ControllerData controllerData;
    
    public static LinkedList<NodeObjects> ALL_NODES;
    public static HashMap<String, Component> POWER_TO_COMPONENT;
    public static HashMap<Component, String> COMPONENT_TO_POWER;
   
    /**
    * !-- Initialize everything for the Controller here --!
    */
    public void contextInitialized(ServletContextEvent arg0) 
    {   
       controllerData = new ControllerData();
       
       POWER_TO_COMPONENT = new HashMap<String, Component>();
       ALL_NODES = new LinkedList<NodeObjects>();
       //TODO add all parsed Nodes to allNodes

       addExpExample();
       
    }

    
    public void contextDestroyed(ServletContextEvent arg0) 
    {
             
    }//end constextDestroyed method

    
    public static void addExpExample(){
        
        exp1();
        
        
    }
    
    public static void exp1(){
        
       String porta1 = "NätComponentA.1";
 	   String porta2 = "NetComponentA.2";
 	   
 	   String portf7 = "NetzKomponenteF.7";
 	   String portf8 = "NetzKomponenteF.8";
    	
       String powerSource1 = "TESTAEHOME#1;1";
       String powerSource2 = "TESTAEHOME#1;2";
       String powerSource3 = "TESTAEHOME#1;3";
 	   
    	Component c1 = new Component("WARP");
        Component c2 = new Component("APU");
        
        Component c3 = new Component("Komponente X");
        Component c4 = new Component("Komponente Z");
        
       /**  c1.setvLanId(0);
        c2.setvLanId(0);
        
        c3.setvLanId(0);
        c4.setvLanId(0); **/
        
        c1.setPowerSource(powerSource1);
        c2.setPowerSource(powerSource2);
        c3.setPowerSource(powerSource3);
        c4.setPowerSource(powerSource1);
        
        c1.setPort(porta1);
        c2.setPort(porta2);
        c3.setPort(portf7);
        c4.setPort(portf8);
        
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
        
        Experiment exp = new Experiment("EXPERIMENT123");
        exp.addNode(node1);
        exp.addNode(node2);
        
        VLan vlan1 = new VLan(125);
        VLan vlan2 = new VLan(124);
        
        exp.addVLan(vlan1);
        exp.addVLan(vlan2);
        
        
        ControllerData.addExp(exp);

       ControllerData.addPort(porta1, c1);
       ControllerData.addPort(porta2, c2);
       ControllerData.addPort(portf7, c3);
       ControllerData.addPort(portf8, c4);
    
 	   ALL_NODES.add(node1);
 	   ALL_NODES.add(node2);
    
    }
    

    
    }