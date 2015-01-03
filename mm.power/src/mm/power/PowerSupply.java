package mm.power;

public interface PowerSupply {

  public int dose = 0;
  PowerSupply getType();
  
  boolean turnOn(int dose);

  boolean reset();
  
  boolean turnOff(int dose);
}
