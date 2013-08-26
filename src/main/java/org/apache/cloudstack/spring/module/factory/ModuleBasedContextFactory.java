package org.apache.cloudstack.spring.module.factory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.cloudstack.spring.module.model.ModuleDefinition;
import org.apache.cloudstack.spring.module.model.ModuleDefinitionSet;
import org.apache.cloudstack.spring.module.model.impl.DefaultModuleDefinition;
import org.apache.cloudstack.spring.module.model.impl.DefaultModuleDefinitionSet;
import org.apache.cloudstack.spring.module.util.ModuleLocationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class ModuleBasedContextFactory {

    public ModuleDefinitionSet loadModules(String baseName, String root) throws IOException {
        
        Map<String, ModuleDefinition> modules = getModules(baseName, root);
        
        DefaultModuleDefinitionSet moduleSet = new DefaultModuleDefinitionSet(modules, root);
        moduleSet.load();
        
        return moduleSet;
    }
    
    protected ResourcePatternResolver getResolver() {
        return new PathMatchingResourcePatternResolver();
    }
    
    protected Map<String, ModuleDefinition> getModules(String baseName, String root) throws IOException {
        ResourcePatternResolver resolver = getResolver();
        
        Map<String, ModuleDefinition> allModules = discoverModules(baseName, resolver);
        
        return wireUpModules(root, allModules);
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
    
    protected Map<String, ModuleDefinition> wireUpModules(String root, Map<String, ModuleDefinition> modules) throws IOException {
        ModuleDefinition rootDef = null;
        Map<String, ModuleDefinition> result = new HashMap<String, ModuleDefinition>();
        
        for ( ModuleDefinition def : modules.values() ) {
            if ( def.getName().equals(root) ) {
                rootDef = def;
            }
            
            if ( def.getParentName() != null ) {
                ModuleDefinition parentDef = modules.get(def.getParentName());
                
                if ( parentDef != null )
                    parentDef.addChild(def);
            }
        }
        
        return traverse(rootDef, result);
    }
    
    protected Map<String, ModuleDefinition> traverse(ModuleDefinition base, Map<String, ModuleDefinition> result) {
        if ( base == null )
            return result;
        
        result.put(base.getName(), base);
        
        for ( ModuleDefinition childDef : base.getChildren() )
            traverse(childDef, result);
        
        return result;
    }
}