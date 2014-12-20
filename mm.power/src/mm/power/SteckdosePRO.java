package mm.power;

public class SteckdosePRO implements PowerSupply {

	int dosen;
	
	SteckdosePRO(int dosen){
		
		this.dosen = dosen;
		
	}
	
	
	public boolean turnOn(int dose) {
		System.out.println("PRO Dose " + dose + " wurde angeschaltet");
		return true;
	}

	public boolean turnOff(int dose) {
		System.out.println("PRO Dose " + dose + " wurde ausgeschaltet");
		return true;
	}

	public boolean reset() {
		System.out.println("PRO Steckdose resettet ");
		return true;
	}


	public PowerSupply getType() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	

}
