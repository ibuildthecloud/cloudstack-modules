package org.apache.cloudstack.spring.module.model.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.cloudstack.spring.module.context.ResourceApplicationContext;
import org.apache.cloudstack.spring.module.model.ModuleDefinition;
import org.apache.cloudstack.spring.module.model.ModuleDefinitionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

public class DefaultModuleDefinitionSet implements ModuleDefinitionSet {

    private static final Logger log = LoggerFactory.getLogger(DefaultModuleDefinitionSet.class);
    
    public static final String DEFAULT_CONFIG_RESOURCES = "DefaultConfigResources";
    public static final String DEFAULT_CONFIG_XML = "defaults-context.xml";
    
    String root;
    Map<String, ModuleDefinition> modules;
    Map<String, ApplicationContext> contexts = new HashMap<String, ApplicationContext>();
    ApplicationContext rootContext = null;

    public DefaultModuleDefinitionSet(Map<String, ModuleDefinition> modules, String root) {
        super();
        this.root = root;
        this.modules = modules;
    }

    public void load() throws IOException {
        if ( ! loadRootContext() )
            return;
        
        printHierarchy();
        loadContexts();
    }
    
    protected boolean loadRootContext() {
        ModuleDefinition def = modules.get(root);
        
        if ( def == null )
            return false;
        
        ApplicationContext defaultsContext = getDefaultsContext();
        
        rootContext = loadContext(def, defaultsContext);
        
        return true;
    }
    
    protected void loadContexts() {
        withModule(new WithModule() {
            public void with(ModuleDefinition def, Stack<ModuleDefinition> parents) {
                try {
                    ApplicationContext parent = getApplicationContext(parents.peek().getName());
                    loadContext(def, parent);
                } catch ( EmptyStackException e ) {
                    // The root context is already loaded, so ignore the exception
                }
            }
        });
    }
    
    protected ApplicationContext loadContext(ModuleDefinition def, ApplicationContext parent) {
        ResourceApplicationContext context = new ResourceApplicationContext();
        
        Resource[] resources = getConfigResources(def.getName());
        context.setConfigResources(resources);
        context.setParent(parent);

        log.info("Loading module context [{}]", def.getName());
        context.refresh();
        
        contexts.put(def.getName(), context);
        
        return context;
    }
    
    protected boolean shouldLoad(ModuleDefinition def) {
        return true;
    }
    
    protected ApplicationContext getDefaultsContext() {
        URL config = DefaultModuleDefinitionSet.class.getResource(DEFAULT_CONFIG_XML);
        
        ResourceApplicationContext context = new ResourceApplicationContext(new UrlResource(config));
        context.refresh();
        
        @SuppressWarnings("unchecked")
        final List<Resource> resources = (List<Resource>) context.getBean(DEFAULT_CONFIG_RESOURCES);
        
        withModule(new WithModule() {
            public void with(ModuleDefinition def, Stack<ModuleDefinition> parents) {
                for ( Resource defaults : def.getConfigLocations() ) {
                    resources.add(defaults);
                }
            }
        });
        
        return context;
    }
    
    protected void printHierarchy() {
        withModule(new WithModule() {
            public void with(ModuleDefinition def, Stack<ModuleDefinition> parents) {
                log.info(String.format("Module Hierarchy:%" + ((parents.size() * 2) + 1) + "s%s", "", def.getName()));
            }
        });
    }
    
    protected void withModule(WithModule with) {
        ModuleDefinition rootDef = modules.get(root);
        withModule(rootDef, new Stack<ModuleDefinition>(), with);
    }
    
    protected void withModule(ModuleDefinition def, Stack<ModuleDefinition> parents, WithModule with) {
        if ( def == null )
            return;
        
        if ( ! shouldLoad(def) ) {
            return;
        }
        
        with.with(def, parents);
        
        parents.push(def);
        
        for ( ModuleDefinition child : def.getChildren() ) {
            withModule(child, parents, with);
        }
        
        parents.pop();
    }
    
    private static interface WithModule {
        public void with(ModuleDefinition def, Stack<ModuleDefinition> parents);
    }
    
    @Configuration
    public static class ConfigContext {
        
        List<Resource> resources;
        
        public ConfigContext(List<Resource> resources) {
            super();
            this.resources = resources;
        }

        @Bean(name = DEFAULT_CONFIG_RESOURCES)
        public List<Resource> defaultConfigResources() {
            return new ArrayList<Resource>();
        }
    }

    public ApplicationContext getApplicationContext(String name) {
        return contexts.get(name);
    }

    public Resource[] getConfigResources(String name) {
        List<Resource> resources = new ArrayList<Resource>();
        
        ModuleDefinition def = modules.get(name);
        
        if ( def == null )
            return new Resource[] {};
        
        resources.addAll(def.getContextLocations());
        
        while ( def != null ) {
            resources.addAll(def.getInheritableContextLocations());
            def = modules.get(def.getParentName());
        }
        
        return resources.toArray(new Resource[resources.size()]);
    }
}
