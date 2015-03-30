package mm.power.servlet;

import java.util.LinkedList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mm.power.main.PowerData;
import mm.power.modeling.*;

@Path("/get")
public class PowerGet {

  private Gson gson = new GsonBuilder().setPrettyPrinting().create();

  /**
   * Returns information about the PowerSource with the given ID.
   * 
   * <p>
   * The information about the current state of the PowerSource is contained in
   * a list of PowerSources, each of them has an ID, a state and a failure
   * String with the error description or "none".
   * </p>
   *
   * <p>
   * It is possible to chain the PowerSources. They must be separated by a ";" and
   * the message must end with ";end". The size of the returned list as the same
   * as the count of PowerSources in the incoming String.
   * </p>
   * 
   * <p>
   * Possible HTTP status codes:
   * 
   * <li>200: Everything went as expected. State of each PowerSource could be
   * retrieved</li>
   * <li>404: A PowerSupply defined in a PowerSource could not be found.</li>
   * <li>500: Internal Server Error. Something went wrong on getting the state
   * of the PowerSource Error description in the error field of the PowerSource.
   * If at least one PowerSource caused an error the status 500 is set.</li>
   * 
   * @param incoming
   *          ID of the PowerSupply and the socket/port number e.g
   *          "PowerSupply;1;end"
   * @return response A response Object with the requested information
   */
  @GET
  @Path("{incoming}")
  @Produces({ MediaType.TEXT_PLAIN, "json/application" })
  public Response getStatus(@PathParam("incoming") String incoming) {

    LinkedList<PowerSource> returnList = new LinkedList<PowerSource>();
    PowerSupply ps;
    boolean status;
    String id;
    String error = "none";
    int socket;
    int responseStatus = 200;

    System.out.println("INCOMING in POWERGET : " + incoming);

    String[] parts = incoming.split(";");

    if (!(parts[parts.length - 1].equals("end"))) {
      String responseString = "Transfer not Complete or wrong format in PowerGet GetById. "
          + "Message : " + incoming;
      return Response.status(500).entity(responseString).build();
    }

    for (int i = 0; i < parts.length - 2; i = i + 2) {

      id = parts[i];
      socket = Integer.parseInt(parts[i + 1]);

      if (!(PowerData.exists(id))) {
        responseStatus = 404;
        error = id + " does not exist";
        returnList.add(new PowerSource(id, false, error));

      } else {
        ps = PowerData.getById(id);

        Response response = ps.status(socket);

        if (response.getStatus() == 200) {

          if (((String) response.getEntity()).equals("0")) {
            status = false;
          } else {
            status = true;
          }

          returnList.add(new PowerSource(id + ";" + socket, status));
        } else {
          responseStatus = 500;
          error = (String) response.getEntity();
          returnList.add(new PowerSource(id + ";" + socket, false, error));
        }
      }
    }

    String json = gson.toJson(returnList);
    return Response.status(responseStatus).entity(json).build();

  }

}
