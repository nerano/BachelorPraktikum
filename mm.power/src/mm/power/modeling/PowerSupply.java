package mm.power.modeling;

import javax.ws.rs.core.Response;

/**
 * This Interface defines a set of methods which must be implemented if the
 * PowerService should use a specific implementation of a PowerSupply.
 * 
 * <p>
 * After implementing the sets of methods you should add your constructor to the
 * PowerSupplyFac, located in the same package. This factory is used for
 * constructing all PowerSupplys used by the PowerService.
 * </p>
 * 
 * <p>
 * After adding your constructor to the PowerSupplyFac you can use the model in
 * the powerSupply.xml and create the new implemented type of the PowerSupply.
 * If you are in need of additional parameters in the powerSupply.xml you can
 * add them to the XmlParser in the parser package.
 * </p>
 * 
 * <p>
 * The specific model of the outlet, switch or any other power supply has to be
 * abstracted. The methods from this interface have to be implemented like the
 * documentation states. The PowerSupply needs to have an global ID and an
 * internal representation of the ports/outlet sockets/ .. is abstracted to the
 * generic PowerSupply;socket view.
 * </p>
 *
 */
public interface PowerSupply {

  /**
   * Turns the given socket on the PowerSupply on.
   *
   * <p>
   * This method is idempotent. Calling it multiple times leads to the same
   * result as calling it once. If the socket on the PowerSupply is turned off,
   * before calling this method, it is on, after calling this method.
   * </p>
   * 
   * <p>
   * If the socket on the PowerSupply is currently turned on calling this method
   * does not change the state of the PowerSupply at all, but only a HTTP
   * message with the response code 200 (OK) is returned.
   * </p>
   * 
   * 
   * <p>
   * If the method is successful a HTTP response with status code 200 (OK) is
   * returned, otherwise, in case of an error, the HTTP status code 500
   * (Internal Server Error) is returned with an specified error description in
   * the message body.
   * </p>
   * 
   * @param socket
   *          the number the socket to turn on. The range of the valid input
   *          parameters ranges from 1 to the number of sockets on the
   *          PowerSupply
   * @return an outbound response object
   */
  public Response turnOn(int socket);

  /**
   * Turns the given socket on the PowerSupply off.
   *
   * <p>
   * This method is idempotent. Calling it multiple times leads to the same
   * result as calling it once. If the socket on the PowerSupply is turned on,
   * before calling this method, it is off, after calling this method.
   * </p>
   * 
   * <p>
   * If the socket on the PowerSupply is currently turned off calling this
   * method does not change the state of the PowerSupply at all, but only a HTTP
   * message with the response code 200 (OK) is returned.
   * </p>
   * 
   * <p>
   * If the method is successful a HTTP response with status code 200 (OK) is
   * returned, otherwise, in case of an error, the HTTP status code 500
   * (Internal Server Error) is returned with an specified error description in
   * the message body.
   * </p>
   * 
   * @param socket
   *          the number the socket to turn off. The range of the valid input
   *          parameters ranges from 1 to the number of sockets on the
   *          PowerSupply
   * @return an outbound response object
   */
  public Response turnOff(int socket);

  /**
   * Returns a status String.
   * 
   * <p>
   * The String has the length equal to the number of sockets/ports on the
   * PowerSupply. The ith character in the String is the status of the ith
   * socket/port on the PowerSupply, where a 0 means the PowerSupply is
   * currently turned off and a 1 shows that this particular socket is currently
   * on.
   * </p>
   * 
   * <p>
   * This method should never change any state on any PowerSupply.
   * </p>
   * 
   * <p>
   * The String is located in the responses message body, if the HTTP status
   * code is 200. If an error occurred the response code should be 500 and a
   * specified error report should be located in the message body.
   * </p>
   * 
   * @return an outbound response message
   */
  public Response status();

  /**
   * Returns a String with the status of the socket.
   * 
   * <p>
   * The String has the length of 1. A 0 shows that the socket/port is currently
   * turned off and a 1 indicates an active socket/port.
   * </p>
   *
   * <p>
   * This method should never change any state on any PowerSupply.
   * <p>
   *
   * The String is located in the responses message body, if the HTTP status
   * code is 200. If an error occurred the response code should be 500 and a
   * specified error report should be located in the message body.
   * </p>
   * 
   * @param socket
   *          The socket to retrieve status information for. The valid range of
   *          this value goes from 1 to number of sockets/ports on the
   *          PowerSupply.
   * @return an outbound response message
   */
  public Response status(int socket);

  /**
   * A Power Supply must have an unique, global ID.
   * 
   * @return the unique ID
   */
  public String getId();

  /**
   * For displaying error messages a toString method is highly recommended.
   * 
   * @return a String representation from a PowerSupply with all relevant
   *         information
   */
  public String toString();

}