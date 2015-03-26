package mm.server.instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents an template of an instance with its attributes.
 * 
 * @author Benedikt Bakker
 * 
 */
public class Template {

  private HashMap<String, Object> attribute;
  private transient HashMap<String, Boolean> boolmap;
  private transient HashMap<String, String> osparams;
  private transient HashMap<String, String> hvparams;
  private transient HashMap<String, String> beparams;
  private transient HashMap<String, String> nicLink;
  private transient HashMap<String, String> nicIp;
  private transient HashMap<String, String> nicMac;
  private transient HashMap<String, String> nicName;
  private transient HashMap<String, String> nicMode;
  private transient HashMap<String, String> nicNetwork;
  private transient HashMap<String, Object> disksMode;
  private transient HashMap<String, Object> disksSize;
  private List<HashMap<String, String>> nics;
  private List<HashMap<String, Object>> disks;
  private int version;
  private transient List<String> tags;

  /**
   * Initialize the attribute map.
   */
  public Template() {
    this.attribute = new HashMap<String, Object>();
  }

  public void setAttribute(String key, Object value) {
    this.attribute.put(key, value);
  }

  public void removeAttribute(String key) {
    this.attribute.remove(key);
  }

  /**
   * Adds a boolean attribute to the map.
   * 
   * @param key
   *          name of the key.
   * @param value
   *          of the key.
   */
  public void setBoolean(String key, boolean value) {
    if (boolmap == null) {
      boolmap = new HashMap<String, Boolean>();
    }
    this.boolmap.put(key, value);
  }

  /**
   * Adds all dictionaries and lists to the attribute map.
   */
  public void setLists() {
    if (nics != null) {
      this.attribute.put("nics", nics);
    }
    if (disks != null) {
      this.attribute.put("disks", disks);
    }
    if (osparams != null) {
      this.attribute.put("osparams", osparams);
    }
    if (hvparams != null) {
      this.attribute.put("hvparams", hvparams);
    }
    if (beparams != null) {
      this.attribute.put("beparams", beparams);
    }
    if (tags != null) {
      this.attribute.put("tags", tags);
    }
    if (boolmap != null) {
      this.attribute.putAll(boolmap);
    }
    this.attribute.put("__version__", version);
  }

  public void setVersion(int version) {
    this.version = version;
  }

  /**
   * Adds a link parameter to the nics list. Creates nics object if this was not
   * initialized.
   * 
   * @param link
   *          name of the link
   */
  public void setNicLink(String link) {
    this.nicLink = new HashMap<String, String>();
    this.nicLink.put("link", link);
    if (nics == null) {
      nics = new ArrayList<HashMap<String, String>>();
    }
    this.nics.add(nicLink);
  }

  /**
   * Adds a ip address to the nics list. Creates nics object if this was not
   * initialized.
   * 
   * @param ip
   *          address
   */
  public void setNicIp(String ip) {
    this.nicIp = new HashMap<String, String>();
    this.nicIp.put("ip", ip);
    if (nics == null) {
      nics = new ArrayList<HashMap<String, String>>();
    }
    this.nics.add(nicIp);
  }

  /**
   * Adds a mac address parameter to the nics list. Creates nics object if this
   * was not initialized.
   * 
   * @param mac
   *          address
   */
  public void setNicMac(String mac) {
    this.nicMac = new HashMap<String, String>();
    this.nicMac.put("mac", mac);
    if (nics == null) {
      nics = new ArrayList<HashMap<String, String>>();
    }
    this.nics.add(nicMac);
  }

  /**
   * Adds a name parameter to the nics list. Creates nics object if this was not
   * initialized.
   * 
   * @param name
   *          of the name
   */
  public void setNicName(String name) {
    this.nicName = new HashMap<String, String>();
    this.nicName.put("name", name);
    if (nics == null) {
      nics = new ArrayList<HashMap<String, String>>();
    }
    this.nics.add(nicName);
  }

  /**
   * Adds a mode parameter to the nics list. Creates nics object if this was not
   * initialized.
   * 
   * @param mode
   *          name of the mode
   */
  public void setNicMode(String mode) {
    this.nicMode = new HashMap<String, String>();
    this.nicMode.put("mode", mode);
    if (nics == null) {
      nics = new ArrayList<HashMap<String, String>>();
    }
    this.nics.add(nicMode);
  }

  /**
   * Adds a network parameter to the nics list. Creates nics object if this was
   * not initialized.
   * 
   * @param network
   *          name of the network
   */
  public void setNicNetwork(String network) {
    this.nicNetwork = new HashMap<String, String>();
    this.nicNetwork.put("network", network);
    if (nics == null) {
      nics = new ArrayList<HashMap<String, String>>();
    }
    this.nics.add(nicNetwork);
  }

  /**
   * Adds the mode to the disks list. Creates disks object if this was not
   * initialized.
   * 
   * @param mode
   *          of the disks
   */
  public void setDisksMode(String mode) {
    this.disksMode = new HashMap<String, Object>();
    this.disksMode.put("mode", mode);
    if (disks == null) {
      disks = new ArrayList<HashMap<String, Object>>();
    }
    this.disks.add(disksMode);
  }

  /**
   * Adds size to the disks list. Creates disks object if this was not
   * initialized.
   * 
   * @param size
   *          of the disks
   */
  public void setDisksSize(int size) {
    this.disksSize = new HashMap<String, Object>();
    this.disksSize.put("size", size);
    if (disks == null) {
      disks = new ArrayList<HashMap<String, Object>>();
    }
    this.disks.add(disksSize);
  }

  /**
   * Adds a parameter to the tags list.
   * 
   * @param tag
   *          name of the parameter.
   */
  public void setTags(String tag) {
    if (this.tags == null) {
      this.tags = new ArrayList<String>();
    }
    this.tags.add(tag);
  }

  /**
   * Adds a key with its value to the osparams HashMap.
   * 
   * @param key
   *          name of the key.
   * @param value
   *          of the key.
   */
  public void setOsparams(String key, String value) {
    if (this.osparams == null) {
      this.osparams = new HashMap<String, String>();
    }
    this.osparams.put(key, value);
  }

  /**
   * Adds a key with its value to the hvparams HashMap.
   * 
   * @param key
   *          name of the key.
   * @param value
   *          of the key.
   */
  public void setHvparams(String key, String value) {
    if (this.hvparams == null) {
      this.hvparams = new HashMap<String, String>();
    }
    this.hvparams.put(key, value);
  }

  /**
   * Adds a key with its value to the beparams HashMap.
   * 
   * @param key
   *          name of the key.
   * @param value
   *          of the key.
   */
  public void setBeparams(String key, String value) {
    if (this.beparams == null) {
      this.beparams = new HashMap<String, String>();
    }
    this.beparams.put(key, value);
  }

  public HashMap<String, Object> getAttribute() {
    return this.attribute;
  }
}