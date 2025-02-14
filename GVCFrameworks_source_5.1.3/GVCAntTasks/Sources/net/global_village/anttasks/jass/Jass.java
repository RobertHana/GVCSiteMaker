package net.global_village.anttasks.jass;

import java.io.*;
import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.*;
import org.apache.tools.ant.taskdefs.Ant.Reference;
import org.apache.tools.ant.types.*;

import net.global_village.anttasks.Task;



/**
 * <p>An Ant Task facilitating the use of <a href="http://csd.informatik.uni-oldenburg.de/~jass/">
 * Jass</a> to generate contracted Java source files.  See the Jass documentation for details on the
 * contracting process.</p>
 *
 * <p>Here is an example usage of the task:
 * <pre>
 *   &lt;target name="dist.jass" depends="setProps" if="use.jass"&gt;
 *
 *        &lt;!-- Define this for use when compiling contracted source --&gt;
 *        &lt;path id="contracted.class.path"&gt;
 *            &lt;pathelement location="ContractedSource"/&gt;
 *        &lt;/path&gt;
 *        &lt;property name="project.source.path" value="contracted.class.path"/&gt;
 *
 *        &lt;jass contract="pre,post,inv,loop,check,forall"
 *            isDebugging="${jass.debug}"
 *            destination="ContractedSource"&gt;
 *            &lt;source dir="src"&gt;
 *                &lt;include name="**&#47;*.java"/&gt;
 *            &lt;/source&gt;
 *            &lt;source dir="GeneratedEOs"&gt;
 *                &lt;include name="**&#47;*.java"/&gt;
 *            &lt;/source&gt;
 *             &lt;classpath&gt;
 *                &lt;path location="${junit.path}"/&gt;
 *                &lt;path location="${jass.path}"/&gt;
 *            &lt;/classpath&gt;
 *       &lt;/jass&gt;
 *   &lt;/target&gt;
 * </pre>
 * </p>
 * <p><code>depends="setProps"</code> is there to load any property definitions before starting the
 * contrating process.</p>
 * <p><code>if="use.jass"</code> is used to make this target optional</p>
 * <p><b>Note</b>: the example above references several properties that are assumed to have been set
 * before executing the task.</p>
 * <p>This section is used to set up a classpath that can be used by wocompile to later compile the
 * contracted source:
 * <pre>
 *        &lt;!-- Define this for use when compiling contracted source --&gt;
 *       &lt;path id="contracted.class.path"&gt;
 *            &lt;pathelement location="ContractedSource"/&gt;
 *        &lt;/path&gt;
 *        &lt;property name="project.source.path" value="contracted.class.path"/&gt;
 * </pre></p>
 * <p>These attributes are recognized by the jass task:<br/>
 * <table border="1">
 *   <tr align="left">
 *     <th>Name</th><th>Type</th><th>Use</th>
 *   </tr>
 *   <tr>
 *     <td>contract</td><td>String</td><td>Comma separated list of contract checks to generate as
 *         described in the Jass documentation.</td>
 *   </tr>
 *   <tr>
 *     <td>isDebugging</td><td>boolean</td><td><code>on</code> or <code>off</code> to control output
 *         of verbose debugging information.</td>
 *   </tr>
 *   <tr>
 *     <td>destination</td><td>Path</td><td>The location to generate contracted source at, not
 *         including package names.</td>
 *   </tr>
 *   <tr>
 *     <td>source</td><td>FileSet</td><td>One or more FileSets defining the locations to read source
 *         file from, not including package names.</td>
 *   </tr>
 *   <tr>
 *     <td>classpath</td><td>Path</td><td>The classpath to use when running Jass.</td>
 *   </tr>
 * </table>
 * </p>
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class Jass extends Task
{
    private boolean isDebugging = false;
    private String contract;
    private Path classpath;
    private FileSet source;
    private Vector sourcePaths = new Vector();
    private Vector sourceFiles = new Vector();
    private Path destination;
    private Path generatedClasspath;



    /**
     * Runs Jass task.
     *
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException
    {
        logDebugInfo();
        launchJass();
    }



    /**
     * Logs out useful debug information.
     */
    protected void logDebugInfo()
    {
        if (isDebugging())
        {
            log("Starting Jass task", Project.MSG_INFO);
            log("Contracts are " + contract(), Project.MSG_INFO);
            log("Classpath is " + generatedClasspath().toString(), Project.MSG_INFO);

            log("Source paths are", Project.MSG_INFO);
            for (Iterator sourceIterator = sourcePaths().iterator(); sourceIterator.hasNext();)
            {
                FileSet fileset = (FileSet) sourceIterator.next();
                log("   " + fileset.getDir(getProject()).getAbsolutePath(), Project.MSG_INFO);
            }

            log("Source files: " , Project.MSG_INFO);
            Enumeration sourceFilesEnum = sourceFiles().elements();
            while (sourceFilesEnum.hasMoreElements())
            {
                String aFilePath = (String) sourceFilesEnum.nextElement();
                log("  " + aFilePath, Project.MSG_INFO);
            }

            log("Destination is " + destination(), Project.MSG_INFO);
        }
    }



    /**
     * Creates a command line and launches EOGenerator.
     */
    protected void launchJass()
    {
    	// Jass kicks up a fuss if there is nothing to process.
    	if (sourceFiles().size() == 0)
    	{
    		log("No files to process, skipping Jass");
    		return;
    	}

        Java javaTask = (Java) getProject().createTask("java");
        javaTask.setClassname("jass.Jass");
        javaTask.setTaskName(getTaskName());
        javaTask.setClasspath(generatedClasspath());

        // Contracts selected for generation
        javaTask.createArg().setValue("-contract[" + contract() + "]");

        // Destination directory
        javaTask.createArg().setValue("-d");
        javaTask.createArg().setValue(destination().toString());

        // If we don't fork it does not see the classpath we setup above.  I don't understand why not.
        javaTask.setFork(true);

        // The fully qualified path name of each of the source files is provided to Jass
        Enumeration sourceFilesEnum = sourceFiles().elements();
        while (sourceFilesEnum.hasMoreElements())
        {
            javaTask.createArg().setValue((String)sourceFilesEnum.nextElement());
        }

        // Run Jass and check for errors
        if (javaTask.executeJava() != 0)
        {
            throw new BuildException("Error contracting source");
        }
    }




    /**
     * Returns the classpath to run Jass with.  This is the path to all of the jar on the explicit classpath,
     * the path to all of the jars in all of the frameworks referenced, <b>and</b> the path to the source files
     * (the root directory, without any package directories just like a real classpath).
     *
     * @return the classpath to run Jass with
     */
    protected Path generatedClasspath()
    {
        if (generatedClasspath == null)
        {
            generatedClasspath = new Path(getProject());

            for (Iterator sourceIterator = sourcePaths().iterator(); sourceIterator.hasNext();)
            {
                FileSet fileset = (FileSet) sourceIterator.next();
                generatedClasspath.setPath(fileset.getDir(getProject()).getAbsolutePath());
            }

            generatedClasspath.append(classpath());
        }

        return generatedClasspath;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns a list of the fully qualified paths for of each of the source files to be processed
     * by Jass.
     *
     * @return a list of the fully qualified paths for of each of the source files to be processed
     * by Jass
     */
    protected Vector sourceFiles()
    {
        if (sourceFiles.isEmpty())
        {
            for (Iterator sourceIterator = sourcePaths().iterator(); sourceIterator.hasNext();)
            {
                FileSet fileset = (FileSet) sourceIterator.next();

                DirectoryScanner ds = fileset.getDirectoryScanner(getProject());
                String[] sourceFilesInDirectory = ds.getIncludedFiles();
                for (int i = 0; i < sourceFilesInDirectory.length; i++)
                {
                    sourceFiles.add(ds.getBasedir() + File.separator + sourceFilesInDirectory[i]);
                }
            }
        }

        return sourceFiles;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Jass task attribute.  Returns <code>true</code> if debugging mode is enabled
     *
     * @return <code>true</code> if debugging mode is enabled
     */
    public boolean isDebugging()
    {
        return isDebugging;
    }



    /**
     * Jass task attribute.  Turns debug mode on and off.
     *
     * @param aBoolean <code>true</code> if debugging mode is enabled
     */
    public void setIsDebugging(boolean aBoolean)
    {
        isDebugging = aBoolean;
    }



    /**
     * Jass task attribute.  Returns a comma separated string of the contracts enabled for generation.
     *
     * @return a comma separated string of the contracts enabled for generation
     */
    public String contract()
    {
        return contract;
    }



    /**
     * Jass task attribute.  Sets the contracts enabled for generation.
     *
     * @param string comma separated string of the contracts enabled for generation
     */
    public void setContract(String string)
    {
        contract = string;
    }


    /**
     * Jass task attribute.  Returns a new portion of the classpath to be used to run Jass.
     *
     * @return a new portion of the classpath to be used to run Jass
     */
    public Path createClasspath()
    {
        if (classpath == null)
        {
            classpath = new Path(getProject());
        }
        return classpath.createPath();
    }



    /**
     * Jass task attribute.  Adds to the classpath to be used to run Jass.
     *
     * @param newClasspath  the new path to add to the existing classpath
     */
    public void setClasspath(Path newClasspath)
    {
        if (classpath == null)
        {
            classpath = newClasspath;
        }
        else
        {
            classpath.append(newClasspath);
        }
    }



    /**
     * Jass task attribute.  Adds, by reference, to the classpath to be used to run Jass.
     *
     * @param r the refererence to the classpath to be added
     */
    public void setClasspathRef(Reference r)
    {
        createClasspath().setRefid(r);
    }



    /**
     * Jass task attribute.  Returns the classpath to be used to run Jass.
     *
     * @return the classpath to be used to run Jass
     */
    public Path classpath()
    {
        return classpath;
    }



    /**
     * Jass task attribute.  Adds to the list of FileSets to take source files from.
     *
     * @param newPath the new FileSet to add
     */
    public void addSource(FileSet newPath)
    {
        sourcePaths.add(newPath);
    }



    /**
     * Jass task attribute.  Returns the list of FileSets to take source files from.
     *
     * @return the list of FileSets to take source files from
     */
    public Vector sourcePaths()
    {
        return sourcePaths;
    }



    /**
     * Jass task attribute.  Returns the Path to write the contracted source files to.
     *
     * @return the Path to write the contracted source files to
     */
    public Path destination()
    {
        return destination;
    }



    /**
     * Jass task attribute.  Sets the Path to write the contracted source files to.
     *
     * @param path the Path to write the contracted source files to.
     */
    public void setDestination(Path path)
    {
        destination = path;
    }

}
