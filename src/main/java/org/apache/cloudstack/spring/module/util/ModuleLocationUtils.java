package org.apache.cloudstack.spring.module.util;

public class ModuleLocationUtils {

    private static final String ALL_MODULE_PROPERTIES = "classpath*:%s/*/module.properties";
    private static final String MODULE_PROPERTIES = "classpath:%s/%s/module.properties";
    private static final String CONTEXT_LOCATION = "classpath*:%s/%s/*context.xml";
    private static final String INHERTIABLE_CONTEXT_LOCATION = "classpath*:%s/%s/*context-inheritable.xml";
    private static final String DEFAULTS_LOCATION = "classpath*:%s/%s/*defaults.properties";

    public static String getModulesLocation(String baseDir) {
        return String.format(ALL_MODULE_PROPERTIES, baseDir);
    }
    
    public static String getModuleLocation(String baseDir, String name) {
        return String.format(MODULE_PROPERTIES, baseDir, name);
    }
    
    public static String getContextLocation(String baseDir, String name) {
        return String.format(CONTEXT_LOCATION, baseDir, name);
    }
    
    public static String getInheritableContextLocation(String baseDir, String name) {
        return String.format(INHERTIABLE_CONTEXT_LOCATION, baseDir, name);
    }
    
    public static String getDefaultsLocation(String baseDir, String name) {
        return String.format(DEFAULTS_LOCATION, baseDir, name);
    }
}
