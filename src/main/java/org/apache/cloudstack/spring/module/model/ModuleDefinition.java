package org.apache.cloudstack.spring.module.model;

import java.util.List;

import org.springframework.core.io.Resource;

public interface ModuleDefinition {
    
    String getName();
    
    String getParentName();
    
    List<Resource> getConfigLocations();
    
    List<Resource> getContextLocations();
    
    List<Resource> getInheritableContexts();
    
    boolean isValid();
    
}