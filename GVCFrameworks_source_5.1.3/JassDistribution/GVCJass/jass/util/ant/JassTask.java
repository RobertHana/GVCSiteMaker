/*
 * Jass Ant task.
 *
 * Copyright (C) 1999-2002 by Semantics Group, University of Oldenburg.
 *
 * $Id: JassTask.java,v 1.0, 2003-09-05 23:02:36Z, Sacha Mallais$
 */
package jass.util.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.util.*;
import org.apache.tools.ant.types.*;

import java.io.*;
import java.util.*;

import jass.*;
import jass.util.SourceFile;
import jass.commandline.CommandlineParser;

/**
 * <p> A Jass Ant task that executes Jass as a subprocess that translates
 * annotated .jass or .java files into instrumented .java files. </p>
 * 
 * <p> This task does not compile the resulting .java files; that is the
 * responsibility of a different Ant javac target. </p>
 *
 * <p> This task understands the following Ant tags: </p>
 * <dl>
 * <dt> <code>src</code>
 * <dd> the source path from which we select .java or .jass files to
 * instrument.
 * <dt> <code>destdir</code>
 * <dd> the destination directory for the instrumented file(s).  This (or the 
 * dst tag) must be used when the source files have .java rather  than .jass 
 * suffixes.
 * <dt> <code>dst</code></dt>
 * <dd> the destination path for the instrumented file.  This (or the 
 * destdir attribute) must be used when the source files have .java rather 
 * than .jass suffixes. The path must contain only one element specifying
 * the destination directory. This will is overridden by a possible destdir 
 * attribute!
 * <dt> <code>classpath</code>
 * <dd> the classpath from which we classload during instrumentation.
 * <dt> <code>quiet</code>
 * <dd> a flag indicating that instrumentation should produce no output.
 * <dt> <code>normal</code>
 * <dd> a flag indicating that instrumentation should produce normal
 * amounts of output (progress indication).
 * <dt> <code>dataflow</code>
 * <dd> a flag indicating that Jass should perform dataflow analysis.
 * <dt> <code>depend</code>
 * <dd> a flag indicating that Jass should produce an instrumented version
 * of the parent class of all classes Jass is instrumenting (those that are
 * in the source path).  Note that such analysis and instrumentation often
 * issues warnings when the parent classes are part of a reusable library
 * (e.g., JDK core classes).
 * </dl>
 *
 * @author Mark Broerkens <Mark.Broerkens@informatik.uni-oldenburg.de>
 * @author Michael Moeller <Michael.Moeller@informatik.uni-oldenburg.de>
 * @author Joseph Kiniry <kiniry@acm.org>
 *
 * @history Note that the semantics of the "depend" switch match those of
 * Jass 2.0.8; depend only implies that parent classes need be evaluated
 * for automatic translation.
 *
 * @design eagle - I don't know when this class is created. So it is
 * unclear when to reset the GlobalFlags. It should happen before each
 * run of Jass, but also before option parsing.
 *
 * @see <a href="http://jakarta.apache.org/ant/">The Ant homepage</a>
 */
public class JassTask extends MatchingTask {

  // Attributes

  // Constants used to denote failure messages and verbose levels.
    private static final String FAIL_MSG 
        = "Compile failed, messages should have been provided.";
    private static final String VERBOSE_NORMAL = "NORMAL";
    private static final String VERBOSE_QUIET  = "QUIET";
    private static final String VERBOSE_VERBOSE= "VERBOSE";
    
    private static final String DOCMODE_JAVA = "JAVA";
    private static final String DOCMODE_HTML = "HTML";
    private static final String DOCMODE_NONE = "NONE";
    
    private static final JassFileMapper jassFileMapper = new JassFileMapper();
    
    // The path to the source code ("src" Ant element).
    private Path src;

    // The classpath ("classpath" Ant element)
    private Path classpath;

    // The destination directory ("dst" Ant element)
    private Path destination_dir;

    private String contract = null;

    /**
     * The list of files that is to be compiled. The Vector contains 
     * objects of class jass.util.SourceFile
     */
    protected Vector compileList = new Vector();
    

    /**
     * <p> Construct a new Jass task. </p>
     *
     * <p> We call Jass via its Java interface 
     * <code>jass.Jass.start(SourceFile)</code>. Since Jass uses a lot
     * of static information for configuration we reset this global
     * flags.</p>
     */
    public JassTask() {
	GlobalFlags.reset();
    }


    // Public methods

    // @design - Note that all of these methods below with regards to path
    // manipulation are named in a precise fashion so that Ant's
    // introspection mechanism can find them.  Do not rename them unless you
    // know what you are doing.
    
    // Source path handling.  


    /**
     * Create a nested <src ...> element for multiple source path
     * support.
     *
     * @return a nexted Ant src (source) element.
     */
    public Path createSrc() {
        if (src == null) {
            src = new Path(project);
        }
       return src.createPath();
    }

    /**
     * Set the source dirs to find the source Java files.
     *
     * @param srcDir  the new path to add to our existing source path.
     */
    public void setSrcdir(Path srcDir) {
        if (src == null) {
            src = srcDir;
        } else {
            src.append(srcDir);
        }
    }

    /** @return the source dirs to find the source java files. **/
    public Path getSrcdir() {
        return src;
    }        
    
    
    /** 
     * Set the destination directory in which instrumented files will be
     * written.
     *
     * @param destDir  the destination directory for the instrumented 
     *                 Java files.
     */
    public void setDestdir(String destDir) {
    	GlobalFlags.destinationDir = new File(destDir);
    }

    /** 
     * @return  the destination directory in which instrumented files will 
     * be written or <code>null</code> if not set yet.
     */
    public String getDestdir(String destDir) {
    	if (GlobalFlags.destinationDir == null) {
    	    return null;
    	}
    	return GlobalFlags.destinationDir.toString();
    }

    /**
     * The destination path must be only a single directory.
     *
     * @return a nested Ant src (source) element.
     */
    public Path createDst() {
	if (destination_dir == null) {
	    destination_dir = new Path(project);
	}
	return destination_dir.createPath();
    }

    /**
     * Set the destination directory in which instrumented files will be
     * written.
     *
     * @param newDestinationPath the new destination path.
     **/
    public void setDstdir(Path newDestinationPath) {
	destination_dir = newDestinationPath;
    }

    /** 
     * @return  the destination directory in which instrumented files will 
     * be written. 
     */
    public Path getDstdir() {
	return destination_dir;
    }
        

    // Classpath handling

    /**
     * Maybe creates a nested classpath element.
     *
     * @return the current classpath.
     */
    public Path createClasspath() {
        if (classpath == null) {
            classpath = new Path(project);
        }
        return classpath.createPath();
    }
    /**
     * Set the classpath to be used for this compilation.
     *
     * @param classpath  the new path to add to our existing classpath.
     */
    public void setClasspath(Path classpath) {
        if (this.classpath == null) {
            this.classpath = classpath;
        } else {
            this.classpath.append(classpath);
        }
    }

    /**
     * Adds a reference to a <code>CLASSPATH</code> defined elsewhere.
     *
     * @param r the refererence to the classpath.
     */
    public void setClasspathRef(Reference r) {
        createClasspath().setRefid(r);
    }

    /** @return the classpath to be used for this compilation. */
    public Path getClasspath() {
        return classpath;
    }


        
  // Micellaneous flag handling.

    /**
     * Set the depend flag.
     *
     * @param depend the new value of the depend flag.
     */
    public void setDepend(boolean depend) {
        GlobalFlags.DEPEND = depend;
    }

    /** @return the depend flag. */
    public boolean getDepend() {
        return GlobalFlags.DEPEND;
    }

    /**
     * Set the dataflow flag.
     *
     * @param dataflow  the new value of the dataflow flag.
     */
    public void setDataflow(boolean dataflow) {
        GlobalFlags.ANALYSE_DATAFLOW = dataflow;
    }

    /** @return the dataflow flag's value. */
    public boolean getDataflow() {
        return GlobalFlags.ANALYSE_DATAFLOW;
    }
    
    /**
     * Set the verbose flag.
     *
     * @param verbose the new value of the verbose flag.
     */
    public void setVerbose(String verbose) {
        if (verbose.equalsIgnoreCase(VERBOSE_VERBOSE) || 
	    verbose.equalsIgnoreCase("true")) {
	    GlobalFlags.VERBOSE=3;
        } else if (verbose.equalsIgnoreCase(VERBOSE_NORMAL)) {
	    GlobalFlags.VERBOSE=1;
        } else if (verbose.equalsIgnoreCase(VERBOSE_QUIET) || 
		   verbose.equalsIgnoreCase("false")) {
	    GlobalFlags.VERBOSE=0;
        }
    }

    /** @return the verbose flag's value. **/
    public String getVerbose() {
	switch (GlobalFlags.VERBOSE) {
	case 3: return VERBOSE_VERBOSE;
	case 1: return VERBOSE_NORMAL;
	case 0: return VERBOSE_QUIET;
	default: return "";
	}
    }
    
    /**
     * Set the docmode flag.
     *
     * @param verbose the new value of the doc flag.
     */
    public void setDocmode(String mode) {
        if (mode.equalsIgnoreCase(DOCMODE_JAVA) || 
	    mode.equalsIgnoreCase("true")) {
	    GlobalFlags.JD_MODE=GlobalFlags.JD_JAVADOC;
        } else if (mode.equalsIgnoreCase(DOCMODE_HTML)) {
	    GlobalFlags.JD_MODE=GlobalFlags.JD_HTML;
        } else if (mode.equalsIgnoreCase(DOCMODE_NONE) || 
		   mode.equalsIgnoreCase("false")) {
	    GlobalFlags.JD_MODE=GlobalFlags.JD_NOTHING;
        }
    }

    /** @return the verbose flag's value. **/
    public String getDocmode() {
	switch (GlobalFlags.JD_MODE) {
	case GlobalFlags.JD_JAVADOC: return DOCMODE_JAVA;
	case GlobalFlags.JD_HTML   : return DOCMODE_HTML;
	case GlobalFlags.JD_NOTHING: return DOCMODE_NONE;
	default: return "";
	}
    }
    
    /**
     * Sets the compile controls for contract compilation mode
     * value.
     *
     * @param contract  the new value for the compile controls.
     */
    public void setContract(String contract) {
        this.contract = contract;
    }
    
    /** @return the contract flag's value. **/
    public String getContract() {
        return this.contract;
    }

    /**
     * Executes the task.
     */
    public void execute() throws BuildException {
	// First, make sure that we've got a source directory.
        if (src == null) {
            throw new BuildException("srcdir attribute must be set!", location);
        }
        String [] list = src.list();
        if (list.length == 0) {
            throw new BuildException("srcdir attribute must be set!", location);
        }

        // set the options for the compile controls
        if (contract!=null) {
	    try {
		CommandlineParser.parseCompilationOptions(contract);
		GlobalFlags.MODE = GlobalFlags.CONTRACT;
	    } catch (Exception e) {
		throw new BuildException(e.getMessage());
	    }
        } 

        // scan source directories and dest directory to build up 
        // compile lists
        resetFileLists();
	if (GlobalFlags.destinationDir == null && destination_dir != null) {
	    GlobalFlags.destinationDir = 
		(File)project.resolveFile(getDstdir().toString());
	}

        for (int i=0; i<list.length; i++) {
            File srcDir = (File)project.resolveFile(list[i]);
            if (!srcDir.exists()) {
                throw new BuildException("srcdir \"" + srcDir.getPath() + "\" does not exist!", location);
            }
            DirectoryScanner ds = this.getDirectoryScanner(srcDir);
            String[] files = ds.getIncludedFiles();
	    if (GlobalFlags.destinationDir != null) {
		scanDir(srcDir, GlobalFlags.destinationDir, files);
	    } else {
		scanDir(srcDir, srcDir, files);
	    }
        }
        
        if (compileList.size() > 0) {
	    Path classpath = (Path) src.clone();
	    if (getClasspath() != null) {
		classpath.append(getClasspath());
	    }
	    System.setProperty("jass.class.path", classpath.toString());
	    log("Precompiling " + compileList.size() + 
		" source file"
		+ (compileList.size() == 1 ? "" : "s"));
	    Enumeration toCompile = compileList.elements();
	    SourceFile next;
	    
	    PrintStream systemErr = System.err;
	    PrintStream systemOut = System.out;
	    PrintStream jassOutput = 
		new PrintStream(new LogOutputStream(this, Project.MSG_INFO));
	    System.setErr(jassOutput);
	    System.setOut(jassOutput);
	    while (toCompile.hasMoreElements()) {
		try {
		    next = (SourceFile)toCompile.nextElement();
		    Jass.start(next);
		}
		catch (Throwable thrown) {
		    System.setErr(systemErr);
		    System.setOut(systemOut);
		    log(thrown.getMessage());
		    throw new BuildException("Jass failed.");
		}
	    }
	    System.setErr(systemErr);
	    System.setOut(systemOut);
        }
    }
    
    
    /**
     * Clear the list of files to be compiled and copied.. 
     */
    protected void resetFileLists() {
        compileList.removeAllElements();
    }

    /**
     * Scans the directory looking for source files to be compiled.  
     * The results are returned in the class variable compileList
     */
    protected void scanDir(File srcDir, File destDir, String files[]) {
        SourceFileScanner sfs = new SourceFileScanner(this);
        File[] newFiles = sfs.restrictAsFiles(files, srcDir, destDir, 
					      jassFileMapper);
        
        for (int i = 0; i < newFiles.length; i++) {
	    File next = newFiles[i];
            compileList.add(new SourceFile(next.toString()));
        }
     }

}
