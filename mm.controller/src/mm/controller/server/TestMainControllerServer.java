package mm.controller.server;


public class TestMainControllerServer {

  public static void main(String[] args) {
    //System.out.println(ControllerServer.getInstances("ganeti"));
    //System.out.println(ControllerServer.getTemplate());
    ControllerServer.createInstance("ganeti", "test.seemoo.tu-darmstadt.de", "Instanz1", 5012, 0, "10.10.11.3");
    //ControllerServer.createInstance(json.toString());
    //ControllerServer.startInstance("ganeti", "testvm.seemoo.tu-darmstadt.de","");
    //ControllerServer.stopInstance("ganeti", "testvm.seemoo.tu-darmstadt.de","");
    //ControllerServer.renameInstance("ganeti", "testController2.seemoo.tu-darmstadt.de", newName);
    //ControllerServer.deleteInstance("ganeti", "benvm2.seemoo.tu-darmstadt.de");
    //ControllerServer.rebootInstance("ganeti", "testController2.seemoo.tu-darmstadt.de", "");
    //ControllerServer.deleteInstance("ganeti", "unittestcontroller.seemoo.tu-darmstadt.de"); 
  }
}
