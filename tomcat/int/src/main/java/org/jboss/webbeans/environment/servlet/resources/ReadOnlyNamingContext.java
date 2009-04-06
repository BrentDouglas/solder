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
package org.jboss.webbeans.environment.servlet.resources;

import javax.inject.DeploymentException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.webbeans.resources.spi.helpers.AbstractNamingContext;

/**
 * 
 * @author Pete Muir
 */
public class ReadOnlyNamingContext extends AbstractNamingContext
{
   
   private final Context context;
   
   public ReadOnlyNamingContext()
   {
      try
      {
         context = new InitialContext();
      }
      catch (NamingException e)
      {
         throw new DeploymentException("Error creating initial context");
      }
   }
   
   public void bind(String name, Object value)
   {
      // No-op
   }

   @Override
   public Context getContext()
   {
      return context;
   }
   
   @Override
   public void unbind(String key)
   {
      // No-op
   }
   
}
