package mm.controller.exclusion;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class OnlyConfigNamesStrat implements ExclusionStrategy {

    public boolean shouldSkipClass(Class<?> arg0) {
        return (arg0 != String.class);
    }

    public boolean shouldSkipField(FieldAttributes f) {

        return (/** f.getDeclaringClass() == Component.class && f.getName().equals("status"))|| **/
        //!(f.getDeclaringClass() == String.class)); 
                false);
    }

}