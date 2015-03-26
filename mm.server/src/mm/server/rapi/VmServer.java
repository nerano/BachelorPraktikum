package mm.server.rapi;

import javax.ws.rs.core.Response;

public interface VmServer {

  public String getInstances();
  
  public String getInstanceInfo(String instance);
  
  public String getInstanceInfoParam(String instance, String param);
  
  public Response create(String param);
  
  public Response delete(String instance);
  
  public Response startup(String instance, String type);
  
  public Response shutdown(String instance, String type);
  
  public Response rename(String instance, String newName);
  
  public Response reboot(String instance, String type);
}
