package mm.controller.exclusion;

import mm.controller.modeling.Component;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class GetPowerNodeStrat implements ExclusionStrategy {

    public boolean shouldSkipClass(Class<?> arg0) {
        return false;
    }

    public boolean shouldSkipField(FieldAttributes f) {

        return (/** f.getDeclaringClass() == Component.class && f.getName().equals("status"))|| **/
        (f.getDeclaringClass() == Component.class && f.getName().equals("vLanId")));
    }

}