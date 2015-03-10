package mm.power.servlet;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mm.power.main.PowerData;
import mm.power.modeling.PowerSupply;

@Path("/put")
public class PowerPut {

  /**
   * Turns PowerSources on from a given concatenated input String.
   *
   * <p>
   * Address of this method is
   * <code> baseuri:port/mm.power/rest/put/turnOn/</code> <br>
   * String consists of PowerSource;Socket couples, which are divided by a ";".
   * The string has to be in the message entity and must end with an "end". If
   * everything was successfully turned on the response holds the status 200.
   * </p>
   * <p>
   * Possible HTTP States:
   * <li>200: Everything went right, all PowerSources could be turned on.</li>
   * <li>404: One or multiple PowerSources could not be found. Which one are</li>
   * specified in the returned Response body. Occurs only if there was no other
   * error
   * </p>
   * <p>
   * <li>500: Something in the process of turning on did not work as intended.
   * E.g. UnknownHost or Timeout on the HTTP connection. Cause and specific
   * error can be found in the Response body or the ServerLog.</li>
   * </p>
   * 
   * @param incoming
   *          String, expected format "[PowerSource;Socket;]end" multiple
   *          PowerSource;Socket; couples possible
   * @return Response Object with a corresponding status code and maybe some
   *         error notifications
   */
  @PUT
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/turnOn")
  public Response turnOn(String incoming) {
    try {
      System.out.println("POWER SERVLET TURN ON");
      String[] parts = incoming.split(";");
      int size; 
      int socket;
      int responseStatus = -1;
      size = (parts.length - 1) / 2;
      String[] ids = new String[size];
      String id;
      StringBuffer buffer = new StringBuffer();
      PowerSupply ps;

      int[] sockets = new int[size];

      if (!(parts[parts.length - 1].equals("end")) || parts.length < 3 || (parts.length - 1) % 2 != 0) {
        String responseString = "Transfer not complete or wrong format in PowerPut turnOn. Message : "
            + incoming;
        return Response.status(500).entity(responseString).build();
      }

      for (int k = 0; k < parts.length - 1; k = k + 2) {

        ids[k / 2] = parts[k];
        sockets[k / 2] = Integer.parseInt(parts[k + 1]);

      }

      for (int i = 0; i < size; i++) {

        id = ids[i];
        socket = sockets[i];

        if (!(PowerData.exists(id))) {
          responseStatus = 404;
          buffer.append("PowerSource '" + id + "' not found\n");
        } else {

          ps = PowerData.getById(id);

          Response response = ps.turnOn(socket);

          if (response.getStatus() != 200) {

            String string = (String) response.getEntity();
            buffer.append(string).append("\n");
            responseStatus = 500;

          } else {
              responseStatus = 200;
          }
        }
      }

      return Response.status(responseStatus).entity(buffer.toString()).build();
    } catch (Exception e) {

      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      String responseString = "Error occurred in PowerPut /turnOn. "
          + "Nothing was turned on, because of error: \n" + sw.toString() + "\n End of Error.";
      return Response.status(500).entity(responseString).build();
    }

  }

  /**
   * Turns Sockets off from a given concatenated input String.
   *
   * <p>
   * Address of this method: baseuri:port/mm.power/rest/put/turnOff/ String
   * consists of PowerSource;Socket couples, which are divided by a ";". The
   * string has to be in the message entity and must end with an "end". If
   * everything was successfully turned off the response holds the status 200.
   *
   * <p>
   * Possible HTTP States:
   * <li>200: Everything went right, all PowerSources could be turned off.
   * <li>404: One or multiple PowerSources could not be found. Which one are
   * specified in the returned Response body. Occurs only if there was no other
   * error
   *
   * <p>
   * <li>500: Something in the process of turning off did not work as intended.
   * E.g. UnknownHost or Timeout on the HTTP connection. Cause and specific
   * error can be found in the Response body or the ServerLog.
   * 
   * @param incoming
   *          String, expected format "[PowerSource;Socket;]end" multiple
   *          PowerSource;Socket; couples possible
   * @return Response Object with a corresponding status code and maybe some
   *         error notifications
   */
  @PUT
  @Consumes(MediaType.TEXT_PLAIN)
  @Path("/turnOff")
  public Response turnOff(String incoming) {
    System.out.println(incoming);
    try {
      String[] parts = incoming.split(";");
      String id;
      StringBuffer buffer = new StringBuffer();
      int size;
      int socket;
      int responseStatus = -1;
      
      PowerSupply ps;
      
      size = (parts.length - 1) / 2;
      String[] ids = new String[size];
      int[] sockets = new int[size];

      if (!(parts[parts.length - 1].equals("end")) || parts.length < 3
          || (parts.length - 1) % 2 != 0) {
        String responseString = "Transfer not complete or wrong format in PowerPut turnOn. Message : "
            + incoming;
        return Response.status(500).entity(responseString).build();
      }

      for (int k = 0; k < parts.length - 1; k = k + 2) {

        ids[k / 2] = parts[k];
        sockets[k / 2] = Integer.parseInt(parts[k + 1]);

      }

      for (int i = 0; i < size; i++) {

        id = ids[i];
        socket = sockets[i];

        if (!(PowerData.exists(id))) {
          responseStatus = 404;
          buffer.append("PowerSource '" + id + "' not found\n");
        } else {

          ps = PowerData.getById(id);

          Response response = ps.turnOff(socket);

          System.out.println("POWER PUT STATUS : " + response.getStatus());
          
          
          if (response.getStatus() != 200) {
            String string = (String) response.getEntity();
            
            buffer.append(string).append("\n");
            responseStatus = 500;

          } else {
            responseStatus = 200;
          }
        }
      }
      System.out.println("DIESE LETZTE RESPONSE");
      System.out.println("responsestatus : " + responseStatus);
      System.out.println("buffer: " + buffer.toString());
      buffer.append("ALLES LIEF GUT \n");
      return Response.status(responseStatus).entity(buffer.toString()).build();
  
    } catch (Exception e) {

      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      return Response.status(500).entity(sw.toString()).build();
    }

  }

}
