package mm.controller.modeling;

import java.util.LinkedList;

import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mm.controller.main.ControllerData;
import mm.controller.net.ControllerNetGet;

public class WPort {

    String id;
    String port;
    String building;
    String room;
    String trunk;

    public WPort(String id, String port, String building, String room, String trunk) {

        this.id = id;
        this.port = port;
        this.building = building;
        this.room = room;
        this.trunk = trunk;

    }

    public String wportList() {
        return this.port;
    }

    public String getId() {
        return this.id;
    }

    public String getTrunk() {
        return trunk;
    }

    /**
     * Checks if the WPort is currently available.
     * 
     * <p>
     * <li>If the HTTP response status is 200 the WPort is available.</li>
     * <li>If the response status is 403 the WPort is currently under use, the
     * message body specifies by whom.</li>
     * <li>If the HTTP status is 500 an error occurred during retrieving the
     * availability, the error is specified in the message body.</li>
     * </p>
     * 
     * @return  an outbound response object with status code and message body.
     */
    public Response isAvailable() {

        LinkedList<Interface> infList = ControllerNetGet.getVlanInfo(this);

        int responseStatus = 200;
        String responseString = null;
        int vlanId;
        LinkedList<Interface> returnList = new LinkedList<Interface>();
        Experiment exp;

        for (Interface inf : infList) {
            vlanId = inf.getVlanId();
            switch (vlanId) {
            case 0:
                responseStatus = 500;
                returnList.add(inf);
                break;
            case 1:
                break;
            default:
                exp = ControllerData.getExpByVlanId(vlanId);
                if (exp == null) {
                    responseStatus = 403;
                    responseString = "Port is currently used, but could not identify the user, "
                            + "because the VLan ID '" + vlanId + "' is not in the system.\n";
                } else {
                    responseString = "Port '" + this.id + "' is currently used by User: '"
                            + exp.getUser()
                            + "'\n";
                    responseStatus = 403;
                }
                break;
            }
        }

        if (responseStatus == 500) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            responseString = gson.toJson(returnList);
        }

        if (responseStatus == 200) {
            responseString = "Node is free to use!";
        }

        return Response.status(responseStatus).entity(responseString).build();

    }

    public String getPort() {
        return this.port;
    }

}
