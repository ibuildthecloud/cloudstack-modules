package org.apache.cloudstack.spring.module.web;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.cloudstack.spring.module.factory.CloudStackSpringContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoaderListener;

public class CloudStackContextLoaderListener extends ContextLoaderListener {

    public static final String WEB_PARENT_MODULE = "parentModule";
    public static final String WEB_PARENT_MODULE_DEFAULT = "web";
    
    CloudStackSpringContext cloudStackContext;
    String configuredParentName;
    
    @Override
    protected ApplicationContext loadParentContext(ServletContext servletContext) {
        return cloudStackContext.getApplicationContextForWeb(configuredParentName);
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            cloudStackContext = new CloudStackSpringContext();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize CloudStack Spring modules", e);
        }
        
        configuredParentName = event.getServletContext().getInitParameter(WEB_PARENT_MODULE);
        if ( configuredParentName == null ) {
            configuredParentName = WEB_PARENT_MODULE_DEFAULT;
        }
        
        super.contextInitialized(event);
    }

    @Override
    protected void customizeContext(ServletContext servletContext, ConfigurableWebApplicationContext applicationContext) {
        super.customizeContext(servletContext, applicationContext);
        
        String[] newLocations = cloudStackContext.getConfigLocationsForWeb(configuredParentName, 
                applicationContext.getConfigLocations());
        
        applicationContext.setConfigLocations(newLocations);
    }
   
}
