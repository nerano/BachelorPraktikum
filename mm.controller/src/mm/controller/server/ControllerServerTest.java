package mm.controller.server;

import static org.junit.Assert.*;

import org.junit.Test;

public class ControllerServerTest {

  ControllerServer conser = new ControllerServer();
  
  @Test
  public void testGetCreateStartStopDelete() {
    String instance = "controllertest.seemoo.tu-darmstadt.de";
    String createInstance = "{\"name\":\"" + instance + "\",\"template\":\"Instanz2\",\"bridge\":\"br0\",\"size\":5012,\"ip\":\"10.10.11.4\"}";
    try {
      assertEquals("Die Instanz " + instance + " ist nicht auf dem Server",
          false, conser.getInstances().contains(instance));
      conser.createInstance(createInstance);
      
      Thread.sleep(10000);
      
      assertEquals("Die Instanz " + instance + " ist auf dem Server",
          true, conser.getInstances().contains(instance));
      
      assertEquals("Die Instanz " + instance + " wurde nicht gestartet",
          false, conser.getInstanceInfoParam(instance, "status")
          .contains("running"));
      
      conser.startInstance(instance, "");
      
      Thread.sleep(10000);
      
      assertEquals("Die Instanz " + instance + " wurde gestartet",
         true, conser.getInstanceInfoParam(instance, "status")
         .contains("running"));
      
      conser.stopInstance(instance, "");
      
      Thread.sleep(10000);
      
      assertEquals("Die Instanz " + instance + " wurde nicht gestartet",
        false, conser.getInstanceInfoParam(instance, "status")
        .contains("running"));
      
      conser.deleteInstance(instance);
      
      Thread.sleep(10000);
      
      assertEquals("Die Instanz " + instance + " ist auf dem Server",
          false, conser.getInstances().contains(instance));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}