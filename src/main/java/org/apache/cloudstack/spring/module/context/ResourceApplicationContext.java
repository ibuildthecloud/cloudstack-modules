package org.apache.cloudstack.spring.module.context;

import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.Resource;

public class ResourceApplicationContext extends AbstractXmlApplicationContext {

    Resource[] configResources;      
    
    public ResourceApplicationContext() {
    }
    
    public ResourceApplicationContext(Resource... configResources) {
        super();
        this.configResources = configResources;
    }

    @Override
    protected Resource[] getConfigResources() {
        return configResources;
    }

    public void setConfigResources(Resource[] configResources) {
        this.configResources = configResources;
    }
    
}
