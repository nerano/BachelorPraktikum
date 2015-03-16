package mm.server.instance;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Instance {

  private JSONObject json;
  private JSONObject nics;
  private JSONObject disks;
  List<JSONObject> nic;
  List<JSONObject> disk;
  
  /**
   * Creates new Objects.
   */
  public Instance() {
    json = new JSONObject();
    nic = new ArrayList<JSONObject>();
    disk = new ArrayList<JSONObject>();
  }
  
  public void setJson(JSONObject json) {
    this.json = json;
  }
  
  public void setNic(List<JSONObject> nic) {
    this.nic.addAll(nic);
  }
  
  public void setDisk(List<JSONObject> disk) {
    this.disk.addAll(disk);
  }
  
  public String toString() {
    return json.toString();
  }
  
  /**
   * Sets the used nic attribute of this VM.
   * @param value the used bridge
   */
  public void setNics(String key, String value) {
    nics = new JSONObject();
    try {
      nics.put(key, value);
      nic.add(nics);
      json.put("nics", nic);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Sets the disk size of this VM.
   * @param size of the disk.
   */
  public void setDisksSize(int size) {
    disks = new JSONObject();
    try {
      disks.put("size", size);
      disk.add(disks);
      json.put("disks", disk);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Sets the instance name of this VM.
   * @param name of instance.
   */
  public void setName(String name) {
    try {
      json.put("instance_name", name);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}