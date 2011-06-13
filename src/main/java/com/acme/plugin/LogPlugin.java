package com.acme.plugin;

import org.jboss.logging.Logger;
import org.jboss.seam.forge.parser.java.Field;
import org.jboss.seam.forge.parser.java.JavaClass;
import org.jboss.seam.forge.parser.java.JavaSource;
import org.jboss.seam.forge.parser.java.Method;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.facets.JavaSourceFacet;
import org.jboss.seam.forge.resources.Resource;
import org.jboss.seam.forge.resources.java.JavaResource;
import org.jboss.seam.forge.shell.Shell;
import org.jboss.seam.forge.shell.plugins.*;

import javax.inject.Inject;
import java.io.FileNotFoundException;

/**
 * Created by IntelliJ IDEA.
 * User: mschuetz
 * Date: 10.06.11
 * Time: 23:13
 * To change this template use File | Settings | File Templates.
 */

@Alias("log")
@RequiresProject
@RequiresResource(JavaResource.class)
@Help("A plugin to add simple logging")
public class LogPlugin implements Plugin {

    private final Project project;
    private final Shell shell;

    @Inject
    public LogPlugin(final Project project, final Shell shell) {
        this.project = project;
        this.shell = shell;
    }

    @DefaultCommand(help = "injects logger and add log statement for method")
    public void exampleDefaultCommand(@Option(name = "method", shortName = "m", required = true) String targetMethod, PipeOut out) {
        out.println(">> invoked default log command with option value: " + targetMethod);

        final JavaSourceFacet javaFacet = project.getFacet(JavaSourceFacet.class);

        try {
            JavaClass clazz = getJavaClass();

            final String fieldName = "log";
            // add new field if not exists
            if (clazz.getField(fieldName) != null) {
                final Field<JavaClass> field = clazz.addField();
                field.setName(fieldName).setPrivate().setType(Logger.class).addAnnotation(Inject.class);
            }

            Method<JavaClass> method = clazz.getMethod(targetMethod);
            String body = method.getBody();
            method.setBody("log.info(\"############### new Customer TODO created\");" + body);

            javaFacet.saveJavaSource(clazz);

        } catch (FileNotFoundException e) {
            out.println("Could not locate the class requested. No update was made.");
        }

    }

    private JavaClass getJavaClass() throws FileNotFoundException {
        Resource<?> resource = shell.getCurrentResource();
        if (resource instanceof JavaResource) {
            return getJavaClassFrom(resource);
        } else {
            throw new RuntimeException("Current resource is not a JavaResource!");
        }

    }


    private JavaClass getJavaClassFrom(final Resource<?> resource) throws FileNotFoundException {
        JavaSource<?> source = ((JavaResource) resource).getJavaSource();
        if (!source.isClass()) {
            throw new IllegalStateException("Current resource is not a JavaClass!");
        }
        return (JavaClass) source;
    }

}
