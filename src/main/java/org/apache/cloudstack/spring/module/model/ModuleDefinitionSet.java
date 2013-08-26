package org.apache.cloudstack.spring.module.model;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

public interface ModuleDefinitionSet {

    ApplicationContext getApplicationContext(String name);
    
    Resource[] getConfigResources(String name);
    
}
