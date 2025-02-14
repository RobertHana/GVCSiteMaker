/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect.traceAssertion;

import jass.parser.JassParserConstants;
import jass.reflect.Class;    // to avoid conflicts with java.lang.class
import jass.reflect.*;
import jass.runtime.traceAssertion.*;
import jass.runtime.util.ToJava;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ProcessCall extends ProcessExpression {
    static int       nextIdNumber = 0;
    boolean	     checkCommunicationGuard;
    int		     idNumber;
    ReferedProcess[] references;

    /**
     * Constructor declaration
     *
     *
     * @param referedProcesses
     *
     * @see
     */
    public ProcessCall(ReferedProcess[] referedProcesses) {
	init(referedProcesses);
    }

    /**
     * Constructor declaration
     *
     *
     * @param referedProcess
     *
     * @see
     */
    public ProcessCall(ReferedProcess referedProcess) {
	init(new ReferedProcess[] {
	    referedProcess
	});
    }

    /**
     * Method declaration
     *
     *
     * @param referedProcesses
     *
     * @see
     */
    protected void init(ReferedProcess[] referedProcesses) {
	idNumber = nextIdNumber++;
	this.references = referedProcesses;
    } 

    /**
     * Method declaration
     *
     *
     * @param notUsed
     *
     * @return
     *
     * @see
     */
    public ProcessFactory constructAutomaton(ProcessFactory notUsed) {

	// The following statement tests whether a process call is communications guarded or not.
	// If it is not communications guarded the initial communications can not
	// be determined and an exception is thrown (see getInitialCommunications()).
	getInitialCommunications();

	// constrcut automaton
	ProcessFactory automaton;

	automaton = 
	    new PrimitiveProcessCall(new SetOfMethods(getInitialCommunications()), 
				     getMappedMethodname());

	return automaton;
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

	// IMPORTANT
	// if we check for a communication guard (see constructAutomaton()) an endless
	// loop can occur if there are cycle references.
	// Example: LOOP() { CALL LOOP() }
	// The initial communications of a process (LOOP) are determined by determing
	// the initial communications of the refered processes (CALL LOOP).
	// to avoid the endless loop we set a flag 'checkCommunicationGuard' which signals
	// that this method was invoked before. At the end of this method the flag is
	// set back.
	if (checkCommunicationGuard == true) {
	    throw new SemanticError("Process call must be action guarded", 
				    this);
	} 

	// method begins
	checkCommunicationGuard = true;

	TraceAssertionExpression belongingTraceAssertion = parent.getParent();
	SetOfMethods		 initialCommunications = new SetOfMethods();

	for (int iReference = 0; iReference < references.length; 
		++iReference) {
	    ReferedProcess reference = references[iReference];

	    initialCommunications.add(belongingTraceAssertion.getProcess(reference.getName()).getInitialCommunications());
	} 

	// method ends
	checkCommunicationGuard = false;

	return initialCommunications;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String[] getNames() {
	String[] names = new String[references.length];

	for (int iReference = 0; iReference < references.length; 
		++iReference) {
	    ReferedProcess reference = references[iReference];

	    names[iReference] = reference.getName();
	} 

	return names;
    } 

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void reflect() {
	for (int iReference = 0; iReference < references.length; 
		++iReference) {
	    ReferedProcess reference = references[iReference];

	    reference.reflect(parent.getMappedClass());
	} 

	parent.addProcessCall(this);

	// System.out.println("Reflected " + toString());
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isCorrectProcessClosure() {
	return true;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getMappedMethodname() {
	String methodname = "";

	methodname += "call_" + idNumber;

	return methodname;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String toJava() {
	String javaCode = "";
	String classnameMappedClass = 
	    jass.runtime.traceAssertion.MappedClass.CLASSNAME;

	javaCode += ToJava.INDENT1 + "public " + classnameMappedClass + "[]";
	javaCode += " " + getMappedMethodname() + "()" + ToJava.NEWLINE;
	javaCode += ToJava.INDENT1 + "{" + ToJava.NEWLINE;
	javaCode += ToJava.INDENT2 + "return new " + classnameMappedClass 
		    + "[]" + ToJava.NEWLINE;
	javaCode += ToJava.INDENT2 + "{" + ToJava.NEWLINE;

	for (int iReference = 0; iReference < references.length; 
		++iReference) {
	    ReferedProcess reference = references[iReference];

	    javaCode += 
		ToJava.INDENT3 + "new " 
		+ parent.getParent().getProcess(reference.getName()).getMappedClass().getName() 
		+ reference.getArgumentsAsJavaCode();

	    if (iReference + 1 < references.length) {
		javaCode += ",";
	    } 

	    javaCode += ToJava.NEWLINE;
	} 

	javaCode += ToJava.INDENT2 + "};" + ToJava.NEWLINE;
	javaCode += ToJava.INDENT1 + "}";

	return javaCode;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

