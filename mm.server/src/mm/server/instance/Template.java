package mm.server.instance;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

/**
 * Represents an template of an instance with its attributes.
 * @author Benedikt Bakker
 *
 */
public class Template {

  private JSONObject json;
  private JSONObject nics;
  private JSONObject disks;
  List<JSONObject> nic;
  List<JSONObject> disk;
  
  /**
   * Initialize the Lists and JSONObject.
   */
  public Template() {
    json = new JSONObject();
    nic = new ArrayList<JSONObject>();
    disk = new ArrayList<JSONObject>();
  }
  
  /**
   * Adds a given attribute to the JSONObject.
   * @param key of the given attribute.
   * @param value of the given attribute.
   */
  public void setString(String key, String value) {
    if (key.startsWith("nic_")) {
      nics = new JSONObject();
    }
    if (key.startsWith("disks")) {
      disks = new JSONObject();
    }
    try {
      switch (key) {
        case 
          "nic_link": nics.put("link", value);
          nic.add(nics);
          break;
        case
          "nic_mode": nics.put("mode", value);
          nic.add(nics);
          break;
        case
          "nic_ip": nics.put("ip", value);
          nic.add(nics);
          break;
        case
          "disks_size": disks.put("size", Integer.parseInt(value));
          disk.add(disks);
          break;
        case
          "__version__": json.put(key, Integer.parseInt(value));
          break;
        default:
          json.put(key, value);
          break;
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Adds the List of nics and disks attributes to the JSONObject.
   */
  public void setList() {
    try {
      json.put("nics", nic);
      json.put("disks", disk);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Sets boolean values to the JSONObject.
   * @param key name of the key.
   * @param value true or false.
   */
  public void setBoolean(String key, boolean value) {
    try {
      json.put(key, value);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
  
  public JSONObject getJson() {
    return json;
  }

  public List<JSONObject> getNic() {
    return nic;
  }

  public List<JSONObject> getDisk() {
    return disk;
  }
  
  public String toString() {
    return json.toString();
  }
}