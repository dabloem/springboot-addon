/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.springboot.commands.ui;

import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.text.Inflector;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Creates a new REST Endpoint.
 *
 * @author <a href="cmoulliard@redhat.com">Charles Moulliard</a>
 */
public class RestNewEndpointCommand extends AbstractRestNewCommand<JavaClassSource>
{
   @Inject
   @WithAttributes(label = "Methods", description = "REST methods to be defined", defaultValue = "GET")
   private UISelectMany<RestMethod> methods;

   @Inject
   @WithAttributes(label = "Path", description = "The root path of the endpoint")
   private UIInput<String> path;

   @Inject
   private Inflector inflector;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name(SPRING_REST_CAT + "New Endpoint")
               .description("Creates a new Spring REST Endpoint");
   }

   @Override
   protected String getType()
   {
      return "REST";
   }

   @Override
   protected Class<JavaClassSource> getSourceType()
   {
      return JavaClassSource.class;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      super.initializeUI(builder);
      builder.add(methods).add(path);
   }

   @Override
   public JavaClassSource decorateSource(UIExecutionContext context, Project project, JavaClassSource source)
            throws Exception
   {

      JavaSourceFacet javaSource = project.getFacet(JavaSourceFacet.class);

      // Create the GreetingProperties java class
      JavaClassSource greetingProperties = Roaster.create(JavaClassSource.class);
      greetingProperties.setName("GreetingProperties").setPackage(source.getPackage());
      greetingProperties.addAnnotation(Component.class);
      greetingProperties.addAnnotation(ConfigurationProperties.class).setStringValue("greeting");
      greetingProperties.addProperty(String.class,"message").createAccessor();

      // Save it
      javaSource.saveJavaSource(greetingProperties);

      // Add @RestController to the class
      source.addAnnotation(RestController.class);

      // Add GreetingProperties
      source.addField().setPrivate().setFinal(false).setType("GreetingProperties").setName("properties").addAnnotation(Autowired.class);

      // Add Counter
      source.addField().setPrivate().setFinal(true).setType("AtomicLong").setName("counter").setStringInitializer("new AtomicLong()");

      for (RestMethod method : methods.getValue())
      {
         MethodSource<?> greeting = source.addMethod()
                                          .setPublic()
                                          .setName(method.getMethodName())
                                          .setReturnType("Greeting");

         switch (method)
         {
         case GET:
            greeting.addAnnotation(RequestMapping.class).setStringValue("/greeting");
            greeting.addParameter(String.class,"name").addAnnotation(RequestParam.class).setLiteralValue("value","name").setLiteralValue("defaultValue","world");
            greeting.setBody("new Greeting(this.counter.incrementAndGet(), String.format(this.properties.getMessage(), name));");
            break;
         case POST:
            source.addImport(UriBuilder.class);
/*            doGet.addAnnotation(javax.ws.rs.Consumes.class).setStringArrayValue(
                     new String[] { MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON });
            doGet.addParameter(String.class, "entity");
            doGet.setBody("return Response.created(UriBuilder.fromResource(" + getNamed().getValue()
                     + ".class).build()).build();");*/
            break;
         case PUT:
/*            source.addImport(UriBuilder.class);
            doGet.addAnnotation(javax.ws.rs.Consumes.class).setStringArrayValue(
                     new String[] { MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON });
            doGet.addParameter(String.class, "entity");
            doGet.setBody("return Response.created(UriBuilder.fromResource(" + getNamed().getValue()
                     + ".class).build()).build();");*/
            break;
         case DELETE:
/*            doGet.addAnnotation(Path.class).setStringValue("/{id}");
            doGet.addParameter(Long.class, "id").addAnnotation(PathParam.class).setStringValue("id");
            doGet.setBody("return Response.noContent().build();");*/
            break;
         }
      }

      return source;
   }
}
