package mm.controller.exclusion;


import mm.controller.modeling.Component;
import mm.controller.modeling.Experiment;
import mm.controller.modeling.Interface;
import mm.controller.modeling.NodeObjects;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class NoStatusNodeStrat implements ExclusionStrategy {

    
	
	
	public boolean shouldSkipClass(Class<?> arg0) {
        return false;
    }

    public boolean shouldSkipField(FieldAttributes f) {

        return 	(f.getDeclaringClass() == Component.class && f.getName().equals("status")) ||
        		(f.getDeclaringClass() == Component.class && f.getName().equals("vLanIds")) ||
        		(f.getDeclaringClass() == NodeObjects.class && f.getName().equals("status")) ||
        		(f.getDeclaringClass() == Interface.class && f.getName().equals("vlanId")) ||
        		(f.getDeclaringClass() == Experiment.class &&f.getName().equals("vlans"));
    }

}