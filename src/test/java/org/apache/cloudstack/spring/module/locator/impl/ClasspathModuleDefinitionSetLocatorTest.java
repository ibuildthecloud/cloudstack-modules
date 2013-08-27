package org.apache.cloudstack.spring.module.locator.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;

import org.apache.cloudstack.spring.module.model.ModuleDefinition;
import org.junit.Test;

public class ClasspathModuleDefinitionSetLocatorTest {
    
    @Test
    public void testDiscover() throws IOException {
        ClasspathModuleDefinitionLocator factory = new ClasspathModuleDefinitionLocator();
        
        Collection<ModuleDefinition> modules = factory.locateModules("testhierarchy");
        
        assertEquals(5, modules.size());
    }

}
