/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.springboot.project;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.parser.java.facets.JavaCompilerFacet;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.AbstractProjectType;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.projects.stacks.Stack;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

public class SpringBootProjectType extends AbstractProjectType
{

   @Override
   public boolean supports(Stack stack)
   {
      return false;
   }

   @Override
   public String getType()
   {
      return "Spring Boot";
   }

   @Override
   public Iterable<Class<? extends ProjectFacet>> getRequiredFacets()
   {
      List<Class<? extends ProjectFacet>> result = new ArrayList<Class<? extends ProjectFacet>>(7);
      result.add(MetadataFacet.class);
      result.add(PackagingFacet.class);
      result.add(DependencyFacet.class);
      result.add(ResourcesFacet.class);
      result.add(WebResourcesFacet.class);
      result.add(JavaSourceFacet.class);
      result.add(JavaCompilerFacet.class);
      return result;
   }

   @Override
   public int priority()
   {
      return 100;
   }

   @Override
   public Class<? extends UIWizardStep> getSetupFlow()
   {
      return SpringBootSetupFlow.class;
   }

   @Override
   public String toString()
   {
      return "spring-boot";
   }
}
