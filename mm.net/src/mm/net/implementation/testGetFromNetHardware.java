package mm.net.implementation;

import mm.net.modeling.VLan;

public class testGetFromNetHardware {

	 public static VLan get(int id){
		
		 VLan vlan = null;
		 
		 if(id == 123){
			 vlan = mm.net.main.Initialize.vlan1;
			 
		 }
		 
		 if(id == 124){
			 vlan = mm.net.main.Initialize.vlan2;
		 }
		 
		 return vlan;
		
	 }
	
	
	
}
