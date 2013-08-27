package org.apache.cloudstack.spring.module.model;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

public interface ModuleDefinitionSet {

    ModuleDefinition getModuleDefinition(String name);
    
    ApplicationContext getApplicationContext(String name);
    
    Resource[] getConfigResources(String name);
    
}
