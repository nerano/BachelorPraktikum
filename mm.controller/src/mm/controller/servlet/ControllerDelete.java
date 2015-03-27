package mm.controller.servlet;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mm.controller.main.ControllerData;
import mm.controller.modeling.Experiment;

@Path("/delete")
public class ControllerDelete {

    /**
     * Deletes an experiment.
     * 
     * <p>
     * URI: <code>baseuri:port/mm.controller/rest/delete/exp</code>
     * </p>
     * 
     * <p>
     * The message body contains a human readable form of the status code The
     * experiment has to be in the "stopped" state to delete.
     * </p>
     * 
     * <p>
     * All affiliated Virtual Machines are deleted from their respective servers
     * and the global VLAN affiliated with the experiment is deleted from all
     * NetComponents which were member of this experiment
     * </p>
     * 
     * Possible HTTP status codes:
     * 
     * <li>200: Returned if the experiment with the given ID was removed <li>
     * 404: Returned if there was not experiment with the given ID
     * 
     * @param id
     *            Identifier of the Experiment to delete
     * @return a response object
     */
    @DELETE
    @Path("/exp/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteExperiment(@PathParam("id") String id) {

        System.out.println("DELETE EXPERIMENT: " + id);

        Experiment experiment = ControllerData.getExpById(id);

        Response response;
        String responseString;
        if (experiment != null) {
            
            // Check if experiment is stopped
            if (!experiment.getStatus().equals("stopped")) {
                return Response.status(400).entity("Can not do this in the state "
                        + experiment.getStatus()).build();
            }
            
            // Destroy all data from the experiment
            experiment.destroy();
            ControllerData.removeExp(experiment);
            responseString = "Experiment with ID '" + id + "' was removed";
            response = Response.status(200).entity(responseString).build();
            return response;
        } else {
            responseString = "Experiment with ID '" + id + "' was not found";
            response = Response.status(404).entity(responseString).build();
            return response;
        }

    }

}
