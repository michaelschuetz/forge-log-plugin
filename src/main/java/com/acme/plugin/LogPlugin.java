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

    // log myvalue
    @DefaultCommand
    public void exampleDefaultCommand(@Option String opt) {
        System.out.println(">> invoked default command with option value: " + opt);
        System.out.println("## " + shell.getCurrentResource());


        JavaSourceFacet javaFacet = project.getFacet(JavaSourceFacet.class);


        try {
            JavaClass clazz = getJavaClass();

            // bob as param
            Field<JavaClass> field = clazz.addField();
            Class type = Logger.class;

            field.setName("log").setPrivate().setType(type).addAnnotation(Inject.class);

            // TODO msc How to change existing methods?
            Method<JavaClass> method = clazz.getMethod("save");
            String body = method.getBody();
            method.setBody(body);

            javaFacet.saveJavaSource(clazz);

        } catch (FileNotFoundException e) {
            shell.println("Could not locate the class requested. No update was made.");
        }

    }

    // log perform myvalue
    @Command("perform1")
    public void exampleCommand(@Option String opt, PipeOut out) {

        out.println(">> the command \"perform\" was invoked with the value: " + opt);
    }

    // log --one cat --two dog
    @Command("perform2")
    public void exampleCommand(
            @Option(name = "one", shortName = "o") String one, @Option(name = "two") String two, PipeOut out) {
        out.println(">> option one equals: " + one);
        out.println(">> option two equals: " + two);
    }

    // TODO msc rausziehen
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
