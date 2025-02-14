/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Enumeration;
import jass.reflect.FormalParameter;
import jass.reflect.Method;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class CommunicationManager {
    public static final String  CLASSNAME = 
	"jass.runtime.traceAssertion.CommunicationManager";
    static boolean		disableContractChecking = false;
    public static boolean       internalAction = false;
    static final Object		callerRoot = new Object();
    static String		actualCaller;
    static ExecutionEntry       actualEntry;
    static ExecutionStack       executionStack;
    static final ExecutionEntry entryRoot = new ExecutionEntry(callerRoot, 
	    new MethodReference("", "", "", true));
    static Hashtable		runtimeObjects;
    static {
	executionStack = new ExecutionStack();
	runtimeObjects = new Hashtable();

	runtimeObjects.put(entryRoot.getCallerId(), 
			   new SetOfTraceAssertions());
    } 

    /**
     * Method declaration
     *
     *
     * @param caller
     * @param methodReference
     * @param parameters
     *
     * @see
     */
    public static synchronized void communicate(Object caller, 
						MethodReference methodReference, 
						Parameter[] parameters) {
	if (disableContractChecking) {
	    return;
	} else if (internalAction) {
	    return;
	} else {
	    disableContractChecking = true;
	} 

	// System.out.println("##CommunicationManager receives " + methodReference);
	if (caller == null) {
	    caller = callerRoot;
	    actualEntry = entryRoot;
	} 

	actualCaller = caller.getClass().getName() + "@" 
		       + Integer.toHexString(caller.hashCode());

	ExecutionEntry newEntry = new ExecutionEntry(caller, methodReference);

	// System.out.println("##" + methodReference + "##" + methodReference.isConstructorInvocation() + "##");
	if (methodReference.isBeginOfMethod()) {
	    executionStack.push(newEntry);

	    actualEntry = newEntry;
	} else {
	    if (actualEntry == entryRoot) {

		// We are at the end of the main method
		disableContractChecking = false;

		return;
	    } 

	    actualEntry = executionStack.pop();
	} 

	String callerId = actualEntry.getCallerId();

	if (methodReference.isConstructorInvocation()) {
	    addAssertions(caller, callerId, methodReference);
	} else if (executionStack.entryIsSupercall(actualEntry)) {
	    disableContractChecking = false;

	    return;
	} 

	// The following case can arise because of the super() method-call in every constructor.
	// These calls cannot be registered by the methodReference manager, because a
	// constructor invocation is detected after the super-call.
	if (runtimeObjects.get(callerId) == null) {
	    disableContractChecking = false;

	    return;
	} 

	HashSet checkedObjects = new HashSet();

	checkAssertions((SetOfTraceAssertions) runtimeObjects.get(callerId), 
			methodReference, parameters);
	checkedObjects.add(callerId);

	ExecutionEntry[] entries = executionStack.getEntries();
	int		 iEntry = entries.length - 1;

	if (methodReference.isBeginOfMethod()) {
	    --iEntry;
	} 

	while (iEntry > -1) {
	    ExecutionEntry       entry = entries[iEntry];
	    String		 objectId = entry.getCallerId();
	    SetOfTraceAssertions traceAssertionSet = 
		(SetOfTraceAssertions) runtimeObjects.get(objectId);

	    if (traceAssertionSet != null 
		&& !traceAssertionSet.isEmpty() 
		&& !checkedObjects.contains(objectId)) {
		checkAssertions(traceAssertionSet, methodReference, 
				parameters);
		checkedObjects.add(objectId);
	    } 

	    --iEntry;
	} 

	disableContractChecking = false;
    } 

    /**
     * Method declaration
     *
     *
     * @param caller
     * @param callerId
     * @param methodReference
     *
     * @see
     */
    public static void addAssertions(Object caller, String callerId, 
				     MethodReference methodReference) {
	if (methodReference.isBeginOfMethod()) {
	    String		 assertionClassname = 
		methodReference.getPackagename() + "." 
		+ TraceAssertion.CLASS_IDENTIFIERPREFIX 
		+ methodReference.getClassname();
	    SetOfTraceAssertions traceAssertions = 
		(SetOfTraceAssertions) runtimeObjects.get(callerId);

	    if (traceAssertions == null) {
		traceAssertions = new SetOfTraceAssertions();

		runtimeObjects.put(callerId, traceAssertions);
	    } 

	    Class assertionClass = null;

	    try {

		// System.out.println("Looking for:" + assertionClassname);
		assertionClass = Class.forName(assertionClassname);

		TraceAssertionManager traceAssertionManager = 
		    (TraceAssertionManager) assertionClass.newInstance();

		traceAssertions.add(traceAssertionManager);
	    } catch (ClassNotFoundException notUsed) {

		// do nothing
	    } catch (Throwable exception) {
		exception.printStackTrace();
		System.exit(1);
	    } 

	    if (assertionClass != null) {

		// System.out.println("  found!");
		String  filenameCaller = filename(caller.getClass());
		String  filenameAssertion = filename(assertionClass);
		File    fileCaller = new File(filenameCaller);
		File    fileAssertion = new File(filenameAssertion);
		Date    dateCaller = new Date(fileCaller.lastModified());
		Date    dateAssertion = 
		    new Date(fileAssertion.lastModified());
		boolean assertionMaybeDeprecated = 
		    dateCaller.after(dateAssertion);

		if (assertionMaybeDeprecated) {
		    long difference = dateCaller.getTime() 
				      - dateAssertion.getTime();
		    long differenceInMinutes = difference / (1000 * 60);

		    System.err.println("Warning: Trace Assertion class \"" 
				       + filenameAssertion 
				       + "\" maybe deprecated. It is " 
				       + difference + " ms (" 
				       + differenceInMinutes 
				       + " min) older than the belonging class \"" 
				       + filenameCaller + "\"");
		} 
	    } 
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param traceAssertionSet
     * @param methodReference
     * @param parameters
     *
     * @see
     */
    public static void checkAssertions(SetOfTraceAssertions traceAssertionSet, 
				       MethodReference methodReference, 
				       Parameter[] parameters) {
	TraceAssertionManager[] traceAssertions = 
	    traceAssertionSet.elements();

	for (int iAssertion = 0; iAssertion < traceAssertions.length; 
		++iAssertion) {
	    TraceAssertionManager traceAssertion = 
		traceAssertions[iAssertion];

	    try {
		traceAssertion.communicate(methodReference, parameters);
	    } catch (TraceAssertionException exception) {
		exception.printStackTrace();
		System.exit(1);
	    } 
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param clazz
     *
     * @return
     *
     * @see
     */
    public static String filename(Class clazz) {
	String filename;

	filename = 
	    clazz.getProtectionDomain().getCodeSource().getLocation().getFile() 
	    + clazz.getName().replace('.', java.io.File.separatorChar) 
	    + ".class";

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
    public static String getActualCaller() {
	return actualCaller;
    } 

    /*
     * public static void dumpTraceStack()
     * {
     * disableContractChecking = true;
     * 
     * TraceNode node = actualNode;
     * 
     * do
     * {
     * System.out.println(node.toString());
     * node = (TraceNode) node.getParent();
     * }
     * while(node != null);
     * disableContractChecking = false;
     * }
     * 
     * 
     * 
     * public static void dumpTraceStackAsPreorderEnumeration()
     * {
     * disableContractChecking = true;
     * 
     * Enumeration enumeration = nodeRoot.preorderEnumeration();
     * while(enumeration.hasMoreElements())
     * {
     * TraceNode node = (TraceNode) enumeration.nextElement();
     * System.out.println(node.toString());
     * }
     * disableContractChecking = false;
     * }
     */

    /**
     * Method declaration
     *
     *
     * @param clazz
     * @param method
     * @param parameters
     * @param beginOfMethod
     *
     * @return
     *
     * @see
     */
    public static String javaCodeCommunication(jass.reflect.Class clazz, 
					       jass.reflect.Method method, 
					       FormalParameter[] parameters, 
					       boolean beginOfMethod) {
	String[] parameterStrings = new String[parameters.length];

	for (int iParameter = 0; iParameter < parameters.length; 
		++iParameter) {
	    parameterStrings[iParameter] = parameters[iParameter].getName();
	} 

	return javaCodeCommunication(clazz, method, parameterStrings, 
				     beginOfMethod);
    } 

    /**
     * Method declaration
     *
     *
     * @param clazz
     * @param method
     * @param parameters
     * @param beginOfMethod
     *
     * @return
     *
     * @see
     */
    public static String javaCodeCommunication(jass.reflect.Class clazz, 
					       jass.reflect.Method method, 
					       String[] parameters, 
					       boolean beginOfMethod) {
	String javaCode = "";
	String classname = clazz.getName();

	javaCode += CLASSNAME + ".internalAction = true; ";

	if (beginOfMethod) {
	    javaCode += Parameter.CLASSNAME + "[] jassParameters; ";
	} 

	javaCode += "jassParameters = new " + Parameter.CLASSNAME + "[] {";

	for (int iParameter = 0; iParameter < parameters.length; 
		++iParameter) {
	    String parameter = parameters[iParameter];

	    javaCode += "new " + Parameter.CLASSNAME + "(" + parameter + ")";

	    if (iParameter + 1 < parameters.length) {
		javaCode += ", ";
	    } 
	} 

	javaCode += "}; ";
	javaCode += CLASSNAME + ".internalAction = false; ";
	javaCode += CLASSNAME + ".communicate";
	javaCode += "(";
	javaCode += (method.isStatic() ? "null" : "this");
	javaCode += ", " 
		    + Communication.constructorString(clazz.getPackageName(), 
		    clazz.getIdentifier(), method.getIdString(), 
		    beginOfMethod);
	javaCode += ", jassParameters";
	javaCode += ")";

	return javaCode;
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

