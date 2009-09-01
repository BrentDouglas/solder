/*
* JBoss, Home of Professional Open Source.
* Copyright 2006, Red Hat Middleware LLC, and individual contributors
* as indicated by the @author tags. See the copyright.txt file in the
* distribution for a full listing of individual contributors. 
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/ 
package org.jboss.test.webbeans.beanutils.existing;

import java.util.Map;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;

import org.jboss.test.webbeans.beanutils.AbstractBeanUtilsTest;
import org.jboss.test.webbeans.beanutils.RegisterBeansObserver;
import org.jboss.webbeans.BeanManagerImpl;
import org.jboss.webbeans.CurrentManager;
import org.jboss.webbeans.beanutils.spi.Beans;
import org.jboss.webbeans.beanutils.spi.ExistingBeanDescriber;
import org.jboss.webbeans.bootstrap.BeanDeployerEnvironment;
import org.jboss.webbeans.bootstrap.spi.BeanDeploymentArchive;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
public class ExisitingBeanDescriberTest extends AbstractBeanUtilsTest
{
   @BeforeMethod
   public void beforeMethod()
   {
      RegisterBeansObserver.clear();
   }
   
   @Test
   public void testExisitingInstanceInField() throws Exception
   {
      try
      {
         initialiseEnvironment(DefaultFieldReceiver.class);
         DefaultBean bean = new DefaultBean();
         registerBeans(bean);
         deployWebBeans();

         DefaultFieldReceiver receiver = assertBean(DefaultFieldReceiver.class);
         assert receiver.getBean() != null;
         assert receiver.getBean() == bean;
      }
      finally
      {
         undeployWebBeans();
      }
   }
   
   @Test
   public void testExistingInstanceInConstructor() throws Exception
   {
      try
      {
         initialiseEnvironment(DefaultConstructorReceiver.class);
         DefaultBean bean = new DefaultBean();
         registerBeans(bean);
         deployWebBeans();

         DefaultConstructorReceiver receiver = assertBean(DefaultConstructorReceiver.class);
         assert receiver.getBean() != null;
         assert receiver.getBean() == bean;
      }
      finally
      {
         undeployWebBeans();
      }
   }
   
   @Test
   public void testExistingInstanceFromMethodProducerInField() throws Exception
   {
      try
      {
         initialiseEnvironment(CustomDefaultFieldReceiver.class);
         MethodProducer bean = new MethodProducer();
         registerBeans(bean);
         deployWebBeans();

         CustomDefaultFieldReceiver receiver = assertBean(CustomDefaultFieldReceiver.class);
         assert receiver.getDefaultBean() != null;
         assert receiver.getDefaultBean() == bean.getDefaultBean();
         assert receiver.getCustomBean() != null;
         assert receiver.getCustomBean() == bean.getCustomBean();
      }
      finally
      {
         undeployWebBeans();
      }
   }
   
   @Test
   public void testExistingInstanceFromMethodProducerInConstructor() throws Exception
   {
      try
      {
         initialiseEnvironment(CustomDefaultConstructorReceiver.class);
         MethodProducer bean = new MethodProducer();
         registerBeans(bean);
         deployWebBeans();

         CustomDefaultConstructorReceiver receiver = assertBean(CustomDefaultConstructorReceiver.class);
         assert receiver.getDefaultBean() != null;
         assert receiver.getDefaultBean() == bean.getDefaultBean();
         assert receiver.getCustomBean() != null;
         assert receiver.getCustomBean() == bean.getCustomBean();
      }
      finally
      {
         undeployWebBeans();
      }
   }
   
   @Test
   public void testExistingInstanceFromFieldProducerInField() throws Exception
   {
      try
      {
         initialiseEnvironment(CustomDefaultFieldReceiver.class);
         FieldProducer bean = new FieldProducer();
         registerBeans(bean);
         deployWebBeans();

         CustomDefaultFieldReceiver receiver = assertBean(CustomDefaultFieldReceiver.class);
         assert receiver.getDefaultBean() != null;
         assert receiver.getDefaultBean() == bean.getDefaultBean();
         assert receiver.getCustomBean() != null;
         assert receiver.getCustomBean() == bean.getCustomBean();
      }
      finally
      {
         undeployWebBeans();
      }
   }
   
   @Test
   public void testExistingInstanceFromFieldProducerInConstructor() throws Exception
   {
      try
      {
         initialiseEnvironment(CustomDefaultConstructorReceiver.class);
         FieldProducer bean = new FieldProducer();
         registerBeans(bean);
         deployWebBeans();

         CustomDefaultConstructorReceiver receiver = assertBean(CustomDefaultConstructorReceiver.class);
         assert receiver.getDefaultBean() != null;
         assert receiver.getDefaultBean() == bean.getDefaultBean();
         assert receiver.getCustomBean() != null;
         assert receiver.getCustomBean() == bean.getCustomBean();
      }
      finally
      {
         undeployWebBeans();
      }
   }
   
   private <T> void registerBeans(T instance) throws Exception
   {
      AnnotatedType<T> type = getCurrentManager().createAnnotatedType((Class<T>)instance.getClass());
      Beans<T> beans = ExistingBeanDescriber.describePreinstantiatedBean(type, getBeanDeployerEnvironment(), getCurrentManager(), instance);
      RegisterBeansObserver.addBeans(beans);
   }
   
   private <T> T assertBean(Class<T> type) throws Exception
   {
      Set<Bean<?>> beans = getCurrentManager().getBeans(type);
      assert beans.size() == 1;
      Bean<T> bean = (Bean<T>)beans.iterator().next();
      CreationalContext<T> context = getCurrentManager().createCreationalContext(null);
      T t = bean.create(context);
      return t;
   }
}
