package mm.controller.main;

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

   /**
    * !-- Initialize everything for the Controller here --!
    */
    public void contextInitialized(ServletContextEvent arg0) 
    {
       counter = 3;     
       expdata = new ExpData();
       
    
       addExpExample();
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
        
        Component c1 = new Component("WARP");
        Component c2 = new Component("APU");
        
        c1.setStatus(true);
        c2.setStatus(true);
        
        c1.setvLanId(123);
        c2.setvLanId(123);
        
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
        node2.setRoom("Raum Links");
        
        Experiment exp = new Experiment(123);
        exp.addNode(node1);
        exp.addNode(node2);
        
        ExpData.addExp(exp);
    }
    
   public static void exp2(){
        
        Component c1 = new Component("WARP");
        Component c2 = new Component("APU");
        
        c1.setStatus(true);
        c2.setStatus(true);
        
        c1.setvLanId(123);
        c2.setvLanId(123);
        
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