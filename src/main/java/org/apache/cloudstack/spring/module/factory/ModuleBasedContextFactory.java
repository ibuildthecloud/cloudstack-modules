package org.apache.cloudstack.spring.module.factory;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.cloudstack.spring.module.model.ModuleDefinition;
import org.apache.cloudstack.spring.module.model.ModuleDefinitionSet;
import org.apache.cloudstack.spring.module.model.impl.DefaultModuleDefinitionSet;

public class ModuleBasedContextFactory {

    public ModuleDefinitionSet loadModules(Collection<ModuleDefinition> defs, String root) throws IOException {
        
        Map<String, ModuleDefinition> modules = wireUpModules(root, defs);
        
        DefaultModuleDefinitionSet moduleSet = new DefaultModuleDefinitionSet(modules, root);
        moduleSet.load();
        
        return moduleSet;
    }
    
    protected Map<String, ModuleDefinition> wireUpModules(String root, Collection<ModuleDefinition> defs) throws IOException {
        Map<String, ModuleDefinition> modules = new HashMap<String, ModuleDefinition>();
        
        for ( ModuleDefinition def : defs ) {
            modules.put(def.getName(), def);
        }
        
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
        
        if ( result.containsKey(base.getName()) ) {
            throw new RuntimeException("Circular dependency to [" + base.getName() + "] from current set " +
                    result.keySet());
        }
        
        result.put(base.getName(), base);
        
        for ( ModuleDefinition childDef : base.getChildren() )
            traverse(childDef, result);
        
        return result;
    }
}