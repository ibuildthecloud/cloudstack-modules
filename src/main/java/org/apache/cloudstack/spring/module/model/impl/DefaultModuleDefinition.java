package org.apache.cloudstack.spring.module.model.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.cloudstack.spring.module.model.ModuleDefinition;
import org.apache.cloudstack.spring.module.util.ModuleLocationUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;

public class DefaultModuleDefinition implements ModuleDefinition {

    public static final String NAME = "name";
    public static final String PARENT = "parent";
        
    String name;
    String baseDir;
    String parent;
    Resource moduleProperties;
    ResourcePatternResolver resolver;
    boolean valid;
    
    List<Resource> configLocations;
    List<Resource> contextLocations;
    List<Resource> inheritableContextLocations;
    Map<String, ModuleDefinition> children = new TreeMap<String, ModuleDefinition>();
    
    public DefaultModuleDefinition(String baseDir, Resource moduleProperties, ResourcePatternResolver resolver) {
        this.baseDir = baseDir;
        this.resolver = resolver;
        this.moduleProperties = moduleProperties;
    }
    
    public void init() throws IOException {
        
        if ( ! moduleProperties.exists() ) {
            return;
        }
        
        resolveNameAndParent();
        
        contextLocations = Arrays.asList(resolver.getResources(ModuleLocationUtils.getContextLocation(baseDir, name)));
        configLocations = Arrays.asList(resolver.getResources(ModuleLocationUtils.getDefaultsLocation(baseDir, name)));
        inheritableContextLocations = Arrays.asList(resolver.getResources(ModuleLocationUtils.getInheritableContextLocation(baseDir, name)));

        if ( contextLocations.size() > 0 )
            valid = true;
    }
    
    protected void resolveNameAndParent() throws IOException {
        InputStream is = null;
        
        try {
            is = moduleProperties.getInputStream();
            Properties props = new Properties();
            props.load(is);
            
            name = props.getProperty(NAME);
            parent = props.getProperty(PARENT);
            
            if ( ! StringUtils.hasText(name) ) {
                throw new IOException("Missing name property in [" + location() + "]");
            }
            
            if ( ! StringUtils.hasText(parent) ) {
                parent = null;
            }
            
            checkNameMatchesSelf();
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
    
    protected void checkNameMatchesSelf() throws IOException {
        String expectedLocation = ModuleLocationUtils.getModuleLocation(baseDir, name);
        Resource self = resolver.getResource(expectedLocation);
        
        if ( ! self.exists() ) {
            throw new IOException("Resource [" + location() + "] is expected to exist at [" +
                    expectedLocation + "] please ensure the name property is correct");
        }
        
        String moduleUrl = moduleProperties.getURL().toExternalForm();
        String selfUrl = self.getURL().toExternalForm();
            
        if ( ! moduleUrl.equals(selfUrl) ) {
            throw new IOException("Resource [" + location() + "] and [" +
                    self.getURL() + "] do not appear to be the same resource, " + 
                    "please ensure the name property is correct");
        }
    }
    
    private String location() throws IOException {
        return moduleProperties.getURL().toString();
    }
    
    public void addChild(ModuleDefinition def) {
        children.put(def.getName(), def);
    }
    
    public Collection<ModuleDefinition> getChildren() {
        return children.values();
    }

    public String getName() {
        return name;
    }

    public String getParentName() {
        return parent;
    }

    public List<Resource> getConfigLocations() {
        return configLocations;
    }

    public List<Resource> getContextLocations() {
        return contextLocations;
    }

    public List<Resource> getInheritableContextLocations() {
        return inheritableContextLocations;
    }

    public boolean isValid() {
        return valid;
    }

}