package mm.power.main;

import java.util.HashMap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import mm.power.modeling.PowerSupply;
import mm.power.parser.XmlParser;

public class Initialize implements ServletContextListener {


  /**
   * !-- Initialize everything for the PowerService here. --!
   */
  public void contextInitialized(ServletContextEvent contextEvent) {

    System.out.println("Initializing PowerService");
    XmlParser parser = new XmlParser();
   
    String path = contextEvent.getServletContext().getRealPath("/PowerSupply.xml");
    
    parser.parseXml(path);
    
    HashMap<String, PowerSupply> map = parser.getPowerSupply();
    new PowerData(map);
    System.out.println("Initializing PowerService finished");
    
  }

  public void contextDestroyed(ServletContextEvent arg0) {

  }// end constextDestroyed method
}