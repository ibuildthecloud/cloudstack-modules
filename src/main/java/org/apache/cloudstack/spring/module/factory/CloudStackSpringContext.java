package org.apache.cloudstack.spring.module.factory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.cloudstack.spring.module.locator.ModuleDefinitionLocator;
import org.apache.cloudstack.spring.module.locator.impl.ClasspathModuleDefinitionLocator;
import org.apache.cloudstack.spring.module.model.ModuleDefinition;
import org.apache.cloudstack.spring.module.model.ModuleDefinitionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;

public class CloudStackSpringContext {

    private static final Logger log = LoggerFactory.getLogger(CloudStackSpringContext.class);
    
    public static final String CLOUDSTACK_CONTEXT = "META-INF/cloudstack";
    public static final String CLOUDSTACK_BASE = "bootstrap";
    
    ModuleBasedContextFactory factory = new ModuleBasedContextFactory();
    ModuleDefinitionLocator loader = new ClasspathModuleDefinitionLocator();
    ModuleDefinitionSet moduleDefinitionSet;
    String baseName;
    String contextName;
    
    public CloudStackSpringContext(String context, String base) throws IOException {
        this.baseName = base;
        this.contextName = context;
        
        factory = new ModuleBasedContextFactory();
        loader = new ClasspathModuleDefinitionLocator();
        init();
    }
    
    public CloudStackSpringContext() throws IOException {
        this(CLOUDSTACK_CONTEXT, CLOUDSTACK_BASE);
    }
    
    public void init() throws IOException {
        Collection<ModuleDefinition> defs = loader.locateModules(contextName);
        
        if ( defs.size() == 0 )
            throw new RuntimeException("No modules found to load for Spring");
        
        moduleDefinitionSet = factory.loadModules(defs, baseName);
    }
    
    public void registerShutdownHook() {
        ApplicationContext base = moduleDefinitionSet.getApplicationContext(baseName);
        
        if ( base instanceof ConfigurableApplicationContext ) {
            ((ConfigurableApplicationContext)base).registerShutdownHook();
        }
    }
    
    public ModuleDefinition getModuleDefinitionForWeb(String name) {
        ModuleDefinition def = moduleDefinitionSet.getModuleDefinition(name);
        
        if ( def != null ) {
            return def;
        }
        
        /* Grab farthest descendant that is deterministic */
        def = moduleDefinitionSet.getModuleDefinition(baseName);
        
        if ( def == null ) {
            throw new RuntimeException("Failed to find base spring module to extend for web");
        }
        
        while ( def.getChildren().size() == 1 ) {
            def = def.getChildren().iterator().next();
        }
        
        return def;
    }
    
    public ApplicationContext getApplicationContextForWeb(String name) {
        ModuleDefinition def = getModuleDefinitionForWeb(name);
        
        return moduleDefinitionSet.getApplicationContext(def.getName());
    }
    
    public String[] getConfigLocationsForWeb(String name, String[] configured) {
        if ( configured == null )
            configured = new String[] {};
        
        ModuleDefinition def = getModuleDefinitionForWeb(name);
        
        List<Resource> inherited = new ArrayList<Resource>();
        
        while ( def != null ) {
            inherited.addAll(def.getInheritableContextLocations());
            def = moduleDefinitionSet.getModuleDefinition(def.getParentName());
        }

        List<String> urlList = new ArrayList<String>();
        
        for ( Resource r : inherited ) {
            try {
                String urlString = r.getURL().toExternalForm();
                urlList.add(urlString);
            } catch (IOException e) {
                log.error("Failed to create URL for {}", r.getDescription(), e);
            }
        }
        
        String[] result = new String[urlList.size() + configured.length];
        result = urlList.toArray(result);
        
        System.arraycopy(configured, 0, result, urlList.size(), configured.length);
        
        return result;
    }
}
