package mm.auth.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.HashMap;
import java.util.UUID;
import javax.ws.rs.core.Response;

public class SessionTest {

  Session session = new Session();
  
  @Test
  public void testCreateSession() {
    String user = "test";
    String pw = "test";
    HashMap<UUID, SessionData> ids = new HashMap<UUID, SessionData>();
    
    Response response = session.createSessionId(user, pw);
    assertEquals(200, response.getStatus());
    ids = this.session.getIdMap();
    assertTrue(ids.containsKey(
        UUID.fromString(response.readEntity(String.class).substring(0, 36))));
  }

}
