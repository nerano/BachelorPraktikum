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

  private int __version__;
  private String id;
  private String disk_template;
  private String file_driver;
  private String file_storage_dir;
  private String hypervisor;
  private String iallocator;
  private String instance_name;
  private String mode;
  private String os_type;
  private String pnode;
  private String pnode_uuid;
  private String source_instance_name;
  private String source_x509_ca;
  private String src_node;
  private String src_node_uuid;
  private String src_path;
  //private boolean force_variant;
  //private boolean identify_defaults;
  //private boolean ignore_ipolicy;
  private boolean ip_check = true;
  private boolean name_check = true;
  //private boolean no_install;
  //private boolean opportinistic_locking;
  private boolean conflicts_check = true;
  private boolean start = true;
  //private boolean wait_for_sync = true;
  private int source_shutdown_timeout = 120;
  private HashMap<String, String> osparams;
  private HashMap<String, String> hvparams;
  private HashMap<String, String> beparams;
  transient HashMap<String, String> nicLink;
  transient HashMap<String, String> nicIp;
  transient HashMap<String, String> nicMac;
  transient HashMap<String, String> nicName;
  transient HashMap<String, String> nicMode;
  transient HashMap<String, String> nicNetwork;
  transient HashMap<String, Object> disksMode;
  transient HashMap<String, Object> disksSize;
  private List<HashMap<String, String>> nics;
  private List<HashMap<String, Object>> disks;
  private List<String> tags;

  /**
   * Initialize the Lists and JSONObject.
   */
  public Template() {

  }

  /**
   * Sets boolean values to its attributes.
   * 
   * @param key
   *          name of the key.
   * @param value
   *          true or false.
   */
  public void setBoolean(String key, boolean value) {
    switch (key) {
      /*case "force_variant": force_variant = value;
        break;
      case "identify_defaults": identify_defaults = value;
        break;
      case "ignore_ipolicy": ignore_ipolicy = value;
        break;
      case "no_install": no_install = value;
        break;
      case "opportinistic_locking": opportinistic_locking = value;
        break;
      case "wait_for_sync": wait_for_sync = value;
        break;*/
      case "ip_check": ip_check = value;
        break;
      case "name_check": name_check = value;
        break;
      case "conflicts_check": conflicts_check = value;
        break;
      case "start": start = value;
        break;
      default:
        break;
    }
  }

  public void setId(String id) {
    this.id = id;
  }
  
  public List<HashMap<String, String>> getNics() {
    return nics;
  }

  public List<HashMap<String, Object>> getDisks() {
    return disks;
  }

  public HashMap<String, String> getNicLink() {
    return nicLink;
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

  public HashMap<String, String> getNicIp() {
    return nicIp;
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

  public HashMap<String, String> getNicMac() {
    return nicMac;
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

  public HashMap<String, String> getNicName() {
    return nicName;
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

  public HashMap<String, String> getNicMode() {
    return nicMode;
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

  public HashMap<String, String> getNicNetwork() {
    return nicNetwork;
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

  public HashMap<String, Object> getDisksMode() {
    return disksMode;
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

  public HashMap<String, Object> getDisksSize() {
    return disksSize;
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

  public int get__version__() {
    return __version__;
  }

  public String getDisk_template() {
    return disk_template;
  }

  public String getFile_driver() {
    return file_driver;
  }

  public String getFile_storage_dir() {
    return file_storage_dir;
  }

  public String getHypervisor() {
    return hypervisor;
  }

  public String getIallocator() {
    return iallocator;
  }

  public String getInstance_name() {
    return instance_name;
  }

  public String getMode() {
    return mode;
  }

  public String getOs_type() {
    return os_type;
  }

  public String getPnode() {
    return pnode;
  }

  public String getPnode_uuid() {
    return pnode_uuid;
  }

  public String getSource_instance_name() {
    return source_instance_name;
  }

  public String getSource_x509_ca() {
    return source_x509_ca;
  }

  public String getSrc_node() {
    return src_node;
  }

  public String getSrc_node_uuid() {
    return src_node_uuid;
  }

  public String getSrc_path() {
    return src_path;
  }
/*
  public boolean isForce_variant() {
    return force_variant;
  }

  public boolean isIdentify_defaults() {
    return identify_defaults;
  }

  public boolean isIgnore_ipolicy() {
    return ignore_ipolicy;
  }

  public boolean isNo_install() {
    return no_install;
  }

  public boolean isOpportinistic_locking() {
    return opportinistic_locking;
  }

  public boolean isWait_for_sync() {
    return wait_for_sync;
  }
*/
  public boolean isIp_check() {
    return ip_check;
  }

  public boolean isName_check() {
    return name_check;
  }

  public boolean isConflicts_check() {
    return conflicts_check;
  }

  public boolean isStart() {
    return start;
  }

  public int getSource_shutdown_timeout() {
    return source_shutdown_timeout;
  }

  public HashMap<String, String> getOsparams() {
    return osparams;
  }

  public HashMap<String, String> getHvparams() {
    return hvparams;
  }

  public HashMap<String, String> getBeparams() {
    return beparams;
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

  public List<String> getTags() {
    return tags;
  }

  /**
   * Adds a key with its value to the osparams HashMap.
   * 
   * @param key
   *          name of the key.
   * @param value
   *          to the key.
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
   *          to the key.
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
   *          to the key.
   */
  public void setBeparams(String key, String value) {
    if (this.beparams == null) {
      this.beparams = new HashMap<String, String>();
    }
    this.beparams.put(key, value);
  }

  public void set__version__(int __version__) {
    this.__version__ = __version__;
  }

  public void setDisk_template(String disk_template) {
    this.disk_template = disk_template;
  }

  public void setFile_driver(String file_driver) {
    this.file_driver = file_driver;
  }

  public void setFile_storage_dir(String file_storage_dir) {
    this.file_storage_dir = file_storage_dir;
  }

  public void setHypervisor(String hypervisor) {
    this.hypervisor = hypervisor;
  }

  public void setIallocator(String iallocator) {
    this.iallocator = iallocator;
  }

  public void setInstance_name(String instance_name) {
    this.instance_name = instance_name;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public void setOs_type(String os_type) {
    this.os_type = os_type;
  }

  public void setPnode(String pnode) {
    this.pnode = pnode;
  }

  public void setPnode_uuid(String pnode_uuid) {
    this.pnode_uuid = pnode_uuid;
  }

  public void setSource_instance_name(String source_instance_name) {
    this.source_instance_name = source_instance_name;
  }

  public void setSource_x509_ca(String source_x509_ca) {
    this.source_x509_ca = source_x509_ca;
  }

  public void setSrc_node(String src_node) {
    this.src_node = src_node;
  }

  public void setSrc_node_uuid(String src_node_uuid) {
    this.src_node_uuid = src_node_uuid;
  }

  public void setSrc_path(String src_path) {
    this.src_path = src_path;
  }
/*
  public void setForce_variant(boolean force_variant) {
    this.force_variant = force_variant;
  }

  public void setIdentify_defaults(boolean identify_defaults) {
    this.identify_defaults = identify_defaults;
  }

  public void setIgnore_ipolicy(boolean ignore_ipolicy) {
    this.ignore_ipolicy = ignore_ipolicy;
  }

  public void setNo_install(boolean no_install) {
    this.no_install = no_install;
  }

  public void setOpportinistic_locking(boolean opportinistic_locking) {
    this.opportinistic_locking = opportinistic_locking;
  }

  public void setWait_for_sync(boolean wait_for_sync) {
    this.wait_for_sync = wait_for_sync;
  }
*/
  public void setIp_check(boolean ip_check) {
    this.ip_check = ip_check;
  }

  public void setName_check(boolean name_check) {
    this.name_check = name_check;
  }

  public void setConflicts_check(boolean conflicts_check) {
    this.conflicts_check = conflicts_check;
  }

  public void setStart(boolean start) {
    this.start = start;
  }

  public void setSource_shutdown_timeout(int source_shutdown_timeout) {
    this.source_shutdown_timeout = source_shutdown_timeout;
  }
}
