package mm.power.servlet;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mm.power.exceptions.EntryDoesNotExistException;
import mm.power.exceptions.TransferNotCompleteException;
import mm.power.main.PowerData;
import mm.power.modeling.PowerSupply;

@Path("/post")
public class PowerPost {

  @POST
  @Path("/toggle/{incoming}")
  @Consumes(MediaType.TEXT_PLAIN)
  public Response toggle(String incoming) throws TransferNotCompleteException, IOException,
      EntryDoesNotExistException {

    String[] parts = incoming.split(";");
    String id;
    int socket;

    if (!(parts[parts.length - 1].equals("end"))) {

      throw new TransferNotCompleteException("Transfer not Complete in PowerPut turnOn. Message : "
          + incoming);
    }

    PowerSupply ps;

    for (int i = 0; i < parts.length - 2; i = i + 2) {

      id = parts[i];
      socket = Integer.parseInt(parts[i + 1]);

      if (!(PowerData.exists(id))) {
        return Response.status(404).entity("404, PowerSource '" + id + "' not found").build();
      }

      ps = PowerData.getById(id);
      ps.toggle(socket);

    }

    return Response.status(200).entity("All PowerSources successful toggled").build();

  }

}
