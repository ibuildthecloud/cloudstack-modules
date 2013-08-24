package org.apache.cloudstack.spring.module.util;

public class ModulePathUtils {

    public static final String MODULE_PROPERTIES = "classpath:%s/%s/module.properties";
    public static final String CONTEXT_LOCATION = "classpath*:%s/%s/*context.xml";
    public static final String DEFAULTS_LOCATION = "classpath*:%s/%s/*defaults.properties";

    public static String getModuleLocation(String baseDir, String name) {
        return String.format(MODULE_PROPERTIES, baseDir, name);
    }
    
    public static String getContextLocation(String baseDir, String name) {
        return String.format(CONTEXT_LOCATION, baseDir, name);
    }
    
    public static String getDefaultsLocation(String baseDir, String name) {
        return String.format(DEFAULTS_LOCATION, baseDir, name);
    }
}
