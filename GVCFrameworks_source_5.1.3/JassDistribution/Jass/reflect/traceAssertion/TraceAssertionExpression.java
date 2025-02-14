/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

import java.io.*;
import java.util.*;
import jass.GlobalFlags;
import jass.reflect.Class;
import jass.reflect.*;
import jass.runtime.util.ToJava;
import jass.runtime.traceAssertion.*;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class TraceAssertionExpression extends AssertionExpression {
    public static final String INTERNAL_LABEL_PREFIX = "NR";
    public static final String FIELDNAME_PROCESSES = "processes";
    static int		       idForInternalLabel = 0;
    TraceAlphabet	       alphabet;
    Vector		       alphabetListeners;
    SetOfCommunications	       communications;
    Class		       mappedClass;
    Hashtable		       processes;
    TraceAlphabet	       implicitAlphabet;
    boolean		       hasImplicitAlphabet = true;
    TraceAssertion	       parent;

    /**
     * Constructor declaration
     *
     *
     * @param parent
     * @param label
     *
     * @see
     */
    public TraceAssertionExpression(TraceAssertion parent, 
				    AssertionLabel label) {
	this.parent = parent;

	setLabel(label);

	alphabetListeners = new Vector();
	implicitAlphabet = new TraceAlphabet();
	processes = new Hashtable();

	// Get the class object from the ClassPool (class is in state unreflected)
	String mappedClassname = getMappedClassname();

	mappedClass = new Class(mappedClassname);

	ClassPool.addClass(mappedClass);
    }

    /**
     * Method declaration
     *
     *
     * @see
     */
    protected void checkProcessReferences() {
	Enumeration processEnumeration = processes.elements();

	while (processEnumeration.hasMoreElements()) {
	    Process       process = 
		(Process) processEnumeration.nextElement();
	    ProcessCall[] referers = process.getProcessCalls();

	    for (int iProcess = 0; iProcess < referers.length; ++iProcess) {
		ProcessCall referer = referers[iProcess];
		String[]    referedProcessnames = referer.getNames();

		for (int iName = 0; iName < referedProcessnames.length; 
			++iName) {
		    String referedProcessname = referedProcessnames[iName];

		    if (getProcess(referedProcessname) == null) {
			throw new ReflectExpressionError("Call refers undefined process", 
							 referer);
		    } 
		} 
	    } 
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    protected String createJavaFile() {
	String packagename = parent.getMappedPackagename();
	String classidentifier = getMappedClassidentifier();
	String classname = getMappedClassname();
	String filename = classname.replace('.', File.separatorChar) 
			  + ".java";

	if (GlobalFlags.VERBOSE > 2) {
	    System.out.println("Message: Creating file " + filename + " ...");
	} 

	FileOutputStream targetStream = null;

	try {
	    targetStream = new FileOutputStream(filename);
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	} 

	PrintWriter writer = new PrintWriter(targetStream);

	writer.println("package " + packagename + ";");
	writer.println();
	writer.println("public class " + classidentifier + " extends " 
		       + jass.runtime.traceAssertion.TraceAssertion.CLASSNAME);
	writer.println("{");

	// constants
	Field[] fields = getConstants();

	for (int iField = 0; iField < fields.length; ++iField) {
	    Field constant = fields[iField];

	    writer.println(ToJava.INDENT1 + constant.toJava() + ";");
	} 

	writer.println();

	// name
	writer.println(ToJava.INDENT1 + "public String getName(){return \"" 
		       + getLabel().getName() + "\";}");
	writer.println();

	// source
	writer.println(ToJava.INDENT1 + "public " + Source.CLASSNAME 
		       + " getSource(){return " 
		       + (new Source(getContainer().getName(), getLine())).toJava() 
		       + ";}");
	writer.println();

	// alphabet
	writer.println(getAlphabet().toJava(ToJava.INDENT1));
	writer.println();

	// mapping: processname to mapped classname
	writer.println(ToJava.INDENT1 + "static java.util.Hashtable " 
		       + FIELDNAME_PROCESSES 
		       + " = new java.util.Hashtable();");

	Enumeration processEnumeration = processes.elements();

	writer.println(ToJava.INDENT1 + "static");
	writer.println(ToJava.INDENT1 + "{");

	while (processEnumeration.hasMoreElements()) {
	    Process process = (Process) processEnumeration.nextElement();

	    writer.println(ToJava.INDENT2 + FIELDNAME_PROCESSES + ".put(" 
			   + "\"" + process.getName() + "\", \"" 
			   + process.getMappedClass().getName() + "\");");
	} 

	writer.println(ToJava.INDENT1 + "}");
	writer.println(ToJava.INDENT1 
		       + "public String getClassname(String processname)" 
		       + "{return (String) " + FIELDNAME_PROCESSES 
		       + ".get(processname);}");
	writer.println();
	writer.println("} // end of class");
	writer.close();

	if (GlobalFlags.VERBOSE > 2) {
	    System.out.println("Message: Ended file creation " + filename 
			       + ".");
	} 

	return filename;
    } 

    /**
     * Method declaration
     *
     *
     * @param process
     *
     * @see
     */
    public void add(Process process) {
	String processName;

	processName = process.getName();

	processes.put(processName, process);

	/*
	 * Object oldProcessReferedWithName = processes.put(processName, process);
	 * boolean processAllreadyDefined = oldProcessReferedWithName != null;
	 * if(processAllreadyDefined)
	 * {
	 * throw new ReflectExpressionError
	 * (
	 * "Process name '" + processName + "' is used twice",
	 * this
	 * );
	 * }
	 */
    } 

    /**
     * Method declaration
     *
     *
     * @param listener
     *
     * @see
     */
    public void addAlphabetListener(AlphabetListener listener) {
	alphabetListeners.add(listener);
    } 

    /**
     * Method declaration
     *
     *
     * @param communications
     *
     * @see
     */
    public void addToAlphabet(SetOfCommunications communications) {
	if (hasImplicitAlphabet) {
	    implicitAlphabet.add(communications);
	} else {
	    alphabet.add(communications);
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String[] createJavaFiles() {
	Vector filenameVector = new Vector();

	filenameVector.add(this.createJavaFile());

	Enumeration processEnumeration = processes.elements();

	while (processEnumeration.hasMoreElements()) {
	    Process process = (Process) processEnumeration.nextElement();
	    String  filename = process.createJavaFile();

	    filenameVector.add(filename);
	} 

	String[] filenames = new String[filenameVector.size()];

	filenameVector.toArray(filenames);

	return filenames;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public TraceAlphabet getAlphabet() {
	return alphabet;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public SetOfCommunications getCommunications() {
	return communications;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Field[] getConstants() {
	return mappedClass.getDeclaredFields();
    } 

    /**
     * Method declaration
     *
     *
     * @param name
     *
     * @return
     *
     * @see
     */
    public Process getProcess(String name) {
	return (Process) processes.get(name);
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Process[] getProcesses() {
	Process[] processArray = new Process[processes.size()];

	processes.values().toArray(processArray);

	return processArray;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getMappedClassidentifier() {
	String identifier = parent.getMappedClassidentifier() + "_" 
			    + getLabel().getName();

	return identifier;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Class getMappedClass() {
	return mappedClass;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getMappedClassname() {
	String classname = getMappedPackagename() + "." 
			   + getMappedClassidentifier();

	return classname;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getMappedPackagename() {
	return parent.getMappedPackagename();
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean hasExplicitAlphabet() {
	return !hasImplicitAlphabet;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflect() {
	if (GlobalFlags.VERBOSE > 2) {
	    System.out.println("Message: Reflecting trace assertion " 
			       + getLabel().getName());
	} 

	/*
	 * if(label != null)
	 * {
	 * label.reflect();
	 * }
	 */

	// alphabet reflection
	// An alphabet can be set explicitly or implicitly.
	// In case of an implicit alphabet, process reflection
	// determines alphabet.
	// An explicit alphabet must be reflected before process reflection to offer
	// all processes a valid alphabet for reflection.
	// In contrast an inplicit alphabet is reflected alter process reflection.
	if (hasExplicitAlphabet()) {
	    alphabet.setParent(this);
	    alphabet.reflect();
	} 

	// reflect processes
	Enumeration processEnumeration = processes.elements();

	while (processEnumeration.hasMoreElements()) {
	    Process process = (Process) processEnumeration.nextElement();

	    process.reflect();
	} 

	// after process reflection
	if (!hasExplicitAlphabet()) {
	    alphabet = implicitAlphabet;

	    alphabet.setParent(this);
	    alphabet.reflect();
	} 

	Iterator iterator = alphabetListeners.iterator();

	while (iterator.hasNext()) {
	    AlphabetListener listener = (AlphabetListener) iterator.next();

	    listener.announceAlphabet(alphabet);
	} 

	communications = alphabet.getCommunications();

	checkProcessReferences();

	if (GlobalFlags.VERBOSE > 2) {
	    System.out.println("Message: Ended reflecting " 
			       + getLabel().getName());
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public static String newInternalNameForLabel() {
	return INTERNAL_LABEL_PREFIX + idForInternalLabel++;
    } 

    /**
     * Method declaration
     *
     *
     * @param alphabet
     *
     * @see
     */
    public void setAlphabet(TraceAlphabet alphabet) {
	this.alphabet = alphabet;
	hasImplicitAlphabet = false;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

