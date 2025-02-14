package net.global_village.anttasks.eogenerator;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DynamicConfigurator;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;

/**
 * <p>An Ant Task facilitating the use of <a href="http://www.rubicode.com/Software/EOGenerator/">
 * EOGenerator</a> to generate the Java source for EOEnterpriseObjects.  See the
 * EOGenerator documentation for details on the code generation process.</p>
 * 
 * <p>Here is an example usage of the task:
 * <pre>
 *  &lt;target name="test" depends="setProps"&gt;
 *       &lt;eogenerator
 *           isDebugging="on"
 *           model="TestModel1.eomodeld"
 *           baseClassDestination="GeneratedEOs"&gt;
 *           subClassDestination="testSrc"
 *           &lt;referencedModels&gt;
 *               &lt;model location="../EOPrototypes/EOPrototypes.eomodeld" /&gt;
 *           &lt;/referencedModels&gt;
 *       &lt;/eoGenerator&gt;
 *   &lt;/target&gt;
 * </pre>
 * </p>
 * 
 * <p>These properties are recognized by the eogenerator task:<br/>
 * <table border="1">
 *   <tr align="left">
 *     <th>Name</th><th>Type</th><th>Use</th>
 *   </tr>
 *   <tr>
 *     <td>executable</td><td>File</td><td>The EOGenerator executable.</td>
 *   </tr>
 *   <tr>
 *     <td>model</td><td>Path</td><td>The model to generate classes for.</td>
 *   </tr>
 *   <tr>
 *     <td>baseClassDestination</td><td>Path</td><td>The location to generate base-classes at, not including package names.</td>
 *   </tr>
 *   <tr>
 *     <td>subClassDestination</td><td>Path</td><td>The location to generate sub-classes at, not including package names.</td>
 *   </tr>
 *   <tr>
 *     <td>templateDirectory</td><td>Path</td><td>The directory containing the templates for EOGenerator.</td>
 *   </tr>
 *   <tr>
 *     <td>baseClassTemplate</td><td>String</td><td>Name of the template file for base classes.</td>
 *   </tr>
 *   <tr>
 *     <td>subClassTemplate</td><td>String</td><td>Name of the template file for sub-classes.</td>
 *   </tr>
 *   <tr>
 *     <td>entities</td><td>String</td><td>Optional, space seperated, list of entities to generate classes.  If ommitted, all entities are generated</td>
 *   </tr>
 *   <tr>
 *     <td>isDebugging</td><td>boolean</td><td>Dumps out details of EOGenerator command useful when things go wrong.</td>
 *   </tr>
 *   <tr>
 *     <td>referencedModels</td><td>ReferencedModels(path structure)</td><td>Paths to models referenced by model that classes are being generated for.</td>
 *   </tr>
 *   <tr>
 *     <td><i>&lt;anything else&gt;<i></td><td>String</td><td>Define passed to EOGenerator.</td>
 *   </tr>
 * </table>
 * </p>
 * 
 * <p>Additionally, some special property names can be used in place of direct specification
 * in the task.  This is useful for general settings in the the <code>wobuild.properties</code> file
 * and overrides in the project's <code>build.properties</code> file.  These are:
 * <table border="1">
 *   <tr align="left">
 *     <th>eogenerator Task Property</th><th>wobuild.properties / build.properties name</th>
 *   </tr>
 *   <tr>
 *     <td>executable</td><td>eogenerator.executable</td>
 *   </tr>
 *   <tr>
 *     <td>templateDirectory</td><td>eogenerator.template.dir</td>
 *   </tr>
 *   <tr>
 *     <td>baseClassTemplate</td><td>eogenerator.baseClassTemplate</td>
 *   </tr>
 *   <tr>
 *     <td>subClassTemplate</td><td>eogenerator.subClassTemplate</td>
 *   </tr>
 * </table>
 * </p>
 * <p>Three special property names are also associated with defines.  These are useful 
 * for general settings in the the <code>wobuild.properties</code> file and overrides in the 
 * project's <code>build.properties</code> file. These are:
 * <table border="1">
 *   <tr align="left">
 *     <th>eogenerator Task Property</th><th>wobuild.properties / build.properties name</th>
 *   </tr>
 *   <tr>
 *     <td>EOSuperClass</td><td>eogenerator.superclass</td>
 *   </tr>
 *   <tr>
 *     <td>copyrightYear</td><td>eogenerator.copyrightYear</td>
 *   </tr>
 *   <tr>
 *     <td>copyrightBy</td><td>eogenerator.eogenerator.copyrightBy</td>
 *   </tr>
 * </table>
 * </p>
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class EOGenerator extends net.global_village.anttasks.Task implements DynamicConfigurator
{
    
    public static final String EOSUPER_CLASS_DEFINE = "EOSuperClass";
    public static final String COPYRIGHT_YEAR_DEFINE = "copyrightYear";
    public static final String COPYRIGHT_BY_DEFINE = "copyrightBy";
    
    public static final String EXECUTABLE_PROPERTY = "eogenerator.executable";
    public static final String TEMPLATE_DIRECTORY_PROPERTY = "eogenerator.template.dir";
    public static final String BASE_CLASS_TEMPLATE_PROPERTY = "eogenerator.baseClassTemplate";
    public static final String SUB_CLASS_TEMPLATE_PROPERTY = "eogenerator.subClassTemplate";
    public static final String EOSUPER_CLASS_PROPERTY = "eogenerator.superclass";
    public static final String COPYRIGHT_YEAR_PROPERTY = "eogenerator.copyrightYear";
    public static final String COPYRIGHT_BY_PROPERTY = "eogenerator.copyrightBy";
    
    protected File executable;
    protected Path model;
    protected Path baseClassDestination;
    protected Path subClassDestination; 
    protected Path templateDirectory;
    protected String baseClassTemplate;
    protected String subClassTemplate;
    protected ReferencedModels referencedModels;
    protected String entities;    
    protected boolean isDebugging = false;
    protected Hashtable defines = new Hashtable();
    
    

    /**
     * Runs EOGenerator.
     * 
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException
    {
        setValuesFromPropertyFiles();
        logDebugInfo();
        checkErrorConditions();
        launchEOGenerator();
    }



    /**
     * Uses the values from the project's properties for executable, 
     * templateDirectory, baseClassTemplate, subClassTemplate, and the EOSuperClass, 
     * copyrightYear, and copyrightBy defines if they were not set in the xml file.
     */
    public void setValuesFromPropertyFiles()
    {
        if ((executable() == null) && isPropertyDefined(EXECUTABLE_PROPERTY))
        {
            setExecutable(new File(propertyNamed(EXECUTABLE_PROPERTY)));
        }
        
        if ((templateDirectory() == null) && isPropertyDefined(TEMPLATE_DIRECTORY_PROPERTY))
        { 
            setTemplateDirectory(new Path(getProject(), propertyNamed(TEMPLATE_DIRECTORY_PROPERTY)));
        }
        
        if ((baseClassTemplate() == null) && isPropertyDefined(BASE_CLASS_TEMPLATE_PROPERTY))
        { 
            setBaseClassTemplate(propertyNamed(BASE_CLASS_TEMPLATE_PROPERTY));
        }

        
        if ((subClassTemplate() == null) && isPropertyDefined(SUB_CLASS_TEMPLATE_PROPERTY))
        { 
            setSubClassTemplate(propertyNamed(SUB_CLASS_TEMPLATE_PROPERTY));
        }
        
        if ( ( ! isDefined(COPYRIGHT_YEAR_DEFINE)) && (isPropertyDefined(COPYRIGHT_YEAR_PROPERTY)) )
        {
            setDynamicAttribute(COPYRIGHT_YEAR_DEFINE, propertyNamed(COPYRIGHT_YEAR_PROPERTY));
        }
        
        if ( ( ! isDefined(COPYRIGHT_BY_DEFINE)) && (isPropertyDefined(COPYRIGHT_BY_PROPERTY)) )
        {
            setDynamicAttribute(COPYRIGHT_BY_DEFINE, propertyNamed(COPYRIGHT_BY_PROPERTY));
        }

        if ( ( ! isDefined(EOSUPER_CLASS_DEFINE)) && (isPropertyDefined(EOSUPER_CLASS_PROPERTY)) )
        {
            setDynamicAttribute(EOSUPER_CLASS_DEFINE, propertyNamed(EOSUPER_CLASS_PROPERTY));
        }
    }



    /**
     * Checks for error conditions.  At present, only that the EOGenerator executable
     * can be found. 
     */
    public void checkErrorConditions()
    {
        if ((executable() == null) || (! executable().exists()))
        {
            throw new BuildException("eoGenerator executable not set or not found.");
        }
    }
    
    
    
    /**
     * Logs out useful debug information. 
     */
    public void logDebugInfo()
    {
        if (isDebugging())
        {
            log("Starting eoGenerator task", Project.MSG_INFO);
            log("Executable is " + executable(), Project.MSG_INFO);
            log("Model is " + model(), Project.MSG_INFO);
            log("Generating entities: " + (entities() == null ? "All" : entities()), Project.MSG_INFO);
            
            log("Referenced Models: ", Project.MSG_INFO);
            String[] paths = referencedModels.list();
            for (int i = 0; i < paths.length; i++) {
                log("  " + paths[i], Project.MSG_INFO);
            }
            log("eoSuperClass is " + defines().get(EOSUPER_CLASS_DEFINE), Project.MSG_INFO);
            log("Base-classes generated to " + baseClassDestination(), Project.MSG_INFO);
            log("Sub-classes generated to " + subClassDestination(), Project.MSG_INFO);
            log("Using templates from " + templateDirectory(), Project.MSG_INFO);
            
            log("Base-class template " + baseClassTemplate(), Project.MSG_INFO);
            log("Sub-class template " + subClassTemplate(), Project.MSG_INFO);
            
            log("Defined Values: ", Project.MSG_INFO);
            for (Enumeration definedKeys = defines().keys() ; definedKeys.hasMoreElements() ;) 
            {
                String aKey = (String) definedKeys.nextElement();
                log("  " + aKey + "=" + definitionFor(aKey), Project.MSG_INFO);
            }
        }
    }
        


    /**
     * Creates a command line and launches EOGenerator. 
     */
    public void launchEOGenerator()
    {
        Commandline cmd = new Commandline();
        cmd.setExecutable(executable().toString());
        
        cmd.createArgument().setValue("-model");
        cmd.createArgument().setPath(model());
        
        String[] paths = referencedModels.list();
        for (int i = 0; i < paths.length; i++) {
            cmd.createArgument().setValue("-refmodel");
            cmd.createArgument().setValue(paths[i]);
        }
                 
        cmd.createArgument().setValue("-destination");
        cmd.createArgument().setPath(baseClassDestination());

        cmd.createArgument().setValue("-subclassDestination");
        cmd.createArgument().setPath(subClassDestination());
        
        cmd.createArgument().setValue("-templatedir");
        cmd.createArgument().setPath(templateDirectory());
        
        cmd.createArgument().setValue("-javaTemplate");
        cmd.createArgument().setValue(baseClassTemplate());
        
        cmd.createArgument().setValue("-subclassJavaTemplate");
        cmd.createArgument().setValue(subClassTemplate());

        cmd.createArgument().setValue("-java");
        cmd.createArgument().setValue("-packagedirs");
        
        if (isDebugging())
        {
            cmd.createArgument().setValue("-verbose");
        }

        for (Enumeration definedKeys = defines().keys() ; definedKeys.hasMoreElements() ;) 
        {
            String aKey = (String) definedKeys.nextElement();
            cmd.createArgument().setValue("-define-" + aKey);
            cmd.createArgument().setValue(definitionFor(aKey));
        }

        PumpStreamHandler streamHandler = new PumpStreamHandler();
        Execute runner = new Execute(streamHandler, null);
        runner.setAntRun(getProject());
        runner.setCommandline(cmd.getCommandline());
        
        int returnValue = 0;
        try
        {
            returnValue = runner.execute();
        }
        catch (IOException e)
        {
            log(e.getMessage(), Project.MSG_INFO);
        }
    }
    
   
   
    /**
     * Overridden to add unrecogized attributes to the defines passed to EOGenerator.
     * 
     * @see org.apache.tools.ant.DynamicConfigurator#setDynamicAttribute(java.lang.String, java.lang.String)
     */
    public void setDynamicAttribute(String name, String value) throws BuildException
    {
        defines().put(name, value);
    }



    /**
     * Overridden to throw.  This task does not handle dynamic elements.
     * 
     * @see org.apache.tools.ant.DynamicConfigurator#createDynamicElement(java.lang.String)
     */
    public Object createDynamicElement(String arg0) throws BuildException
    {
        throw new BuildException("DynamicElement " + arg0 + " not handled.");
    }



    /**
     * Returns the EOGenerator defintion for <code>key</code> if it is defined.
     * 
     *  @return the EOGenerator defintion for <code>key</code> if it is defined, 
     * null otherwise.
     */
    public String definitionFor(String key)
    {
        return (String) defines().get(key);
    }
    
    

    /**
     * Returns <code>true</code> if there is a EOGenerator defintion for <code>key</code>.
     * 
     *  @return <code>true</code> if there is a EOGenerator defintion for <code>key</code>, 
     * <code>false</code> otherwise.
     */
    public boolean isDefined(String key)
    {
        return definitionFor(key) != null;
    }


    //
    // Move along folks, nothing to see below here except generator mutators and
    // accessors.  That's right, move along folks, just move along.
    //
    
        
    /**
     * @return fully qualified path to EOGenerator executable
     */
    public File executable()
    {
        return executable;
    }

    /**
     * @param file fully qualified path to EOGenerator executable
     */
    public void setExecutable(File file)
    {
        executable = file;
    }

    /**
     * @return path to model to generate from
     */
    public Path model()
    {
        return model;
    }

    /**
     * @param path the path to model to generate from
     */
    public void setModel(Path path)
    {
        model = path;
    }

    /**
     * @return directory to generate the base classes in 
     */
    public Path baseClassDestination()
    {
        return baseClassDestination;
    }

    /**
     * @param path the directory to generate the base classes in 
     */
    public void setBaseClassDestination(Path path)
    {
        baseClassDestination = path;
    }

    /**
     * @return directory to generate the subclasses in 
     */
    public Path subClassDestination()
    {
        return subClassDestination;
    }

    /**
     * @param path the directory to generate the subclasses in 
     */
    public void setSubClassDestination(Path path)
    {
        subClassDestination = path;
    }

    /**
     * @return the directory to take the templates from
     */
    public Path templateDirectory()
    {
        return templateDirectory;
    }

    /**
     * @param path the directory to take the templates from
     */
    public void setTemplateDirectory(Path path)
    {
        templateDirectory = path;
    }

    /**
     * @return the name of the template to generate the base classes from
     */
    public String baseClassTemplate()
    {
        return baseClassTemplate;
    }

    /**
     * @param string the name of the template to generate the base classes from
     */
    public void setBaseClassTemplate(String string)
    {
        baseClassTemplate = string;
    }

    /**
     * @return the name of the template to generate the subclasses from
     */
    public String subClassTemplate()
    {
        return subClassTemplate;
    }

    /**
     * @param string the name of the template to generate the subclasses from
     */
    public void setSubClassTemplate(String string)
    {
        subClassTemplate = string;
    }


    public ReferencedModels createReferencedModels()
    {
        if (referencedModels == null)
        {
            referencedModels = new ReferencedModels(getProject());
        }
        
        return referencedModels;
    }


    /**
     * @return list of models referenced by model() 
     */
    public ReferencedModels referencedModels()
    {
        return referencedModels;
    }
    
    
    /**
     * @return list of names of the Entities that files should be generated for
     */
    public String entities()
    {
        return entities;
    }

    /**
     * @param string list of names of the Entities that files should be generated for
     */
    public void setEntities(String string)
    {
        entities = string;
    }

    /**
     * @return <code>true</code> if in debug mode
     */
    public boolean isDebugging()
    {
        return isDebugging;
    }

    /**
     * @param aBoolean <code>true</code> if in debug mode
     */
    public void setIsDebugging(boolean aBoolean)
    {
        isDebugging = aBoolean;
    }

    /**
     * @return dictionary of additional key-value pairs to be available during generation
     */
    public Hashtable defines()
    {
        return defines;
    }


}
