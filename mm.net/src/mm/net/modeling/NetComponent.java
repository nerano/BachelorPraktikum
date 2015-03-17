package mm.net.modeling;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.Response;

public interface NetComponent {

	public Response start();
	public Response stop();
	public void createLocalVlans();
	
	public Response getPVID(int port);
	public Response setPVID(int port, int pvid);
	
    public LinkedList<Integer> getTrunks();
    
    //public LinkedList<VLan> getUnusedLocalVlans();

    
	
	
    
    
    
	
	public Response addPort(int port, int vlanId);
	public Response addPort(List<Integer> port, int vlanId);
	
	public Response setPort(int port, int vlanId, String name);
    public Response setPort(List<Integer> port, int vlanId, String name);
	
	public Response removePort(int port, int vlanId);
    public Response removePort(List<Integer> port, int vlanId);
    
    public Response addTrunkPort(int port, int vlanId);
    public Response addTrunkPort(List<Integer> port, int vlanId);
	
	public Response setTrunkPort(int port, int vlanId, String name);
	public Response setTrunkPort(List<Integer> port, int vlanId, String name);
	
	
    
    //public Response setLocalPort(int port, int vlanId, String name);
    //public Response addLocalPort(int port, int vlanId);
    
    //public Response setLocalPort(List<Integer> port, int vlanId, String name);
    //public Response addLocalPort(List<Integer> port, int vlanId);

    public Response destroyVlan(int vlanId);

}
