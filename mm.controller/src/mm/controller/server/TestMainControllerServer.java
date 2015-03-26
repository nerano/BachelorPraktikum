package mm.controller.server;


public class TestMainControllerServer {

  public static void main(String[] args) {
     ControllerServer conser = new ControllerServer();
    String json = "{\"template\":\"Instanz1\","
        + "\"name\":\"test.seemoo.tu-darmstadt.de\","
        + "\"bridge\":\"br0\","
        + "\"size\":5120,"
        + "\"ip\":\"10.10.11.3\"}";
    //System.out.println(conser.getInstances());
    //System.out.println(conser.getTemplate());
    conser.createInstance("ganeti",json);
    //conser.createInstance(json.toString());
    //conser.startInstance("testvm.seemoo.tu-darmstadt.de","");
    //conser.stopInstance("testvm.seemoo.tu-darmstadt.de","");
    //conser.renameInstance("testController2.seemoo.tu-darmstadt.de", newName);
    //conser.deleteInstance("benvm2.seemoo.tu-darmstadt.de");
    //conser.rebootInstance("testController2.seemoo.tu-darmstadt.de", "");
    //conser.deleteInstance("unittestcontroller.seemoo.tu-darmstadt.de"); 
  }
}
