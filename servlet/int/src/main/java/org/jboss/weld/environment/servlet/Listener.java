/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.environment.servlet;

import java.util.logging.Logger;

import org.jboss.weld.environment.servlet.deployment.ServletDeployment;
import org.jboss.weld.environment.servlet.services.ServletResourceInjectionServices;
import org.jboss.weld.environment.servlet.services.ServletServicesImpl;
import org.jboss.weld.environment.servlet.util.Reflections;
import org.jboss.weld.environment.tomcat.WeldAnnotationProcessor;

/**
 * @author Pete Muir
 */
public class Listener extends ForwardingServletListener
{
   private static final Logger log = LoggerFactory.getLogger(Listener.class);
   
   private static final String BOOTSTRAP_IMPL_CLASS_NAME = "org.jboss.weld.bootstrap.WeldBootstrap";
   private static final String WELD_LISTENER_CLASS_NAME = "org.jboss.weld.servlet.WeldListener";
   private static final String APPLICATION_BEAN_STORE_ATTRIBUTE_NAME = Listener.class.getName() + ".applicationBeanStore";
   private static final String EXPRESSION_FACTORY_NAME = "org.jboss.weld.el.ExpressionFactory";
   
   private final transient Bootstrap bootstrap;
   private final transient ServletListener weldListener;
   private WeldManager manager;
   
   public Listener()
   {
      try
      {
         bootstrap = Reflections.newInstance(BOOTSTRAP_IMPL_CLASS_NAME);
      }
      catch (IllegalArgumentException e)
      {
         throw new IllegalStateException("Error loading Weld bootstrap, check that Weld is on the classpath", e);
      }
      try
      {
         weldListener = Reflections.newInstance(WELD_LISTENER_CLASS_NAME);
      }
      catch (IllegalArgumentException e)
      {
         throw new IllegalStateException("Error loading Weld listener, check that Weld is on the classpath", e);
      }
   }

   @Override
   public void contextDestroyed(ServletContextEvent sce)
   {
      bootstrap.shutdown();
      try
      {
         Reflections.classForName("org.apache.AnnotationProcessor");
         sce.getServletContext().removeAttribute(WeldAnnotationProcessor.class.getName());
      }
      catch (IllegalArgumentException e) {}
      super.contextDestroyed(sce);
   }

   @Override
   public void contextInitialized(ServletContextEvent sce)
   {
      BeanStore applicationBeanStore = new ConcurrentHashMapBeanStore();
      sce.getServletContext().setAttribute(APPLICATION_BEAN_STORE_ATTRIBUTE_NAME, applicationBeanStore);
      
      ServletDeployment deployment = new ServletDeployment(sce.getServletContext());
      try
      {
    	  deployment.getWebAppBeanDeploymentArchive().getServices().add(
    	        ResourceInjectionServices.class, new ServletResourceInjectionServices() {});
      }
      catch (NoClassDefFoundError e)
      {
    	 // Support GAE
    	 log.warn("@Resource injection not available in simple beans");
      }
      
      deployment.getServices().add(ServletServices.class,
            new ServletServicesImpl(deployment.getWebAppBeanDeploymentArchive()));
      
      bootstrap.startContainer(Environments.SERVLET, deployment, applicationBeanStore).startInitialization();
      manager = bootstrap.getManager(deployment.getWebAppBeanDeploymentArchive());
      
      boolean tomcat = true;
      try
      {
         Reflections.classForName("org.apache.AnnotationProcessor");
      }
      catch (IllegalArgumentException e)
      {
         log.info("JSR-299 injection will not be available in Servlets, Filters etc. This facility is only available in Tomcat");
         tomcat = false;
      }
      
      if (tomcat)
      {
         // Try pushing a Tomcat AnnotationProcessor into the servlet context
         try
         {
            Class<?> clazz = Reflections.classForName(WeldAnnotationProcessor.class.getName());
            Object annotationProcessor = clazz.getConstructor(WeldManager.class).newInstance(manager);
            sce.getServletContext().setAttribute(WeldAnnotationProcessor.class.getName(), annotationProcessor);
         }
         catch (Exception e)
         {
            log.error("Unable to create Tomcat AnnotationProcessor. JSR-299 injection will not be available in Servlets, Filters etc.", e);
         }
      }

      // Push the manager into the servlet context so we can access in JSF
      
      if (JspFactory.getDefaultFactory() != null)
      {
         JspApplicationContext jspApplicationContext = JspFactory.getDefaultFactory().getJspApplicationContext(sce.getServletContext());
         
         // Register the ELResolver with JSP
         jspApplicationContext.addELResolver(manager.getELResolver());
         
         // Register ELContextListener with JSP
         jspApplicationContext.addELContextListener(Reflections.<ELContextListener>
            newInstance("org.jboss.weld.el.WeldELContextListener"));
         
         // Push the wrapped expression factory into the servlet context so that Tomcat or Jetty can hook it in using a container code
         sce.getServletContext().setAttribute(EXPRESSION_FACTORY_NAME,
               manager.wrapExpressionFactory(jspApplicationContext.getExpressionFactory()));
      }
      
      bootstrap.deployBeans().validateBeans().endInitialization();
      super.contextInitialized(sce);
   }
   
   @Override
   protected ServletListener delegate()
   {
      return weldListener;
   }
   
}
