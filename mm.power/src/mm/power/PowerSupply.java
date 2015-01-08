package mm.power;

import java.io.IOException;

public interface PowerSupply {

  public int dose = 0;
  
  PowerSupply getType();
  
  boolean turnOn(int dose) throws IOException;

  boolean reset() throws IOException;
  
  boolean turnOff(int dose) throws IOException;
}
