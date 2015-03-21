package mm.power.servlet;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.util.LinkedList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import mm.power.modeling.PowerSource;

import org.glassfish.jersey.client.ClientConfig;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tngtech.java.junit.dataprovider.*;

import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class PowerPutTest {

    private PowerPut pp = new PowerPut();
    private ClientConfig config    = new ClientConfig();
    private Client       client    = ClientBuilder.newClient(config);
    private WebTarget    putTarget = client.target(getPowerPutUri());
    private WebTarget    getTarget = client.target(getGetUri());
    private static Gson  gson      = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void testTurnOn404() {
        String parameter = "YouWontFindThis;1;YouWontFindThisToo;5;end";
        Response response = pp.turnOn(parameter);

        int responseCode = response.getStatus();
        String responseBody = (String) response.getEntity();
        System.out.println(responseBody);

        assertEquals("Response Code must be 404", 404, responseCode);
        assertEquals("Response Data must have 'not found'", true,
                responseBody.contains("not found"));
    }

    @DataProvider
    public static Object[][] provideFalseFormat() {
        return new Object[][] { { "FalseFormat1;1;FalseFormat2;test;end" },
                { "FalseFormat4;1;FalseFormat3;end" } };
    }

    @Test
    @UseDataProvider("provideFalseFormat")
    public void testTurnOnNotRightFormat(String parameter) {

        Response response = pp.turnOn(parameter);

        int responseCode = response.getStatus();
        String responseBody = (String) response.getEntity();
        System.out.println(responseBody);

        assertEquals("Response Code must be 500", 500, responseCode);
        boolean bool = (responseBody.contains("wrong format") || responseBody
                .contains("Exception"));
        assertTrue("Response Data must have 'wrong format' or exception", bool);
    }

    public static String parameter(String p) {
        return p;
    }
    /**
     * Used for testing the REST method turnOn in PowerPut. Does not only uses and tests the
     * method PowerPut.turnOn, but also PowerGet.getStatus for validating that the PowerSources
     * actually were turned on
     * 
     * @param parameter
     * @param expList
     */
    @Test
    @UseDataProvider("provideLegalFormatTrue")
    public void testTurnOn(String parameter, LinkedList<PowerSource> expList) throws UnsupportedEncodingException {

        // Turn on
        Response response = putTarget.path("turnOn").request().accept(MediaType.TEXT_PLAIN)
                .put(Entity.entity(parameter, MediaType.TEXT_PLAIN),
                        Response.class);

        assertEquals("Response Code must be 200 for turning On", 200, response.getStatus());

        
        // Get Status of changed PowerSources
        response = getTarget.path(URLEncoder.encode(parameter, "UTF-8"))
                    .request().get(Response.class);

        assertEquals("Response Code must be 200 for getting Status", 200, response.getStatus());

        Type type = new TypeToken<LinkedList<PowerSource>>() {}.getType();
        LinkedList<PowerSource> retList = gson.fromJson(response.readEntity(String.class), type);

        
        assertEquals("Expected list and returned List should be of the same size",
                        retList.size(), expList.size());
            
        // Check status of changed powerSources
        for (PowerSource powerSource : expList) {
            
            String id = powerSource.getId();
            
            for (PowerSource ps : retList) {
                
                if(ps.getId().equals(id)) {
                    assertEquals("Result has to be the same as the expected result"
                            , powerSource.isStatus(), ps.isStatus());
                    ps.setId("ALREADY CHECKED");
                }
            }
        }
    }

    /**
     * Used for testing the REST method turnOff in PowerPut. Does not only uses and tests the
     * method PowerPut.turnOff, but also PowerGet.getStatus for validating that the PowerSources
     * actually were turned off.
     * 
     * @param parameter
     * @param expList
     */
    @Test
    @UseDataProvider("provideLegalFormatFalse")
    public void testTurnOff(String parameter, LinkedList<PowerSource> expList) throws UnsupportedEncodingException {

        // Turn off
        Response response = putTarget.path("turnOff").request().accept(MediaType.TEXT_PLAIN)
                .put(Entity.entity(parameter, MediaType.TEXT_PLAIN),
                        Response.class);

        assertEquals("Response Code must be 200 for turning On", 200, response.getStatus());

        // Get Status of changed PowerSources
        response = getTarget.path(URLEncoder.encode(parameter, "UTF-8"))
                    .request().get(Response.class);

        assertEquals("Response Code must be 200 for getting Status", 200, response.getStatus());

        Type type = new TypeToken<LinkedList<PowerSource>>() {}.getType();
        LinkedList<PowerSource> retList = gson.fromJson(response.readEntity(String.class), type);

        assertEquals("Expected list and returned List should be of the same size",
                        retList.size(), expList.size());

        // Check status of changed powerSources
        for (PowerSource powerSource : expList) {
            
            String id = powerSource.getId();
            
            for (PowerSource ps : retList) {
                
                if(ps.getId().equals(id)) {
                    assertEquals("Result has to be the same as the expected result"
                            , powerSource.isStatus(), ps.isStatus());
                    ps.setId("ALREADY CHECKED");
                }
            }
        }
    }

    /**
     * Provides legal Formats with the respective PowerSources with status 'false'. 
     * Used in following Tests: 
     *      turnOff
     */
    @DataProvider
    public static Object[][] provideLegalFormatTrue() {

        String parameter1 = "AeHome1;1;AeHome2;2;end";
        String parameter2 = "AeHome1;1;end";
        String parameter3 = "AeHome1;1;AeHome2;3;AeHome1;2;end";

        LinkedList<PowerSource> expList1 = new LinkedList<PowerSource>();
        LinkedList<PowerSource> expList2 = new LinkedList<PowerSource>();
        LinkedList<PowerSource> expList3 = new LinkedList<PowerSource>();

        PowerSource ps1 = new PowerSource("AeHome1;1", true);
        PowerSource ps3 = new PowerSource("AeHome1;3", true);

        PowerSource ps4 = new PowerSource("AeHome2;2", true);
        PowerSource ps5 = new PowerSource("AeHome2;3", true);

        expList1.add(ps1);
        expList1.add(ps4);

        expList2.add(ps1);

        expList3.add(ps1);
        expList3.add(ps3);
        expList3.add(ps5);

        return new Object[][] { { parameter1, expList1 },
                                { parameter2, expList2 },
                                { parameter3, expList3 }
        };
    }

    /**
     * Provides legal Formats with the respective PowerSources with status 'true'. 
     * Used in following Tests: 
     *      turnOn
     */
    @DataProvider
    public static Object[][] provideLegalFormatFalse() {

        String parameter1 = "AeHome1;1;AeHome2;2;end";
        String parameter2 = "AeHome1;1;end";
        String parameter3 = "AeHome1;1;AeHome2;3;AeHome1;2;end";

        LinkedList<PowerSource> expList1 = new LinkedList<PowerSource>();
        LinkedList<PowerSource> expList2 = new LinkedList<PowerSource>();
        LinkedList<PowerSource> expList3 = new LinkedList<PowerSource>();

        PowerSource ps1 = new PowerSource("AeHome1;1", false);
        PowerSource ps3 = new PowerSource("AeHome1;3", false);

        PowerSource ps4 = new PowerSource("AeHome2;2", false);
        PowerSource ps5 = new PowerSource("AeHome2;3", false);

        expList1.add(ps1);
        expList1.add(ps4);

        expList2.add(ps1);

        expList3.add(ps1);
        expList3.add(ps3);
        expList3.add(ps5);

        return new Object[][] { { parameter1, expList1 },
                { parameter2, expList2 },
                { parameter3, expList3 }
        };
    }

    private static URI getPowerPutUri() {
        return UriBuilder.fromUri("http://localhost:8080/mm.power/rest/put/").build();
    }

    private static URI getGetUri() {
        return UriBuilder.fromUri("http://localhost:8080/mm.power/rest/get/")
                .build();
    }
}
