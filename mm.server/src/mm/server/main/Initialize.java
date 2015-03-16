package mm.server.main;

import mm.server.instance.Template;
import mm.server.parser.XmlParser;

import java.util.HashMap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class Initialize implements ServletContextListener {


  /**
   * !-- Initialize everything for the ServerService here --!.
   */
  public void contextInitialized(ServletContextEvent contextEvent) {
    String path = contextEvent.getServletContext().getRealPath("/VM.xml");
    XmlParser parser = new XmlParser(path);
    
    HashMap<String, Template> map = parser.parse();
    new ServerData(map);
  }

  public void contextDestroyed(ServletContextEvent arg0) {

  }// end constextDestroyed method
}