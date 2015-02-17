package mm.power.main;

import java.util.HashMap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import mm.power.implementation.AEHome;
import mm.power.modeling.PowerSupply;

public class Initialize implements ServletContextListener {

 // public PowerData  psdata;
  

  /**
   * !-- Initialize everything for the PowerService here --!
   */
  public void contextInitialized(ServletContextEvent arg0) {

    String id = "TESTAEHOME#1";
    String type = "AE HOME";
    String host = "";

    PowerSupply ps1 = new AEHome(id, type, host);
    
    HashMap<String, PowerSupply> map = new HashMap<String, PowerSupply>();
    map.put("TESTAEHOME#1", ps1);
    
    //psdata = new PowerData(map);
    PowerData.addPs(ps1);

   

   // PowerData.addPs(ps1);

  }

  public void contextDestroyed(ServletContextEvent arg0) {

  }// end constextDestroyed method


}