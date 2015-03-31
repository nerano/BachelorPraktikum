package mm.power.implementation;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class AEHomeTest {

    
    AeHome aehome = new AeHome("aehome", "AeHome", "192.168.178.21");
	
	/** 
	 * Tests if on the case of a thrown UnknownHostException, the Exception is handled correctly.
	 */
	@Test
	public void testGetStatesUnknownHostURLException() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		
		
		AeHome malformedURLpowerSupply = new AeHome("AeHome1", "AeHome", "unknownHost");
		
		Class<? extends AeHome> c = malformedURLpowerSupply.getClass();
		Method method = c.getDeclaredMethod("getStates", (Class<?>[]) null);
		method.setAccessible(true);
		Response r = (Response) method.invoke(malformedURLpowerSupply, (Object[]) null);
		
		String s =  (String) r.getEntity();
		boolean bool = s.contains("UnknownHostException");
		
	
		assertEquals("Status code must be 500", 500, r.getStatus());
		assertEquals("MessageEntity must contain a clue about UnknownHostException", true, bool);
	}
	/** 
	 * Tests if a invalid parameter for the socket parameter is handled correctly
	 */
	@Test
	public void testSocketNumberExceedsExistingSockets() {
		
	    
	    AeHome ps = new AeHome("AeHome1", "AeHome", "unknownHost");
		Response r = ps.status(5);
		
		String s =  (String) r.getEntity();
		
		boolean bool = s.contains("Socketnumber exceeds existing sockets on:");
	
		assertEquals("Status code must be 500", 500, r.getStatus());
		assertEquals("MessageEntity must contain a clue about exceeding Socketnumber", true, bool);
	}
	
	/**
	 * 
	 * Testing toggle for all three sockets.
	 */
	@Test
	@UseDataProvider("provideSockets1u3")
	public void testToggle(int socket) {
		Response response;
		boolean before = true;
		boolean after = true;
		response = aehome.status(socket);
		String state = (String) response.getEntity();
		
		if (response.getStatus() == 200) {
			if(state.equals("0")){
				before = false;
			} else {
				before = true;
			}
		} else {
			fail("BeforeStatus could not recieved for the first time");
		}
			
		response = aehome.toggle(socket);
		assertEquals("Status of the toggle Response must be 200", 200, response.getStatus());
		response = aehome.status(socket);
		state = (String) response.getEntity();
		
		if (response.getStatus() == 200) {
			if(state.equals("0")){
				after = false;
			} else {
				after = true;
			}
		} else {
			fail("AfterStatus could not recieved for the first time");
		}
	
		
		try { Thread.sleep(100); } catch (Exception e) {}
		
		assertEquals("Socket must be toggled for the first time", before, !after);
		
		//-! Second Toggle !-// 
		
		response = aehome.status(socket);
		state = (String) response.getEntity();
		
		if (response.getStatus() == 200) {
			if(state.equals("0")){
				before = false;
			} else {
				before = true;
			}
		} else {
			fail("BeforeStatus could not recieved for the second time");
		}
		
		
		response = aehome.toggle(socket);
		assertEquals("Status of the toggle Response must be 200", 200, response.getStatus());
		
		response = aehome.status(socket);
		state = (String) response.getEntity();
		
		if (response.getStatus() == 200) {
			if(state.equals("0")){
				after = false;
			} else {
				after = true;
			}
		} else {
			fail("AfterStatus could not recieved for the second time");
		}
		
		assertEquals("Socket must be toggled for the second time", before, !after);
		
		try { Thread.sleep(100); } catch (Exception e) {}
		
		
	}

	@Test
	@UseDataProvider("provideSockets1u3")
	public void testTurnOffFromOn(int socket) {
	    
	    Response response;
	    
	    response = aehome.turnOn(socket);
	    
	    if(response.getStatus() != 200) {
	        fail("Could not turn on the socket " + socket);
	    }
	    
	    response = aehome.turnOff(socket);
	    
        assertEquals("status code should be 200", 200, response.getStatus());
    
        response = aehome.status(socket);
        
        assertEquals("socket status should be 0", "0", (String) response.getEntity());
	}
	
	@Test
    @UseDataProvider("provideSockets1u3")
    public void testTurnOffFromOff(int socket) {
        
        Response response;
        
        response = aehome.turnOff(socket);
        
        if(response.getStatus() != 200) {
            fail("Could not turn off the socket " + socket);
        }
        
        response = aehome.turnOff(socket);
        
        assertEquals("status code should be 200", 200, response.getStatus());
    
        response = aehome.status(socket);
        
        assertEquals("socket status should be 0", "0", (String) response.getEntity());
    }

	@Test
	@UseDataProvider("provideSockets1u3")
	public void testTurnOn(int socket) {
	
		Response response = aehome.turnOn(socket);
	
		assertEquals("status code should be 200", 200, response.getStatus());
	
		response = aehome.status(socket);
		
		assertEquals("socket status should be 1", "1", (String) response.getEntity());
	
	}

	
	/**
	 * Turns on/off the sockets for each test case. Then retrieves the states from the outlet
	 * and checks if the expected and received state match.
	 */
	@Test
	@UseDataProvider("provideGetStatesTestCases")
	public void testGetStates(int[] sockets) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	    
	    // Setting up the sockets for the testcase
	    for (int i = 0; i < sockets.length; i++) {
            if(sockets[i] == 0) {
                aehome.turnOff(i+1);
            } else {
                aehome.turnOn(i+1);
            }
        }

	    // Getting the private method
	    Class<? extends AeHome> c = aehome.getClass();
        Method method = c.getDeclaredMethod("getStates", (Class<?>[]) null);
        method.setAccessible(true);
        
        
        Response r = (Response) method.invoke(aehome, (Object[]) null);
        
        assertEquals("Response Code should be 200", 200, r.getStatus());

        String states = (String) r.getEntity();
        
        // Checking if the returned states match the states set up
        for (int i = 0; i < sockets.length; i++) {
            assertEquals("", sockets[i], states.charAt(i) - 48 );
        }
	    
	}

	@Test
	@UseDataProvider("provideGetStatesTestCases")
	public void testStatusInt(int[] sockets) {
	    for(int i = 0; i < 3; i++) {
	      
	        if(sockets[i] == 0) {
	            aehome.turnOff(i+1);
	        } else {
	            aehome.turnOn(i+1);
	        }
	        
	        Response response = aehome.status(i+1);
	        
	        assertEquals("Response Code should be 200", 200, response.getStatus());
	        
	        String state = (String) response.getEntity();
	    
	        assertEquals("", sockets[i], state.charAt(0) - 48 );
	    }
	}

	
	
	
	@DataProvider
    public static Object[][] provideGetStatesTestCases() {
        
	    int[] sockets1 = {1, 0, 1};
	    int[] sockets2 = {1, 1, 1};
	    int[] sockets3 = {0, 0, 0};
	    
	    return new Object[][] { { sockets1 },
                                { sockets2 }, 
                                { sockets3 } }; 
        };
	
	@DataProvider
    public static Object[][] provideSockets1u3() {
        return new Object[][] { { 1 },
                                { 2 }, 
                                { 3 } }; 
        };
	
	
}
