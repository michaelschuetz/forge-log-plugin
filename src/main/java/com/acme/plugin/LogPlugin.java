package com.acme.plugin;

import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.resources.java.JavaResource;
import org.jboss.seam.forge.shell.plugins.*;

import javax.inject.Inject;

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

    @Inject
   private Project project;

    // log myvalue
    @DefaultCommand
    public void exampleDefaultCommand(@Option String opt) {
        System.out.println(">> invoked default command with option value: " + opt);
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
}
