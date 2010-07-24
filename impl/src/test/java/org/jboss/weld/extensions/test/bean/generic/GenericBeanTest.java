/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.extensions.test.bean.generic;

import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class GenericBeanTest
{
   @Deployment
   public static Archive<?> deploy()
   {
      return ShrinkWrap.create("test.jar", JavaArchive.class).addPackage(GenericBeanTest.class.getPackage());
   }

   @Inject
   @Grault(1)
   private Baz baz1;

   @Inject
   @Grault(2)
   private Baz baz2;

   @Test
   public void testGeneric()
   {
      // Test that the generic configuration injection wiring is working!
      assert baz1.getGarply() != null;
      assert baz1.getGarply().equals("hello1");
      assert baz2.getGarply() != null;
      assert baz2.getGarply().equals("hello2");
      assert baz1.getBar() != null;
      assert baz1.getBar().getInjectedGarply() != null;
      assert baz1.getBar().getInjectedGarply().value().equals("hello1");
      assert baz2.getBar() != null;
      assert baz2.getBar().getInjectedGarply() != null;
      assert baz2.getBar().getInjectedGarply().value().equals("hello2");
      // Check that this isn't affecting annotations on the generic bean without @Inject 
      assert baz1.getBar().getGarply() == null;
      assert baz2.getBar().getGarply() == null;
      // Check that normal bean injection is working correctly!
      assert baz2.getCorge() != null;
      assert baz2.getCorge().getName() == "fred";
   }
}
