package mm.controller.exclusion;
import mm.controller.modeling.Config;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;


public class OnlyConfigName implements ExclusionStrategy {

    public boolean shouldSkipClass(Class<?> arg0) {
        return false;
    }

    public boolean shouldSkipField(FieldAttributes f) {
        return  (f.getDeclaringClass() == Config.class && f.getName().equals("globalWire")) ||
                (f.getDeclaringClass() == Config.class && f.getName().equals("wires")) ||
                (f.getDeclaringClass() == Config.class && f.getName().equals("globals")) ||
                (f.getDeclaringClass() == Config.class && f.getName().equals("locals"));
    }
}




