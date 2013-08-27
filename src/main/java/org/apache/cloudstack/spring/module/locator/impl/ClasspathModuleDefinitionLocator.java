package org.apache.cloudstack.spring.module.locator.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.cloudstack.spring.module.locator.ModuleDefinitionLocator;
import org.apache.cloudstack.spring.module.model.ModuleDefinition;
import org.apache.cloudstack.spring.module.model.impl.DefaultModuleDefinition;
import org.apache.cloudstack.spring.module.util.ModuleLocationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class ClasspathModuleDefinitionLocator implements ModuleDefinitionLocator {
    
    protected ResourcePatternResolver getResolver() {
        return new PathMatchingResourcePatternResolver();
    }
    
    public Collection<ModuleDefinition> locateModules(String context) throws IOException {
        ResourcePatternResolver resolver = getResolver();
        
        Map<String, ModuleDefinition> allModules = discoverModules(context, resolver);
        
        return allModules.values();
    }
    
    protected Map<String, ModuleDefinition> discoverModules(String baseDir, ResourcePatternResolver resolver) throws IOException {
        Map<String, ModuleDefinition> result = new HashMap<String, ModuleDefinition>();
        
        for ( Resource r : resolver.getResources(ModuleLocationUtils.getModulesLocation(baseDir)) ) {
            DefaultModuleDefinition def = new DefaultModuleDefinition(baseDir, r, resolver);
            def.init();
            
            if ( def.isValid() )
                result.put(def.getName(), def);
        }
        
        return result;
    }
    
}
