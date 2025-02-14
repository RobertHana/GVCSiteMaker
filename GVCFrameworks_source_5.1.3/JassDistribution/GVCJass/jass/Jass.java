/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass;

import java.io.*;
import java.util.Vector;
import jass.commandline.CommandlineParser;
import jass.commandline.Help;
import jass.parser.*;
import jass.reflect.Class;
import jass.reflect.*;
import jass.reflect.traceAssertion.*;
import jass.runtime.util.FileTools;
import jass.util.*;
import jass.visitor.*;

/**
 * Main class of the Jass-Compiler.
 * @author Michael Plath, Michael Moeller
 */
public class Jass {

    /*
     * ....................................................................
     * . Fields                                                           .
     * ....................................................................
     */

    /** 
     * store the current version String of Jass. You don't have to modify
     * this String - it is done by a script whenever creating a new version
     */
    public static final String  VERSION = "2.0.11";
    private static JassParser   parser;
    private static NameAnalysis nameAna = new NameAnalysis();

    /**
     * Size of the buffer used for the copy mode.
     */
    private static final int    BUFFER_SIZE = 4096; 

    /*
     * ....................................................................
     * . Private Methods                                                  .
     * ....................................................................
     */

    /**
     * Perform an interference check on the given file. The filename must
     * end with the Java- (<code>.java</code>) or Jass-extension
     * (<code>.jass</code>)
     *
     * @param filename the name of the file to check
     *
     */
    private static void checkInterference(SourceFile filename) {
	if (GlobalFlags.VERBOSE == 0) {
	    System.out.println("Warning: Option NORMAL is set automatically.");

	    GlobalFlags.VERBOSE = 1;
	} 

	try {
	    if (! filename.hasUnknownExtension()) {
		FileInputStream sourceStream = 
		    new java.io.FileInputStream(filename);

		initiateNewParserRun(filename, sourceStream);

		if (GlobalFlags.VERBOSE > 0) {
		    System.out.println("Message: Interference check for file " 
				       + filename + " ...");
		} 

		// parse the file with the startsymbol CompilationUnit ...
		JassCompilationUnit unit = parser.CompilationUnit();

		// get a ReflectVisitor for the class and reflect it ...
		ReflectVisitor      refVisitor = new ReflectVisitor();
		Class		    clazz = (Class) unit.jjtAccept(refVisitor, 
			null);

		// most informations are retrieved, but dome things
		// must yet be done ...
		clazz.reflect();

		// close inputstream
		sourceStream.close();

		Method _main = clazz.getMethod("main(java.lang.String[])");

		if (_main == null) {
		    System.err.println("Error: Interference can only be check for classes with \"main\"-method.");
		    System.exit(1);
		} 

		jass.reflect.DataFlow df = new jass.reflect.DataFlow();

		df.checkInterference(_main);
	    } else {
		System.err.println("Error: File must have extension " + "\"." 
				   + SourceFile.JASS_EXTENSION + "\" or \"." 
				   + SourceFile.JAVA_EXTENSION + "\".");
		System.exit(1);
	    } 
	} catch (Throwable exception) {
	    exception.printStackTrace();
	    System.exit(1);
	} 
    } 

    /**
     * Compile a source-file of a class to the internal reflection object.
     *
     * @param filename the source file (including the Jass or Java extension)
     * @return the compiled class information
     * @exception Exception when an error occures during compilation (?)
     *
     * @see jass.reflect.Class
     */
    public static Class compileClass(SourceFile filename) throws Exception {
	if (GlobalFlags.VERBOSE > 0) {
	    System.out.println("Message: Compiling file " + filename 
			       + " ...");
	} 

	if (!filename.isDestionationAvailable(GlobalFlags.destinationDir)) {
	    System.err.
		println("Error: File must have extension \"." 
			+ SourceFile.JASS_EXTENSION + 
			"\" or a destination directory must be provided.");
	    System.exit(1);
	    return null;
	} else {
	    FileInputStream sourceStream = 
		new java.io.FileInputStream(filename);

	    // compile the file ...
	    initiateNewParserRun(filename, sourceStream);

	    // parse the file with the startsymbol CompilationUnit ...
	    JassCompilationUnit unit = parser.CompilationUnit();

	    // get a ReflectVisitor for the class and reflect it ...
	    ReflectVisitor      refVisitor = new ReflectVisitor();
	    Class		clazz = (Class) unit.jjtAccept(refVisitor, 
							       null);

	    // most informations are retrieved, but dome things must
	    // yet be done ...
	    clazz.reflect();

	    // close inputstream
	    sourceStream.close();

	    // open output stream
	    SourceFile newFile = 
		filename.createDestination(GlobalFlags.destinationDir,
					   clazz.getPackageName());
	    newFile.createSubdirs();
	    FileOutputStream targetStream = null;
	    try {
		targetStream = new FileOutputStream(newFile);
	    } catch (IOException e) {
		System.err.println("Error: File " + newFile 
				   + " could not be opened for writing. " 
				   + "Maybe file is write protected.");
		System.exit(1);
	    } 

	    // get OutputVisitor to generate code ...
	    OutputVisitor outVisitor = 
		new OutputVisitor(new PrintWriter(targetStream, true), clazz);

	    // start writing the output ...
	    unit.jjtAccept(outVisitor, null);

	    // close output
	    targetStream.close();
	    ClassPool.markAsCompiled(clazz);

	    // all classes of an alphabet of a trace assertion must be
	    // compiled too
	    TraceAssertion traceAssertion = clazz.getTraceAssertion();

	    if (traceAssertion != null) {

		// A trace assertion consists of a set of assertion expressions
		TraceAssertionExpression[] traceAssertionExpressions = 
		    traceAssertion.getExpressions();

		for (int iExpression = 0; 
			iExpression < traceAssertionExpressions.length; 
			++iExpression) {
		    TraceAssertionExpression traceAssertionExpression = 
			traceAssertionExpressions[iExpression];
		    TraceAlphabet	     alphabet = 
			traceAssertionExpression.getAlphabet();
		    Class[]		     referedClasses = 
			alphabet.getClasses();

		    for (int iClass = 0; iClass < referedClasses.length; 
			    ++iClass) {
			Class	     referedClass = referedClasses[iClass];
			String       classname = referedClass.getName();
			SourceFile[] sourceFiles = 
			    NameAnalysis.getSourceFilesForClassName(classname);

			if (sourceFiles.length == 0) {
			    System.err.println("Error: Source file of class " 
					       + classname + " not found");
			    System.exit(1);
			} 

			ClassPool.addClassToCompile(referedClass);
		    } 
		} 
	    } 

	    // return the class
	    return clazz;
	} 
    } 

    /**
     * For comiler option "-nothing" only copy the .jass-file to a 
     * .java-file.
     *
     * @param filename the .jass-sourcefile
     *
     * @return null (no information is retrieved)
     *
     * @exception Exception when an error occures during copying (?)
     */
    public static Class copyFile(SourceFile filename) throws Exception {

	// only copy the file
	FileInputStream sourceStream = new java.io.FileInputStream(filename);

	// no depend option should be used !
	if (GlobalFlags.VERBOSE > 0 && GlobalFlags.DEPEND) {
	    System.out.println("Warning: DEPEND-Option ignored in copy mode !");
	} 

	String thePackage = null;
	if (GlobalFlags.destinationDir != null) {
	    //TODO: extract the package information from the given file.
	    thePackage = filename.guessPackage();
	}
	    
	SourceFile newFile = 
	    filename.createDestination(GlobalFlags.destinationDir,
				       thePackage);
	newFile.createSubdirs();
	FileOutputStream targetStream = null;
	// can file be opened ?
	try {
	    targetStream = new FileOutputStream(newFile);
	} catch (IOException e) {
	    System.err.println("Error: File " + newFile 
			       + " could not be opened for writing. " 
			       + "Maybe file is write protected.");
	    System.exit(1);
	} 

	if (GlobalFlags.VERBOSE > 0) {
	    System.out.println("Message: Copying " + filename + " -> " 
			       + newFile + " ...");
	} 

	// copy via buffer, idea: BufferedStream ?
	byte[] buffer = new byte[BUFFER_SIZE];
	int    bytes_read;

	do {
	    bytes_read = sourceStream.read(buffer);

	    if (bytes_read > 0) {
		targetStream.write(buffer, 0, bytes_read);
	    } 
	} while (bytes_read > 0);

	// close everything
	targetStream.close();
	sourceStream.close();

	// no informations retrieved: return null
	return null;
    } 

    /**
     * Prepare a new parser run for the given file. If no parser exits (this
     * is the first run) a new one will be created and the ClassPool will
     * be initialized, else the parser will be reinitialized.
     *
     * @param filename      the filename of the file to parse
     * @param sourceStream  the input stream of that file
     *
     * @see jass.reflect.ClassPool
     */
    private static void initiateNewParserRun(SourceFile filename, 
					     FileInputStream sourceStream) {

	// if there is no parser, create one and get a new ClassPool
	if (parser == null) {
	    parser = new JassParser(sourceStream);

	    ClassPool.initialize(parser);
	} else {
	    parser.ReInit(sourceStream);
	} 

	ClassPool.addClass(new Class(filename.guessClassName()));
	// System.out.println("#ToPool:" + classname);
    } 

	
    /**
     * Start the Jass-Tool. First this method will parse the commandline
     * and set the GlobalFlags. If that was successfull, the 
     * precompilation process will be initiated.
     *
     * @param argsv the commandline arguments
     *
     * @see parseCommandline(java.lang.String[])
     */
    public static void main(String[] args) {
	SourceFile[] files = null;
	try {
	    files = CommandlineParser.parse(args);;
	} catch (Exception e) {
	    if (! e.getMessage().equals("")) {
		System.err.println("Syntax error:");
		System.err.println(e.getMessage());
		System.err.println();
	    }
	    System.out.println(Help.USAGE);
	    System.out.println();
	    if (e.getMessage().equals("")) {
		System.exit(0);
	    } else {
		System.exit(1);
	    }
	} 

	switch (GlobalFlags.MODE) {
	case GlobalFlags.PRINT_HELP: {
	    Help.printHelp();
	    System.exit(0);
	} 
	case GlobalFlags.PRINT_VERSION: {
	    System.out.println("jass version \"" + VERSION + "\"");
	    System.exit(0);
	} 
	}

	// start the compilation
	for (int i = 0; i < files.length; i++) {
	    try {
		start(files[i]);
	    } catch (Throwable thrown) {
		thrown.printStackTrace();
		System.exit(1);
	    } 
	}

	if (GlobalFlags.VERBOSE > 0) {
	    System.out.println("Jass finished successfully");
	} 

	System.exit(0);
    } 


    /**
     * Start the compilation of a file according to the GlobalFlags.
     */
    public static void start(SourceFile filename) throws Throwable {
	// nodataflow && ( opt || interference ) ?
	if (!GlobalFlags.ANALYSE_DATAFLOW 
	    && GlobalFlags.MODE != GlobalFlags.NOTHING 
	    && (GlobalFlags.OPTIMIZING 
		|| GlobalFlags.MODE == GlobalFlags.INTERFERENCE)) {
	    if (GlobalFlags.VERBOSE > 0) {
		System.out.println("Warning: Option DATAFLOW is set automatically.");
	    } 

	    GlobalFlags.ANALYSE_DATAFLOW = true;
	} 

	switch (GlobalFlags.MODE) {

	case GlobalFlags.INTERFERENCE: {
	    // interference check
	    checkInterference(filename);
	    break;
	} 

	case GlobalFlags.NOTHING: {
	    copyFile(filename);
	    break;
	} 

	case GlobalFlags.CONTRACT:
	case GlobalFlags.WARNING: {
	    Class   clazz = compileClass(filename);
		
	    // repeat the process while there are classes to compile
	    while (ClassPool.moreClassesToCompile()) {
		Class  classToCompile = ClassPool.getClassToCompile();
		
		// get path of class
		SourceFile path = 
		    NameAnalysis
		    .expandFullPath(classToCompile.getName());
		compileClass(path); 
		//this will also call ClassPool.markAsCompiled()
		// so that we are not getting into an endless loop here. 
	    } 
	} 
	}
    }
}


/*--- formatting done in "Sun Java Convention" style on 03-06-2001 ---*/

