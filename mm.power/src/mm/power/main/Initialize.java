package mm.power.main;

import java.util.HashMap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import mm.power.modeling.PowerSupply;
import mm.power.parser.XmlParser;

public class Initialize implements ServletContextListener {


  /**
   * !-- Initialize everything for the PowerService here --!
   */
  public void contextInitialized(ServletContextEvent contextEvent) {

    XmlParser parser = new XmlParser();
   
    String path = contextEvent.getServletContext().getRealPath("/PowerSupply.xml");
    System.out.println(path);
    
    parser.parseXml(path);
    
    HashMap<String, PowerSupply> map = parser.getPowerSupply();
    new PowerData(map);
    
    System.out.println(PowerData.getById("AeHome1").toString());
    System.out.println(PowerData.getById("AeHome2").toString());
    System.out.println(PowerData.getById("AeHome3").toString());
    
  }

  public void contextDestroyed(ServletContextEvent arg0) {

  }// end constextDestroyed method


}