package org.apache.cloudstack.spring.module.factory;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;

import org.apache.cloudstack.spring.module.locator.impl.ClasspathModuleDefinitionLocator;
import org.apache.cloudstack.spring.module.model.ModuleDefinition;
import org.apache.cloudstack.spring.module.model.ModuleDefinitionSet;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

public class ModuleBasedContextFactoryTest {

    Collection<ModuleDefinition> defs;
    
    @Before
    public void setUp() throws IOException {
        InstantiationCounter.count = 0;

        ClasspathModuleDefinitionLocator locator = new ClasspathModuleDefinitionLocator();
        defs = locator.locateModules("testhierarchy");
    }

    @Test
    public void testLoad() throws IOException {
        
        ModuleBasedContextFactory factory = new ModuleBasedContextFactory();
        
        ModuleDefinitionSet set = factory.loadModules(defs, "base");
        
        assertNotNull(set.getApplicationContext("base"));
    }
    
    @Test
    public void testBeans() throws IOException {
        ModuleBasedContextFactory factory = new ModuleBasedContextFactory();
        ModuleDefinitionSet set = factory.loadModules(defs, "base");
        
        testBeansInContext(set, "base", 1, new String[] { "base" }, new String[] { "child1", "child2", "child1-1" });
        testBeansInContext(set, "child1", 2, new String[] { "base", "child1" }, new String[] { "child2", "child1-1" });
        testBeansInContext(set, "child2", 4, new String[] { "base", "child2" }, new String[] { "child1", "child1-1" });
        testBeansInContext(set, "child1-1", 3, new String[] { "base", "child1", "child1-1" }, new String[] { "child2" });
    }
    
    protected void testBeansInContext(ModuleDefinitionSet set, String name, int order, String[] parents, String[] notTheres) {
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
        
        int count = context.getBean("count", InstantiationCounter.class).getCount();
        
        assertEquals(order, count);
    }
    
    public static class InstantiationCounter {
        public static Integer count = 0;
        
        int myCount;
        
        public InstantiationCounter() {
            synchronized (count) {
                myCount = count + 1;
                count = myCount;
            }
        }
        
        public int getCount() {
            return myCount;
        }
        
    }
}
