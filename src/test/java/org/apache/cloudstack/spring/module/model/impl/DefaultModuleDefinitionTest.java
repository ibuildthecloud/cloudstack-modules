package org.apache.cloudstack.spring.module.model.impl;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.cloudstack.spring.module.util.ModulePathUtils;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class DefaultModuleDefinitionTest {

    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    
    protected DefaultModuleDefinition createDef(String name) {
        Resource resource = 
                resolver.getResource(ModulePathUtils.getModuleLocation("testfiles", name));
        
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
    
    
}
