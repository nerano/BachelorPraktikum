package mm.power;

public class SteckdoseHOME implements PowerSupply {

	int dosen;
	
	SteckdoseHOME(int dosen){
		
		this.dosen = dosen;
		
	}
	
	
	public boolean turnOn(int dose) {
		System.out.println("Dose " + dose + " wurde angeschaltet");
		return true;
	}

	public boolean turnOff(int dose) {
		System.out.println("Dose " + dose + " wurde ausgeschaltet");
		return true;
	}

	public boolean reset() {
		System.out.println("Steckdose ");
		return true;
	}


	public PowerSupply getType() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	

}
