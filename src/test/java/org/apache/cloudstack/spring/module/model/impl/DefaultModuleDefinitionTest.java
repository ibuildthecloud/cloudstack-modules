package org.apache.cloudstack.spring.module.model.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.cloudstack.spring.module.util.ModuleLocationUtils;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class DefaultModuleDefinitionTest {

    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    
    protected DefaultModuleDefinition createDef(String name) {
        Resource resource = 
                resolver.getResource(ModuleLocationUtils.getModuleLocation("testfiles", name));
        
        return new DefaultModuleDefinition("testfiles", resource, resolver);
    }
    
    @Test
    public void testBlankName() {
        DefaultModuleDefinition def = createDef("blankname");
        
        try {
            def.init();
            fail();
        } catch ( IOException e ) {
            assertTrue(e.getMessage().contains("Missing name property"));
        }
        
    }
    
    @Test
    public void testMissingName() {
        DefaultModuleDefinition def = createDef("missingname");
        
        try {
            def.init();
            fail();
        } catch ( IOException e ) {
            assertTrue(e.getMessage().contains("Missing name property"));
        }
        
    }
    
    @Test
    public void testBadName() {
        DefaultModuleDefinition def = createDef("badname");
        
        try {
            def.init();
            fail();
        } catch ( IOException e ) {
            assertTrue(e.getMessage().contains("is expected to exist at"));
        }
    }

    @Test
    public void testGood() throws IOException {
        DefaultModuleDefinition def = createDef("good");
        def.init();
        assertTrue(def.isValid());
    }
    
    @Test
    public void testWrongName() {
        DefaultModuleDefinition def = createDef("wrongname");
        
        try {
            def.init();
            fail();
        } catch ( IOException e ) {
            assertTrue(e.getMessage().contains("do not appear to be the same resource"));
        }
    }
    
    @Test
    public void testAllFiles() throws IOException {
        DefaultModuleDefinition def = createDef("all");
        
        def.init();
        
        assertEquals(2, def.getContextLocations().size());
        has(def.getContextLocations(), "empty-context.xml", "empty2-context.xml");
        
        assertEquals(2, def.getConfigLocations().size());
        has(def.getConfigLocations(), "test2-defaults.properties", "defaults.properties");
        
        assertEquals(2, def.getInheritableContextLocations().size());
        has(def.getInheritableContextLocations(), "empty-context-inheritable.xml", "empty2-context-inheritable.xml");
    }
    
    protected void has(List<Resource> resources, String... files) throws IOException {
        int count = 0;
        
        for ( Resource r : resources ) {
            for ( String file : files ) {
                if ( r.getURL().toExternalForm().contains(file) ) {
                    count++;
                    break;
                }
            }
        }
        
        assertEquals(resources + " does not contain " + Arrays.toString(files), files.length, count);
    }
}
