package org.apache.cloudstack.spring.module.factory;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Map;

import org.apache.cloudstack.spring.module.model.ModuleDefinition;
import org.apache.cloudstack.spring.module.model.ModuleDefinitionSet;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class ModuleBasedContextFactoryTest {

    @Test
    public void testDiscover() throws IOException {
        ModuleBasedContextFactory factory = new ModuleBasedContextFactory();
        
        Map<String, ModuleDefinition> modules = factory.discoverModules("testhierarchy", 
                new PathMatchingResourcePatternResolver());
        
        assertEquals(5, modules.size());
    }

    @Test
    public void testGetModules() throws IOException {
        ModuleBasedContextFactory factory = new ModuleBasedContextFactory();
        
        Map<String, ModuleDefinition> modules = factory.getModules("testhierarchy", "base");
        
        assertEquals(4, modules.size());
    }
    
    @Test
    public void testLoad() throws IOException {
        ModuleBasedContextFactory factory = new ModuleBasedContextFactory();
        
        ModuleDefinitionSet set = factory.loadModules("testhierarchy", "base");
        
        assertNotNull(set.getApplicationContext("base"));
    }
    
    @Test
    public void testBeans() throws IOException {
        ModuleBasedContextFactory factory = new ModuleBasedContextFactory();
        
        ModuleDefinitionSet set = factory.loadModules("testhierarchy", "base");
        
        testBeansInContext(set, "base", new String[] { "base" }, new String[] { "child1", "child2", "child1-1" });
        testBeansInContext(set, "child1", new String[] { "base", "child1" }, new String[] { "child2", "child1-1" });
        testBeansInContext(set, "child2", new String[] { "base", "child2" }, new String[] { "child1", "child1-1" });
        testBeansInContext(set, "child1-1", new String[] { "base", "child1", "child1-1" }, new String[] { "child2" });
    }
    
    protected void testBeansInContext(ModuleDefinitionSet set, String name, String[] parents, String[] notTheres) {
        ApplicationContext context = set.getApplicationContext(name);
        
        String nameBean = context.getBean("name", String.class);
        assertEquals(name, nameBean);
        
        for ( String parent : parents ) {
            String parentBean = context.getBean(parent, String.class);
            assertEquals(parent, parentBean);
        }
        
        for ( String notThere : notTheres ) {
            try {
                context.getBean(notThere, String.class);
                fail();
            } catch ( NoSuchBeanDefinitionException e ) {
            }
        }
    }
}
