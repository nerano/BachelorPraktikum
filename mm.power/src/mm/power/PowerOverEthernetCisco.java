package mm.power;

public class PowerOverEthernetCisco implements PowerSupply{

	int ports;
	
	PowerOverEthernetCisco(int ports){
		
		this.ports = ports;
		
		}
	
	public boolean turnOn(int dose) {
		System.out.println("Buchse " + dose + " wurde angeschaltet");
		return true;
	}

	public boolean turnOff(int dose) {
		System.out.println("Buchse " + dose + " wurde ausgeschaltet");
		return true;
	}

	public boolean reset() {
		System.out.println("Buchse PoE Reset ");
		return true;
	}

	public PowerSupply getType() {
		// TODO Auto-generated method stub
		return null;
	}
	
	






}
