package mm.net.implementation;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class NetGearGS108Tv2Test {

    LinkedList<Integer> trunks = new LinkedList<Integer>(Arrays.asList(1));
    NetGearGS108Tv2     nc     = new NetGearGS108Tv2("NetGear2", "netgeargs2", trunks);

    @Before
    public void executedBeforeEach() {
        nc.start();
        nc.reset();
    }

    @After
    public void executedAfterEach() {
        nc.reset();
        nc.stop();
    }

    
   /** @Test
    public void testSetTrunkPort(int port, int vlanId, String name) {
        
        
        
        
    } **/
    
    
    
    
    
    /**
     * Tests the getter and setter for the PVID. Sets a PVID and then gets it
     * back and checks if they both are equals
     * 
     * @param port
     *            port on which the pvid is set
     * @param pvid
     *            pvid to set on the selected port
     * @throws InterruptedException
     *             the thread has to wait some time before issuing the next test
     *             otherwise the network is not fast enough and the test will
     *             fail
     */
    @Test
    @UseDataProvider("providePortsAndPVID1u8")
    public void testPVID(int port, int pvid) throws InterruptedException {

        Thread.sleep(250);

        Response response = nc.setPVID(port, pvid);

        assertEquals("response Status from setPVID code should be 200",
                200, response.getStatus());

        response = nc.getPVID(port);

        assertEquals("response Status from getPVID code should be 200",
                200, response.getStatus());

        int retPvid = Integer.parseInt((String) response.getEntity());

        assertEquals("returned PVID should be equal to the expected pvid", pvid, retPvid);
    }

    /**
     * Test the setting and getting of egress ports in the NetGearGS108Tv2.
     * Creates a new VLAN, sets the input egress ports and gets them back.
     * Compares these two values if they are equal.
     * 
     * @param vlanId
     *            which VLAN to create
     * @param egress
     *            what egress ports to set
     * @throws InterruptedException
     *             the thread has to wait some time before issuing the next test
     *             otherwise the network is not fast enough and the test will
     *             fail
     */
    @Test
    @UseDataProvider("provideVlanIdsWithPortSettings")
    public void testEgress(int vlanId, String egress) throws InterruptedException {

        Thread.sleep(750);

        Response response = nc.setRowStatus(vlanId, 4);

        assertEquals("response Status code from setRowStatus should be 200",
                200, response.getStatus());

        response = nc.setEgressPorts(vlanId, egress);

        assertEquals("response Status code from setEgress should be 200",
                200, response.getStatus());

        response = nc.getEgressPorts(vlanId);

        String retEgress = (String) response.getEntity();
        
        // Transform the hexadecimal input egress into a binary
        // to compare the input and the output
        if (egress.length() == 2) {
            egress = new BigInteger(retEgress, 16).toString(2);
            egress = String.format("%8s", retEgress).replace(' ', '0');
        }

        assertEquals("returned egress ports should be the same as expected egress ports",
                egress, retEgress);
        
        
    }
    
    @Test
    public void getUntaggedOnInvalidVlan() {
        
        Response  response = nc.getUntaggedPorts(777);
        
        String retString = (String) response.getEntity();
        
        assertEquals("response Status code from getUntagged should be 500",
                500, response.getStatus());
        
        assertEquals("response from getUntagged should be noSuchInstance",
                "noSuchInstance", retString);
    }
    
    @Test
    public void getNameOnInvalidVlan() {
        
        Response  response = nc.getStaticName(777);
        
        String retString = (String) response.getEntity();
        
        assertEquals("response Status code from getStaticName should be 500",
                500, response.getStatus());
        
        assertEquals("response from getStaticName should be noSuchInstance",
                "noSuchInstance", retString);
    }
    
    @Test
    public void getEgressOnInvalidVlan() {
        
        Response  response = nc.getEgressPorts(777);
        
        String retString = (String) response.getEntity();
        
        assertEquals("response Status code from getUntagged should be 500",
                500, response.getStatus());
        
        assertEquals("response from getUntagged should be noSuchInstance",
                "noSuchInstance", retString);
    }
    
    @Test
    public void getPvidOnInvalidPort() {
        
        Response response = nc.getPVID(777);
        
        String retString = (String) response.getEntity();
        
        assertEquals("response Status code from getPVID should be 500",
                500, response.getStatus());
        
        assertEquals("response from getPVID should be noSuchInstance",
                "noSuchInstance", retString);
    }
    
    
    /**
     * 
     * @param vlanId
     * @param egress
     */
    @Test
    @UseDataProvider("provideVlanIdsWithInvalidPortSettings")
    public void testEgressInvalidFormat(int vlanId, String egress) {
        
        Response response = nc.setEgressPorts(vlanId, egress);
        String retEgress = (String) response.getEntity();
        
        assertEquals("response Status code from setEgress should be 500",
                500, response.getStatus());
        
        System.out.println(retEgress);
    }
    
    /**
     * 
     * @param vlanId
     * @param untagged
     */
    @Test
    @UseDataProvider("provideVlanIdsWithInvalidPortSettings")
    public void testUntaggedInvalidFormat(int vlanId, String untagged) {
        
        Response response = nc.setUntaggedPorts(vlanId, untagged);
        String retUntagged = (String) response.getEntity();
        
        assertEquals("response Status code from setEgress should be 500",
                500, response.getStatus());
        
        System.out.println(retUntagged);
    }

    /**
     * Test the setting and getting of untagged ports in the NetGearGS108Tv2.
     * Creates a new VLAN, sets the input untagged ports and gets them back.
     * Compares these two values if they are equal.
     * 
     * @param vlanId
     * @param egress
     * @throws InterruptedException
     *             the thread has to wait some time before issuing the next test
     *             otherwise the network is not fast enough and the test will
     *             fail
     */
    @Test
    @UseDataProvider("provideVlanIdsWithPortSettings")
    public void testUntagged(int vlanId, String egress) throws InterruptedException {

        Thread.sleep(750);

        Response response = nc.setRowStatus(vlanId, 4);

        assertEquals("response Status code from setRowStatus should be 200",
                200, response.getStatus());

        response = nc.setUntaggedPorts(vlanId, egress);

        assertEquals("response Status code from setEgress should be 200",
                200, response.getStatus());

        response = nc.getUntaggedPorts(vlanId);

        String retEgress = (String) response.getEntity();

        // Transform the hexadecimal input egress into a binary
        // to compare the input and the output
        if (egress.length() == 2) {
            egress = new BigInteger(egress, 16).toString(2);
            egress = String.format("%8s", egress).replace(' ', '0');
        }

        assertEquals("returned egress ports should be the same as expected egress ports",
                egress, retEgress);
    }

    /**
     * Creates a new VLAN, sets the provided name, gets the name back and
     * compares these two.
     * 
     * @param vlanId
     *            VLAN to create
     * @param name
     *            name to set on the created VLAN
     * @throws InterruptedException
     *             the thread has to wait some time before issuing the next test
     *             otherwise the network is not fast enough and the test will
     *             fail
     */
    @Test
    @UseDataProvider("provideVlanIdsWithNames")
    public void testStaticName(int vlanId, String name) throws InterruptedException {

        Thread.sleep(750);

        // Creating the VLAN
        Response response = nc.setRowStatus(vlanId, 4);

        assertEquals("response Status code from setRowStatus should be 200",
                200, response.getStatus());

        response = nc.setStaticName(vlanId, name);

        assertEquals("response Status code from setStaticName should be 200",
                200, response.getStatus());

        response = nc.getStaticName(vlanId);
        String retName = (String) response.getEntity();

        assertEquals("response Status code from getStaticName should be 200",
                200, response.getStatus());

        assertEquals("returned name should be the same as the input name",
                name, retName);

    }

    /**
     * 
     * @param vlanId
     *            a vlanId
     * @param unused
     *            not used in this test
     * @throws InterruptedException
     */
    @Test
    @UseDataProvider("provideVlanIdsWithPortSettings")
    public void destroyVlan(int vlanId, String unused) throws InterruptedException {

        Thread.sleep(750);

        // Create new VLAN
        Response response = nc.setRowStatus(vlanId, 4);

        assertEquals("response Status code from setRowStatus should be 200",
                200, response.getStatus());

        // Destroy VLAN
        response = nc.destroyVlan(vlanId);

        assertEquals("response Status code from destroyVlan should be 200",
                200, response.getStatus());

        // Check if VLAN exists
        response = nc.getStaticName(vlanId);
        String returnString = (String) response.getEntity();

        assertEquals("response Status code from getStaticName should be 500 because VLAN"
                + "does not exists", 500, response.getStatus());

        assertEquals("Message should be 'noSuchInstance' in getStaticName",
                "noSuchInstance", returnString);

        // System.out.println("STATUS: " + response.getStatus());
        // System.out.println("BODY: " + (String) response.getEntity());

    }
    
    
    @Test
    @UseDataProvider("provideVlanIdWithEgressAndUntagged")
    public void testEgressAndUntaggedPorts(int vlanId, String egress, String untagged) throws InterruptedException {
        
        Thread.sleep(750);
        Response response;
        
        nc.setRowStatus(vlanId, 4);
        nc.setEgressPorts(vlanId, egress);
        nc.setUntaggedPorts(vlanId, untagged);
        
        response = nc.getEgressAndUntaggedPorts(vlanId);
        
        String[] ret = (String[]) response.getEntity();
        
        assertEquals("response Status code from getEgressAndUntagged should be 200",
                200, response.getStatus());
        
        
        String retEgress = ret[0];
        String retUntagged = ret[1];
        
        if (egress.length() == 2) {
            egress = new BigInteger(egress, 16).toString(2);
            egress = String.format("%8s", egress).replace(' ', '0');
        }
        
        if (untagged.length() == 2) {
            untagged = new BigInteger(untagged, 16).toString(2);
            untagged = String.format("%8s", untagged).replace(' ', '0');
        }
        
        assertEquals("returned egress should be the same as input egress",
                egress, retEgress);
        
        assertEquals("returned untagged should be the same as input untagged",
                untagged, retUntagged);
        
        
    }
    
    
    @Test
    public void start() {
        
        NetGearGS108Tv2 ng = new  NetGearGS108Tv2("netgear3", "illegaladdress", null);
        
        Response response = ng.start();
        
        System.out.println("STATUS: " + response.getStatus());
        System.out.println("BODY: " + (String) response.getEntity());
        
        
    }
    @Test
    public void reset() {

        
        NetGearGS108Tv2 ng = new  NetGearGS108Tv2("netgear3", "netgeargs2", null);
        ng.start();
        System.out.println(ng.isFree(100));
        ng.stop();
        // Response response = nc.getEgressPorts(0);

        // System.out.println("STATUS: " + response.getStatus());
        // System.out.println("BODY: " + (String) response.getEntity());
    }

    
    
    
    @DataProvider
    public static Object[][] provideVlanIdWithEgressAndUntagged() {

        return new Object[][] { { 5,     "00000000", "00000000" },
                                { 6,     "11111111", "11111111" },
                                { 4093,  "10101010", "01010101" },
                                { 3000,  "11100011", "89" },
                                { 2000,  "FF", "00" },
                                { 4001,  "00", "FF" },
                                { 2700,  "AB", "18" },
        };
    };
    
    
    @DataProvider
    public static Object[][] provideVlanIdsWithNames() {

        return new Object[][] { { 5, "name" },
                { 6, "name2" },
                { 4093, "112927928" },
                { 2000, "FF" },
                { 3000, "00" },
        };
    };

    @DataProvider
    public static Object[][] provideVlanIdsWithPortSettings() {

        return new Object[][] { { 5, "11111111" },
                { 6, "00000000" },
                { 57, "10101010" },
                { 4093, "AA" },
                { 2000, "FF" },
                { 3000, "00" },
        };
    };
    
    @DataProvider
    public static Object[][] provideVlanIdsWithInvalidPortSettings() {

        return new Object[][] { { 5, "111" },
                { 6, "AAA" },
                { 57, "1" },
                { 4093, "B" },
                { 2000, "BBBBBBBBB" },
                { 3000, "GG" },
                { 3000, "0101010A" },
        };
    };

    @DataProvider
    public static Object[][] providePortsAndPVID1u8() {
        return new Object[][] { { 1, 1 },
                { 2, 2 },
                { 3, 10 },
                { 4, 100 },
                { 5, 120 },
                { 6, 3000 },
                { 7, 4000 },
                { 8, 4093 },
        };
    };
}
