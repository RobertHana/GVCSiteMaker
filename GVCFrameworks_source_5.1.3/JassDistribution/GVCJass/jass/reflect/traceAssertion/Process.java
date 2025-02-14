/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

import java.io.*;
import java.util.*;
import jass.GlobalFlags;
import jass.parser.JassCompilationUnit;
import jass.reflect.Class;    // to avoid conflicts with java.lang.Class
import jass.reflect.*;
import jass.runtime.traceAssertion.*;
import jass.runtime.util.*;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class Process implements FileBounded {
    public final static String MAIN = "MAIN";
    public final static String FIELDNAME_PARENT = "parent";
    public final static String FIELDNAME_ALPHABET = 
	TraceAlphabet.FIELDNAME_ALPHABET;
    public final static String FIELDNAME_REFEREDPROCESSES = 
	"referedProcesses";
    public final static String FIELDNAME_SOURCE = "source";
    public final static String METHODNAME_CONSTRUCTPROCESS = 
	"constructProcess()";

    // fields with valid values before reflection
    SetOfCommunications	       alphabet;
    String		       classidentifier;
    Vector		       codeProducers;
    ProcessDeclarator	       declarator;
    ProcessExpression	       expression;
    Class		       mappedClass;
    TraceAssertionExpression   parent;
    Source		       source;

    // fields with valid values after reflection
    HashSet		       processCalls;

    /**
     * Constructor declaration
     *
     *
     * @param parent
     * @param declarator
     * @param classidentifier
     *
     * @see
     */
    public Process(TraceAssertionExpression parent, 
		   ProcessDeclarator declarator, String classidentifier) {
	this.parent = parent;
	this.declarator = declarator;
	this.classidentifier = classidentifier;
	this.source = new Source(declarator.getContainer().getName(), 
				 declarator.getLine());

	if (parent.getProcess(getName()) != null) {
	    throw new ReflectExpressionError("Process name '" + getName() 
					     + "' is used twice", this);
	} 

	alphabet = new SetOfCommunications();
	codeProducers = new Vector();
	processCalls = new HashSet();

	// Get the class object from the ClassPool (class is in state unreflected)
	String mappedClassname = getMappedPackagename() + "." 
				 + getMappedClassidentifier();

	mappedClass = new Class(mappedClassname);

	ClassPool.addClass(mappedClass);

	if (mappedClass == null) {}
    }

    /**
     * Method declaration
     *
     *
     * @param codeProducer
     *
     * @see
     */
    public void addCodeProducer(CodeProducer codeProducer) {
	codeProducers.add(codeProducer);
    } 

    /**
     * Method declaration
     *
     *
     * @param processCall
     *
     * @see
     */
    public void addProcessCall(ProcessCall processCall) {
	processCalls.add(processCall);
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
	parent.addToAlphabet(communications);
	alphabet.add(communications);
    } 

    /**
     * Method declaration
     *
     *
     * @param expression
     *
     * @see
     */
    public void announceCommunication(CommunicationExpressions expression) {
	alphabet.add(expression.getCommunications());
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public SetOfCommunications getAlphabet() {
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
    public Class getContainer() {
	return declarator.getContainer();
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public SetOfMethods getInitialCommunications() {
	return expression.getInitialCommunications();
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public int getLine() {
	return declarator.getLine();
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
	String classname = parent.getMappedClassidentifier() + "_" 
			   + getName();

	// System.out.println("mapped classname: " + mappedClassname);
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
    public ProcessCall[] getProcessCalls() {
	ProcessCall[] processCallArray = new ProcessCall[processCalls.size()];

	processCalls.toArray(processCallArray);

	return processCallArray;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String[] getReferedProcessnames() {
	HashSet  nameSet = new HashSet();
	Iterator callIterator = processCalls.iterator();

	while (callIterator.hasNext()) {
	    ProcessCall processCall = (ProcessCall) callIterator.next();

	    nameSet.addAll(Arrays.asList(processCall.getNames()));
	} 

	String[] names = new String[nameSet.size()];

	nameSet.toArray(names);

	return names;
    } 

    /*
     * public boolean isCommunicationGuarded(ProcessExpression caller)
     * {
     * return expression.isCommunicationGuarded(caller);
     * }
     */

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String createJavaFile() {
	String lineIndent = ToJava.INDENT1;
	String packagename = mappedClass.getPackageName();
	String classname = mappedClass.getName();
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

	// package declaration
	writer.println("package " + packagename + ";");
	writer.println();

	// class declaration
	writer.println(Modifier.toJava(mappedClass.getModifier()) + " class " 
		       + getMappedClassidentifier() + " extends " 
		       + mappedClass.getSuperclass().getName());
	writer.println("{");

	// field declaration
	Field[] variables = mappedClass.getDeclaredFields();

	for (int iVariable = 0; iVariable < variables.length; ++iVariable) {
	    Field variable = variables[iVariable];

	    writer.println(lineIndent + variable.toJava() + ";");
	} 

	writer.println();

	// source of mappedClass
	writer.println(lineIndent + "public static final " + Source.CLASSNAME 
		       + " " + FIELDNAME_SOURCE + " = " + source.toJava() 
		       + ";");
	writer.println(lineIndent + "public " + Source.CLASSNAME 
		       + " getSource()" + "{return " + FIELDNAME_SOURCE 
		       + ";}");
	writer.println();

	// parent: mapped class of trace assertion
	String parentClassname = parent.getMappedClassname();

	writer.println(lineIndent + "public static final String " 
		       + FIELDNAME_PARENT + " = \"" 
		       + parent.getMappedClassname() + "\";");
	writer.println(lineIndent + "public String getParentClassname()" 
		       + "{return " + FIELDNAME_PARENT + ";}");
	writer.println();

	// referedProcesses
	writer.println(lineIndent + "public static final String[] " 
		       + FIELDNAME_REFEREDPROCESSES + " = new String[]{");
	writer.print(ToJava.INDENT2);

	String[] references = getReferedProcessnames();

	for (int iReference = 0; iReference < references.length; 
		++iReference) {
	    String referedProcessname = references[iReference];

	    writer.print("\"" + referedProcessname + "\"");

	    if (iReference + 1 < references.length) {
		writer.print(", ");
	    } 
	} 

	writer.println();
	writer.println(lineIndent + "};");
	writer.println(lineIndent + "public String[] getReferedProcesses()" 
		       + "{return " + FIELDNAME_REFEREDPROCESSES + ";}");
	writer.println();

	// alphabet
	writer.println(lineIndent + "public static final " 
		       + jass.runtime.traceAssertion.SetOfMethods.CLASSNAME 
		       + " " + FIELDNAME_ALPHABET + " = ");
	writer.println(getAlphabet().toJava(lineIndent + "  "));
	writer.println(lineIndent + ";");
	writer.println(lineIndent + "public " 
		       + jass.runtime.traceAssertion.SetOfMethods.CLASSNAME 
		       + " getAlphabet(){return " + FIELDNAME_ALPHABET 
		       + ";}");
	writer.println();

	// method: toString()
	String newline = "System.getProperty(\"line.separator\")";

	writer.println(ToJava.INDENT1 + "public String toString()");
	writer.println(ToJava.INDENT1 + "{");
	writer.println(ToJava.INDENT2 + "String string = \"\";");
	writer.println(ToJava.INDENT2 + "string += \"Process name: " 
		       + declarator.getName() + "\" + " + newline + ";");
	writer.println(ToJava.INDENT2 + "string += " + newline + ";");
	writer.println(ToJava.INDENT2 + "string += \"Variables:\" + " 
		       + newline + ";");

	for (int iVariable = 0; iVariable < variables.length; ++iVariable) {
	    Field variable = variables[iVariable];

	    writer.println(ToJava.INDENT2 + "string += \"" 
			   + variable.getName() + " : \" + " 
			   + variable.getName() + " + " + newline + ";");
	} 

	writer.println(ToJava.INDENT2 + "return string;");
	writer.println(ToJava.INDENT1 + "}");
	writer.println();

	// constructors : used by ProcessCall
	writer.println(declarator.toJava());
	writer.println();

	// methods for process calls
	ProcessCall[] referers = getProcessCalls();

	for (int iReferer = 0; iReferer < referers.length; ++iReferer) {
	    ProcessCall referer = referers[iReferer];

	    writer.println(referer.toJava());
	} 

	writer.println();

	// method declaration : automaton
	ProcessFactory automaton = expression.constructAutomaton(null);

	/*
	 * String[] referedProcessnames = new String[referedProcesses.size()];
	 * for(int iReferer = 0; iReferer < referers.length; ++iReferer)
	 * {
	 * String processname = referers[iReferer].getName();
	 * referedProcessnames[iReferer] = processname;
	 * }
	 */
	automaton.finishProcessCreation(getName(), 

	// expression.getRuntimeCommunications(),
	new SetOfMethods(expression.getInitialCommunications())

	// referedProcessnames
	);
	writer.println(lineIndent + "public " 
		       + jass.runtime.traceAssertion.Process.CLASSNAME + " " 
		       + METHODNAME_CONSTRUCTPROCESS);
	writer.println(lineIndent + "{");
	writer.println(automaton.toJava(ToJava.INDENT2));
	writer.println(ToJava.INDENT2 + "return " + automaton.codeProcess() 
		       + ";");
	writer.println(lineIndent + "}");

	// print code of several code producers
	writer.println();

	Iterator codeIterator = codeProducers.iterator();

	while (codeIterator.hasNext()) {
	    CodeProducer codeProducer = (CodeProducer) codeIterator.next();

	    writer.println(codeProducer.toJava());
	    writer.println();
	} 

	/*
	 * // method declaration : java blocks
	 * 
	 * ProcessJavaBlock[] javaBlocks = expression.getJavaBlocks();
	 * for(int iBlock = 0; iBlock < javaBlocks.length; ++iBlock)
	 * {
	 * ProcessJavaBlock javaBlock = javaBlocks[iBlock];
	 * String javaCode = javaBlock.toJava();
	 * 
	 * writer.print(javaCode);
	 * writer.println();
	 * }
	 */
	writer.println("}");
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
    public String getName() {
	return declarator.getName();
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public TraceAssertionExpression getParent() {
	return parent;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflect() {
	if (GlobalFlags.VERBOSE > 2) {
	    System.out.println("Message: Reflecting process " 
			       + declarator.getName());
	} 

	// a process equation must allways end with a basic process or with a
	// (communication guarded) process call (see ProcessPrefixExpression)
	if (!expression.isCorrectProcessClosure()) {
	    throw new SemanticError("Process equation must either end with a basic process or a process call", 
				    expression);
	} 

	declarator.reflect();
	expression.reflect();

	if (GlobalFlags.VERBOSE > 2) {
	    System.out.println("Message: Ended reflecting " 
			       + declarator.getName());
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param expression
     *
     * @see
     */
    public void setExpression(ProcessExpression expression) {
	this.expression = expression;

	expression.setParent(this);
    } 

    /**
     * Method declaration
     *
     *
     * @param clazz
     *
     * @see
     */
    public void setMappedClass(Class clazz) {
	this.mappedClass = clazz;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

