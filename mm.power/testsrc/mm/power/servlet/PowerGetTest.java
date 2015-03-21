package mm.power.servlet;

import static org.junit.Assert.assertEquals;

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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class PowerGetTest {

    private static ClientConfig config    = new ClientConfig();
    private static Client       client    = ClientBuilder.newClient(config);
    private static WebTarget    getTarget = client.target(getBaseUri());
    private static Gson         gson      = new GsonBuilder().setPrettyPrinting().create();

    @Before
    public void executedBeforeEach() {
        // Setting up the right states of the outlet
        WebTarget putTarget = client.target
                (UriBuilder.fromUri("http://localhost:8080/mm.power/rest/put/")
                        .build());

        String parameter = "AeHome1;2;AeHome1;3;AeHome2;2;end";

        Response response = putTarget.path("turnOff").request().accept(MediaType.TEXT_PLAIN)
                .put(
                        Entity.entity(parameter, MediaType.TEXT_PLAIN),
                        Response.class);

        assertEquals("@Before response Status has to be 200", response.getStatus(), 200);

        parameter = "AeHome1;1;end";

        response = putTarget.path("turnOn").request().accept(MediaType.TEXT_PLAIN)
                .put(
                        Entity.entity(parameter, MediaType.TEXT_PLAIN),
                        Response.class);

        assertEquals("@Before response Status has to be 200", response.getStatus(), 200);

    }

    @Test
    @UseDataProvider("provideLegalIncoming")
    public void testStatus(String parameter, LinkedList<PowerSource> expList) {

        Response response = getTarget.path(parameter).request().get(Response.class);

        Type type = new TypeToken<LinkedList<PowerSource>>() {}.getType();
        LinkedList<PowerSource> list = gson.fromJson(response.readEntity(String.class), type);

        assertEquals("Response Code must be 200", 200, response.getStatus());

        String id;
        for (PowerSource powerSource : expList) {

            id = powerSource.getId();

            for (PowerSource ps : list) {

                if (ps.getId().equals(id)) {
                    assertEquals("Result has to be the same as the expected result"
                            , powerSource.isStatus(), ps.isStatus());
                }
            }
        }
    }

    @DataProvider
    public static Object[][] provideLegalIncoming() throws UnsupportedEncodingException {

        String inc1 = URLEncoder.encode("AeHome1;1;end", "UTF-8");
        String inc2 = URLEncoder.encode("AeHome1;2;AeHome1;1;end", "UTF-8");
        String inc3 = URLEncoder.encode("AeHome1;3;AeHome2;1;end", "UTF-8");

        LinkedList<PowerSource> expList1 = new LinkedList<PowerSource>();
        LinkedList<PowerSource> expList2 = new LinkedList<PowerSource>();
        LinkedList<PowerSource> expList3 = new LinkedList<PowerSource>();

        PowerSource ps1 = new PowerSource("AeHome1;1", true);
        PowerSource ps2 = new PowerSource("AeHome1;2", false);
        PowerSource ps3 = new PowerSource("AeHome1;3", false);

        PowerSource ps4 = new PowerSource("AeHome2;3", false);

        expList1.add(ps1);

        expList2.add(ps1);
        expList2.add(ps2);

        expList3.add(ps3);
        expList3.add(ps4);

        return new Object[][] { { inc1, expList1 },
                                { inc2, expList2 },
                                { inc3, expList3 },

        };
    }

    @DataProvider
    public static Object[][] provideFalseFormat() {
        return new Object[][] { { "FalseFormat1;1;FalseFormat2;test;end" },
                { "FalseFormat4;1;FalseFormat3;end" } };
    }

    private static URI getBaseUri() {
        return UriBuilder.fromUri("http://localhost:8080/mm.power/rest/get/")
                .build();
    }

}
