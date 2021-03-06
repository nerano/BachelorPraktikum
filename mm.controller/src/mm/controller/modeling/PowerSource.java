package mm.controller.modeling;

public class PowerSource {


	private String id;
	private boolean status;
	private String failure;
	
	public String getFailure() {
		return failure;
	}

	public void setFailure(String failure) {
        this.failure = failure;
    }

	public PowerSource(String id){
		this.id = id;
	}
	
	public PowerSource(String id, boolean status){
		this.id = id;
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
	
	

}
