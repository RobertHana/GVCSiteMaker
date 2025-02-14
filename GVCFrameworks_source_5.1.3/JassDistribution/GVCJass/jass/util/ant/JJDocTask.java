package jass.util.ant;



import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.util.*;
import org.apache.tools.ant.types.*;

import java.io.*;

import java.util.*;


/**
 * Class JJDocTask
 *
 *
 * @author Mark Broerkens
 * @version 0.1
 */
public class JJDocTask extends Task {

    private File            javaccHome;
    private boolean         text       = false;
    private boolean         oneTable   = true;
    private File            outputFile = null;
    private File            targetFile = null;
    private CommandlineJava cmdl       = new CommandlineJava();

    /**
     * Method setJavacchome
     *
     *
     * @param javaccHome
     *
     */
    public void setJavacchome(File javaccHome) {
        this.javaccHome = javaccHome;
    }

    /**
     * Method setText
     *
     *
     * @param text
     *
     */
    public void setText(boolean text) {
        this.text = text;
    }

    /**
     * Method setOnetable
     *
     *
     * @param oneTable
     *
     */
    public void setOnetable(boolean oneTable) {
        this.oneTable = oneTable;
    }

    /**
     * Method setOutputfile
     *
     *
     * @param outputFile
     *
     */
    public void setOutputfile(File outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * Method setTarget
     *
     *
     * @param targetFile
     *
     */
    public void setTarget(File targetFile) {
        this.targetFile = targetFile;
    }

    /**
     * Constructor JJDocTask
     *
     *
     */
    public JJDocTask() {
        cmdl.setVm("java");
        cmdl.setClassname("COM.sun.labs.jjdoc.JJDocMain");
    }

    /**
     * Method execute
     *
     *
     * @throws BuildException
     *
     */
    public void execute() throws BuildException {

        // check for classpath
        if ((javaccHome == null) ||!javaccHome.isDirectory()) {
            throw new BuildException("Javacchome not set.");
        }

        final Path classpath = cmdl.createClasspath(project);

        classpath.createPathElement().setPath(javaccHome.getAbsolutePath()
                                              + "/JavaCC.zip");

        // check for target
        if ((targetFile == null) ||!targetFile.isFile()) {
            throw new BuildException("target not set.");
        }

        // set classpath
        if (classpath != null) {
            cmdl.createVmArgument().setValue("-classpath");
            cmdl.createVmArgument().setPath(classpath);
        }

        // set vm options
        cmdl.createVmArgument().setValue("-mx140M");
        cmdl.createVmArgument().setValue("-Dinstall.root="
                                         + javaccHome.getAbsolutePath());

        // set arguments
        cmdl.createArgument().setValue("-TEXT:" + text);
        cmdl.createArgument().setValue("-ONE_TABLE:" + oneTable);

        if (outputFile != null) {
            cmdl.createArgument()
                .setValue("-OUTPUT_FILE:"
                          + outputFile.getAbsolutePath().replace('\\', '/'));
        }

        // set inputfile
        cmdl.createArgument()
            .setValue(targetFile.getAbsolutePath().replace('\\', '/'));

        // execute jjdoc
        final Execute process =
            new Execute(new LogStreamHandler(this, Project.MSG_INFO, Project
                .MSG_INFO), null);

        log(cmdl.toString(), Project.MSG_VERBOSE);
        process.setCommandline(cmdl.getCommandline());

        try {
            if (process.execute() != 0) {
                throw new BuildException("JJDoc failed.");
            }
        } catch (IOException e) {
            throw new BuildException("Failed to launch JJDoc: " + e);
        }
    }
}
