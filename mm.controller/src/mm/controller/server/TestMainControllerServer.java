package mm.controller.server;

import org.json.JSONObject;

public class TestMainControllerServer {

  public static void main(String[] args) {
    ControllerServer conser = new ControllerServer();
    JSONObject json = new JSONObject();
    json.put("name", "benvm2.seemoo.tu-darmstadt.de");
    json.put("template", "Instanz1");
    json.put("bridge", "br0");
    json.put("ip", "10.10.11.4");
    json.put("size", 5012);
    //System.out.println(conser.getInstances());
    //System.out.println(conser.getTemplate());
    conser.createInstance(json.toString());
    json.put("name", "benvm3.seemoo.tu-darmstadt.de");
    json.put("template", "Instanz1");
    json.put("bridge", "br1");
    json.put("ip", "10.10.11.3");
    json.put("size", 1024);
    //conser.createInstance(json.toString());
    //conser.startInstance("testvm.seemoo.tu-darmstadt.de","");
    //conser.stopInstance("testvm.seemoo.tu-darmstadt.de","");
    //conser.renameInstance("testController2.seemoo.tu-darmstadt.de", newName);
    //conser.deleteInstance("benvm2.seemoo.tu-darmstadt.de");
    //conser.rebootInstance("testController2.seemoo.tu-darmstadt.de", "");
    //conser.deleteInstance("unittestcontroller.seemoo.tu-darmstadt.de");
  }
}
