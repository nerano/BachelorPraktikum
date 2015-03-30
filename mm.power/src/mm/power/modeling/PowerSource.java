package mm.power.modeling;

public class PowerSource {

  private String id;
  private boolean status;
  private String failure;

  public PowerSource(String id) {
    this.id = id;
  }

  /**
   * Creates a new PowerSource with the given ID and the given state. The failure field
   * is set to "none".
   * @param id  ID of the PowerSource
   * @param status  state of the PowerSource
   */
  public PowerSource(String id, boolean status) {
    this.id = id;
    this.status = status;
    this.failure = "none";
  }

  /**
   * Creates a new PowerSource with the given ID and state, sets the failure field to the
   * given String. 
   * @param id  ID of the PowerSource
   * @param status  state of the PowerSource
   * @param failure  description of the failure
   */
  public PowerSource(String id, boolean status, String failure) {
    this.id = id;
    this.status = status;
    this.failure = failure;
  }

  public String getFailure() {
    return failure;
  }

  public void setFailure(String failure) {
    this.failure = failure;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public boolean isStatus() {
    return status;
  }

  public void setStatus(boolean status) {
    this.status = status;
  }

}
