package mm.server.rapi;

import org.json.JSONException;
import org.json.JSONObject;

public class TestMain {
  
  public static void main(String[] args) {
    /*String createJson = "{\"__version__\":1,"
        + "\"name_check\":false,"
        + "\"pnode\":\"pxhost01.seemoo.tu-darmstadt.de\","
        + "\"disk_template\":\"plain\","
        + "\"conflicts_check\":false,"
        + "\"ip_check\":false,"
        + "\"instance_name\":\"test123.seemoo.tu-darmstadt.de\","
        + "\"nics\":[{\"link\":\"br0\"},{\"mode\":\"bridged\"},{\"ip\":\"10.10.11.3\"}],"
        + "\"disks\":[{\"size\":5120}],"
        + "\"os_type\":\"debootstrap+wheezy\","
        + "\"mode\":\"create\","
        + "\"start\":false}";
    String reboot = "{\"type\":\"soft\","
        + "\"ignore_secondaries\":1}";*/
    Ganeti ga = new Ganeti();
    RestInterface ri = new RestInterface();
    JSONObject json = new JSONObject();
    try {
      json.put("name", "benvm.seemoo.tu-darmstadt.de");
      json.put("template", "Instanz1");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    //System.out.println(ga.getInstances());
    //System.out.println(ga.getInstanceInfo("benvm.seemoo.tu-darmstadt.de"));
    //System.out.println(ga.getInstanceInfoParam("benvm.seemoo.tu-darmstadt.de","always_failover"));
    //System.out.println(ga.getInstanceInfoParam("benvm.seemoo.tu-darmstadt.de","status"));
    ri.createInstance(json.toString());
    //ga.startup("benvm.seemoo.tu-darmstadt.de","");
    //ga.shutdown("testvm.seemoo.tu-darmstadt.de","");
    //ga.rename("test123.seemoo.tu-darmstadt.de", "benvm.seemoo.tu-darmstadt.de");
    //ga.delete("benvm.seemoo.tu-darmstadt.de");
    //ga.reboot("testvm.seemoo.tu-darmstadt.de", "");
  }
}