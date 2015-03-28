package mm.controller.servlet;

import java.util.LinkedList;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mm.controller.auth.WebAuthTest;
import mm.controller.exclusion.NoStatusNodeStrat;
import mm.controller.exclusion.OnlyConfigName;
import mm.controller.main.ControllerData;
import mm.controller.main.Initialize;
import mm.controller.modeling.Experiment;
import mm.controller.modeling.NodeObjects;
import mm.controller.modeling.PowerSource;
import mm.controller.net.ControllerNetGet;

/**
 * Class for all GET methods in the controller servlet
 * 
 */
@Path("/get")
@Singleton
public class ControllerGet {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    @GET
    @Path("/test")
    public Response test() {
        Initialize.saveToDisk();
        return Response.ok().build();
    }
    
    
    /**
     * Returns all nodes.
     *
     * <p>
     * Returns all nodes which are known to the controller; these are all nodes
     * specified in the XML-file and parsed on startup. No dynamic information
     * is serialized, only static. E.g. the VLAN and power status is not
     * serialized, but the location.
     *
     * <p>
     * URI: <code> baseuri:port/mm.controller/rest/get/nodes </code>
     * </p>
     *
     * <p>
     * A valid session ID should be provided within the Header "sessionId"
     * otherwise an access to this method is not possible.
     * </p>
     *
     * The Response Object holds a JSON representation of a list, which contains
     * all nodes. This method can produce the mime types text/plain or
     * application/json.
     *
     * @param sessionId
     *            a session Id which can be validated from the AuthService
     * @return a response object with status code 200 or 403 if the session ID
     *         is not valid
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    @Path("/nodes")
    public Response getAllNodes() {

        Gson gson = new GsonBuilder().setExclusionStrategies(new NoStatusNodeStrat())
                .setExclusionStrategies(new OnlyConfigName()).setPrettyPrinting().create();
        LinkedList<NodeObjects> list = ControllerData.getAllNodesAsList();

        String responseString = gson.toJson(list);

        System.out.println(responseString);
        return Response.status(200).entity(responseString).build();
    }

    /**
     * Returns a list of all available Configs.
     *
     * <p>
     * All available Configs are parsed from the configs.xml on startup or after
     * a reload was issued.
     * </p>
     *
     * <p>
     * This method procudes either the MIME type application/json or text/plain.
     * A valid session ID should be provided within the Header "sessionId"
     * otherwise an access to this method is not possible.
     * </p>
     *
     * <p>
     * URI: <code>baseuri:port/mm.controller/rest/get/configs </code>
     * </p>
     *
     * @param sessionId
     *            a session Id which can be validated from the AuthService
     * @return a response object with status code 200 or 403 it the session ID
     *         is not valid
     */
    @GET
    @Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
    @Path("/configs")
    public Response getConfigs() {

        Gson gson = new GsonBuilder().setExclusionStrategies(new OnlyConfigName())
                .setPrettyPrinting().create();

        String responseString = gson.toJson(ControllerData.getAllConfigs());

        System.out.println(responseString);
        return Response.ok(responseString).build();

    }

    /**
     * Returns a list of all available ports.
     *
     * <p>
     * The list of all ports is parsed by the server on the startup or after
     * issuing a reload command, but only if a valid sessionId is provided,
     * which can be checked by the AuthService.
     * </p>
     *
     * <p>
     * This method returns either the MIME text/plain or application/json
     * </p>
     *
     * <p>
     * URI: <code>baseuri:8080/mm.controller/rest/get/ports</code>
     * </p>
     *
     * @param sessionId
     *            a session Id which can be validated from the AuthService
     * @return a response object with status code 200 (OK) or 403 (forbidden) if
     *         a invalid sessionId was provided
     *
     */
    @GET
    @Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
    @Path("/ports")
    public Response getPorts() {

        String responseString = gson.toJson(ControllerData.getAllWPorts());

        System.out.println(responseString);
        return Response.ok(responseString).build();

    }

    /**
     * This method returns a experiment with live power data.
     *
     * <p>
     * The power status in this experiment is updated, when this method is
     * called. The information is provided by the PowerService. Does not change
     * the state of any PowerSupply. The live data can only be retrieved if the
     * experiment is in the state of "running". A paused or stopped experiment
     * can not retrieve power status data.
     * </p>
     *
     * <p>
     * This method produces either the MIME text/plain or application/json
     * <p>
     *
     * @param sessionId
     *            a session Id which can be validated from the AuthService
     * @param exp
     *            the id of the experiment
     * @return a response object with status code and message body
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/status/{exp}")
    public Response getNodePowerStatus(@PathParam("exp") String exp) {

        Experiment experiment = ControllerData.getExpById(exp);
        
        if(experiment == null) {
            return Response.status(404).entity("404, Experiment not found!").build();
        }

        if(!experiment.getStatus().equals(Experiment.PossibleState.running)) {
            return Response.status(403).entity("Can not do this while the experiment is " 
                    + experiment.getStatus()).build();
        }
        
        LinkedList<PowerSource> psrc = experiment.status();
        experiment.updateNodeStatusPower(psrc);
        return Response.ok(gson.toJson(experiment)).build();
    }

    /**
     * Returns an experiment.
     *
     * <p>
     * Returns an experiment with the given ID in JSON format, the ID is passed
     * through the URI The Response Objects holds the JSON in the message body
     * </p>
     * 
     * <p>
     * URI: baseuri:port/mm.controller/rest/get/exp/{id}
     * </p>
     *
     *
     * Possible HTTP status codes:
     *
     * <li>200: The requested experiment is located in the message body.</li>
     * <li>403: The provided session ID was not valid the owner of this ID can
     * not access this experiment</li> <li>404: The requested experiment with
     * the given ID does not exist.</li>
     *
     * @param sessionId
     *            a session Id which can be validated from the AuthService
     * @param id
     *            Identifier of the experiment
     * @return a response object with status code and message body
     */
    @GET
    @Produces({ "json/application", "text/plain" })
    @Path("/exp/{id}")
    public Response getExpById(@HeaderParam("sessionId") String sessionId,
            @PathParam("id") String id) {

        String responseString;
        Response response = WebAuthTest.checkSession(sessionId);
        if (sessionId != null) {
            if (response.getStatus() == 200) {
                if (!(ControllerData.existsExp(id))) {
                    responseString = "404, Experiment not found";
                    response = Response.status(404).entity(responseString).build();
                    return response;
                }

                Gson gson = new GsonBuilder()/*
                                              * .setExclusionStrategies(new
                                              * NoStatusNodeStrat())
                                              */
                        .setPrettyPrinting().create();

                /*
                 * if
                 * (auth.getUserRole(sessionId).readEntity(String.class).equals
                 * (exp.getUser())) {
                 */
                responseString = gson.toJson(ControllerData.getExpById(id));
                /* } */

                System.out.println(responseString);
                response = Response.status(200).entity(responseString).build();

                return response;
            }
            return response;
        } else {
            return Response.status(401).entity("No SessionId contained!").build();
        }
    }

    /**
     * 
     * @param net
     * @return
     */
    @GET
    @Produces({ "json/application", "text/plain" })
    @Path("/staticConsistency/{net}")
    public Response getStaticConsistency(@PathParam("net") String net) {
        return Response.ok(ControllerNetGet.staticConsistency(net)).build();
    }

    /**
     * 
     * @param net
     * @return
     */
    @GET
    @Produces({ "json/application", "text/plain" })
    @Path("/expConsistency/{id}")
    public Response getExpConsistency(@PathParam("id") String id) {

        Experiment exp = ControllerData.getExpById(id);

        if (exp == null) {
            return Response.status(404).entity("404, Experiment not found!").build();
        }

        String consistency = exp.isConsistent();

        return Response.ok(consistency).build();
    }

    /**
     * Returns a list of all experiments from the owner of the provided session
     * ID
     *
     * <p>
     * Returns a list of all currently existing experiments from the owner of
     * the provided session ID. If the owner has the role admin or a role with
     * similar rights a list of all existing experiments is returned.
     * </p>
     *
     * <p>
     * URI: <code> baseuri:port/mm.controller/rest/get/exp </code>
     * </p>
     * 
     * <p>
     * Produces either the MIME type text/plain or application/json
     * </p>
     *
     * Possible HTTP status codes: <li>200: Header contains valid sessionId. <li>
     * 401: Unauthorized: Header does not contain a sessionId.
     *
     * @param sessionId
     *            a session Id which can be validated from the AuthService
     * @return a Response Object with the JSON in the message body.
     */
    @GET
    @Produces({ "json/application", "text/plain" })
    @Path("/exp")
    public Response getAllExp(@HeaderParam("sessionId") String sessionId) {
        /**
         * test case if the role management works.
         **/

        System.out.println(sessionId);

        Response response = WebAuthTest.checkSession(sessionId);
        if (sessionId != null) {
            if (response.getStatus() == 200) {
                String user = WebAuthTest.getUser(sessionId).readEntity(String.class);
                if (WebAuthTest.getUserRole(sessionId).readEntity(String.class).equals("admin")) {
                    System.out.println(gson.toJson(ControllerData.getAllExp()));
                    return Response.ok(gson.toJson(ControllerData.getAllExp())).build();
                } else {
                    String responseString = gson.toJson(ControllerData.getExpByUser(user));
                    return Response.ok(responseString).build();
                }
            } else {
                return response;
            }
        } else {
            return Response.status(401).encoding("No SessionId contained!").build();
        }
    }
}
