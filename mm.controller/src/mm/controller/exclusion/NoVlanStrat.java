package mm.controller.exclusion;


import java.lang.reflect.Type;
import java.util.LinkedList;

import mm.controller.modeling.Experiment;
import mm.controller.modeling.Interface;
import mm.controller.modeling.NodeObjects;
import mm.controller.modeling.VLan;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.reflect.TypeToken;

public class NoVlanStrat implements ExclusionStrategy {

    
	
	
	public boolean shouldSkipClass(Class<?> arg0) {
        return false;
    }

    public boolean shouldSkipField(FieldAttributes f) {

        Type vlanListType = new TypeToken<LinkedList<VLan>>() {}.getType();
        
        
        return 	(f.getDeclaringClass() == VLan.class && f.getName().equals("globalVlan")) ||
        		(f.getDeclaringClass() == vlanListType && f.getName().equals("localVlans")) ||
        		(f.getDeclaringClass() == Interface.class &&f.getName().equals("vlanId"));
    }

}