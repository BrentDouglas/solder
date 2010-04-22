/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.weld.extensions.beanManager;

import javax.enterprise.inject.spi.BeanManager;

/**
 * Provider for obtaining a BeanManager
 * 
 * @author Nicklas Karlsson
 * 
 */
public interface BeanManagerProvider
{
   /**
    * Try to obtain a BeanManager
    * 
    * @return The BeanManager (or null if non found at this location)
    */
   public abstract BeanManager getBeanManager();

   /**
    * An integer precedence value that indicates how favorable the implementation
    * considers itself amongst alternatives. A higher value is a higher
    * precedence.
    */
   public abstract int getPrecedence();
}
