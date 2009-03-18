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

package org.jboss.webbeans.test;

import org.jboss.testharness.AbstractTest;
import org.jboss.webbeans.CurrentManager;
import org.jboss.webbeans.ManagerImpl;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

/**
 * Some basic abstractions useful for tests in this project.
 * 
 * @author David Allen
 *
 */
public abstract class AbstractLogTest extends AbstractTest
{
   private ManagerImpl manager;

   @Override
   @BeforeSuite
   public void beforeSuite(ITestContext context) throws Exception
   {
      if (!isInContainer())
      {
         getCurrentConfiguration().setStandaloneContainers(new StandaloneContainersImpl());
         getCurrentConfiguration().getExtraPackages().add(AbstractLogTest.class.getPackage().getName());
      }
      super.beforeSuite(context);
   }

   @BeforeMethod
   public void before() throws Exception
   {
      this.manager = CurrentManager.rootManager();
   }
   
   @AfterMethod
   public void after() throws Exception
   {
      manager = null;
   }
   
   public ManagerImpl getCurrentManager()
   {
      return manager;
   }
}
