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

package org.jboss.webbeans.xsd.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.TypeElement;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.jboss.webbeans.xsd.NamespaceHandler;

/**
 * A model of a class
 * 
 * @author Nicklas Karlsson
 * 
 */
public class ClassModel extends NamedModel
{
   // The parent (or null if top-level)
   private ClassModel parent;

   // The fields of the class
   private List<NamedModel> fields = new ArrayList<NamedModel>();
   // The methods of the class
   private List<MethodModel> methods = new ArrayList<MethodModel>();
   // The constructors of the class
   private List<MethodModel> constructors = new ArrayList<MethodModel>();

   protected ClassModel(String name)
   {
      super(name);
   }

   /**
    * Adds a field to the class model
    * 
    * @param field The field to add
    */
   public void addField(NamedModel field)
   {
      fields.add(field);
   }

   /**
    * Adds a constructor to the model
    * 
    * @param constructor The constructor to add
    */
   public void addConstructor(MethodModel constructor)
   {
      constructors.add(constructor);
   }

   /**
    * Adds a method to the model
    * 
    * @param method The method to add
    */
   public void addMethod(MethodModel method)
   {
      methods.add(method);
   }

   /**
    * Gets the parent class model of the class
    * 
    * @return The parent or null if none present
    */
   public ClassModel getParent()
   {
      return parent;
   }

   /**
    * Sets the parent
    * 
    * @param parent The new parent class model
    */
   public void setParent(ClassModel parent)
   {
      this.parent = parent;
   }

   /**
    * Gets the merged hierarchy of available constructors. Returns the
    * constructors of this class since constructors aren't inherited
    * 
    * @return The set of constructors available
    */
   public Set<MethodModel> getMergedConstructors()
   {
      return new HashSet<MethodModel>(constructors);
   }

   /**
    * Gets the public field of the class
    * 
    * @return The public fields
    */
   public List<NamedModel> getFields()
   {
      return fields;
   }

   /**
    * Gets the merged hierarchy of available fields.
    * 
    * @return The set of public fields available
    */
   public Set<NamedModel> getMergedFields()
   {
      Set<NamedModel> mergedFields = new HashSet<NamedModel>(fields);
      ClassModel currentParent = parent;
      while (currentParent != null)
      {
         mergedFields.addAll(currentParent.getFields());
         currentParent = currentParent.getParent();
      }
      return mergedFields;
   }

   /**
    * Gets the public methods of the class
    * 
    * @return The public methods
    */
   public List<MethodModel> getMethods()
   {
      return methods;
   }

   /**
    * Gets the merged hierarchy of available fields.
    * 
    * @return The set of public fields available
    */
   public Set<MethodModel> getMergedMethods()
   {
      Set<MethodModel> mergedMethods = new HashSet<MethodModel>(methods);
      ClassModel currentParent = parent;
      while (currentParent != null)
      {
         mergedMethods.addAll(currentParent.getMethods());
         currentParent = currentParent.getParent();
      }
      return mergedMethods;
   }

   /**
    * Gets the type references used in the package and its contained members
    * 
    * @return The set of types
    */
   public Set<TypedModel> getTypeReferences()
   {
      Set<TypedModel> typeReferences = new HashSet<TypedModel>();
      for (MethodModel method : getMergedMethods())
      {
         for (TypedModel parameter : method.getParameters())
         {
            if (!parameter.isPrimitive())
            {
               typeReferences.add(parameter);
            }
         }
      }
      for (MethodModel constructor : getMergedConstructors())
      {
         for (TypedModel parameter : constructor.getParameters())
         {
            if (!parameter.isPrimitive())
            {
               typeReferences.add(parameter);
            }
         }
      }
      return typeReferences;
   }

   /**
    * Gets the package of the class
    * 
    * @return The package (or "nopak" if root package)
    */
   public String getPackage()
   {
      int lastDot = name.lastIndexOf(".");
      return lastDot < 0 ? "nopak" : name.substring(0, lastDot);
   }

   /**
    * Gets the simple name of a class
    * 
    * @return The simple name
    */
   public String getSimpleName()
   {
      int lastDot = name.lastIndexOf(".");
      return lastDot < 0 ? name : name.substring(lastDot + 1);
   }

   @Override
   public String toString()
   {
      StringBuilder buffer = new StringBuilder();
      buffer.append("----------------------------------\n" + name + "\n");
      buffer.append("Constructors:\n " + getMergedConstructors() + "\n");
      buffer.append("Methods:\n" + getMergedMethods() + "\n");
      buffer.append("Fields:\n" + getMergedFields() + "\n");
      return buffer.toString();
   }

   public static ClassModel of(TypeElement typeElement)
   {
      return new ClassModel(typeElement.getQualifiedName().toString());
   }

   public Element toXSD(NamespaceHandler namespaceHandler)
   {
      Element classElement = DocumentFactory.getInstance().createElement("element");
      classElement.addAttribute("name", getSimpleName());
      for (MethodModel constructor : getMergedConstructors())
      {
         classElement.add(constructor.toXSD(namespaceHandler));
      }
      for (NamedModel field : getMergedFields())
      {
         classElement.add(field.toXSD(namespaceHandler));
      }
      for (MethodModel method : getMergedMethods())
      {
         classElement.add(method.toXSD(namespaceHandler));
      }
      return classElement;
   }

}
