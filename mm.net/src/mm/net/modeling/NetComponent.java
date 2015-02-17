package mm.net.modeling;

public class NetComponent {

    private String Id;
  
    private static final int port = 8;

    private String ip;
    
    public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	String getId() {
        return Id;
    }
    public void setId(String id) {
        Id = id;
    }
    public static int getPort() {
        return port;
    }


    public void getPortByVlan(int id){
        
        
        return;
    }




}
