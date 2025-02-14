package net.global_village.anttasks.jass;

import java.io.*;
import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import org.objectstyle.woproject.ant.*;



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
 *            &lt;frameworks root="${wo.wosystemroot}"&gt;
 *                &lt;patternset&gt;
 *                    &lt;includesfile name="woproject/ant.frameworks.wo.wosystemroot"/&gt;
 *                &lt;/patternset&gt;
 *            &lt;/frameworks&gt;
 *            &lt;frameworks root="${wo.wolocalroot}"&gt;
 *                &lt;patternset&gt;
 *                    &lt;includesfile name="woproject/ant.frameworks.wo.wolocalroot"/&gt;
 *                &lt;/patternset&gt;
 *            &lt;/frameworks&gt;
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
 *   <tr>
 *     <td>frameworks</td><td>FrameworkSet</td><td>The frameworks as WOLips/woproject uses them to use when running Jass.  See <a href="http://www.objectstyle.org/woproject/ant/frameworkset.html">frameworkset</a>.</td>
 *   </tr>
 * </table>
 * </p>
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 *
 * @deprecated This class is obsolete, WOPath allows the Jass task to be used
 */
public class WOProjectJass extends Jass {

    private Vector frameworks = new Vector();
    private Vector frameworkPaths = new Vector();
    private Path generatedClasspath;


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
            generatedClasspath = super.generatedClasspath();

            for (Iterator jarIterator = frameworkPaths().iterator(); jarIterator.hasNext();)
            {
                File jarFile = (File) jarIterator.next();
                generatedClasspath.setPath(jarFile.getAbsolutePath());
            }
        }

        return generatedClasspath;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns a list of Files representing the jars in the <frameworks/> attribute.  These are added to the Jass
     * classpath.
     *
     * @return list of Files representing the jars in the <frameworks/> attribute
     */
     protected Vector frameworkPaths()
     {
// This obsolete code no longer compiles as of WOLips New Hotness
//        // Lazy creation
//        if (frameworkPaths.isEmpty())
//        {
//            // Use a set to avoid duplicates
//            HashSet jarSet = new HashSet();
//
//            for (int i = 0; i < frameworks().size(); i++)
//            {
//                FrameworkSet frameworkSet = (FrameworkSet) frameworks().get(i);
//
//                try
//                {
//                    String[] frameworksInSet = frameworkSet.getFrameworks();
//
//                    for (int j = 0; j < frameworksInSet.length; j++)
//                    {
//                        File[] jarsInFramework = frameworkSet.findJars(frameworksInSet[j]);
//                        if (jarsInFramework != null)
//                        {
//                            jarSet.addAll(Arrays.asList(jarsInFramework));
//                        }
//                    }
//                }
//                catch (BuildException be)
//                {
//                    log(be.getMessage(), Project.MSG_WARN);
//                }
//            }
//
//            frameworkPaths = new Vector(jarSet);
//        }
//
        return frameworkPaths;
     }



     /**
      * Jass task attribute.  Adds to the list of Framework FileSets to create classpath from.
      *
      * @param someFrameworks the new FrameworkSet to add
      */
     public void addFrameworks(FrameworkSet someFrameworks) throws BuildException
    {
         frameworks.add(someFrameworks);
     }



     /**
      * Jass task attribute.  Returns the list of FrameworkSets to add to classpath.
      *
      * @return the list of FrameworkSets to add to classpath
      */
     public Vector frameworks()
     {
         return frameworks;
     }
}
